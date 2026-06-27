package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.library.entity.SysRole;
import com.library.entity.SysUser;
import com.library.mapper.SysRoleMapper;
import com.library.mapper.SysRolePermissionMapper;
import com.library.mapper.SysUserMapper;
import com.library.mapper.SysUserRoleMapper;
import com.library.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null) {
            result.put("success", false);
            result.put("message", "用户名或密码不能为空");
            return result;
        }

        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.eq("status", 0);
        SysUser sysUser = sysUserMapper.selectOne(wrapper);
//取出加密的盐值对传递过来的密码进行加密再与数据库中的密码匹配
        if (sysUser == null || !passwordEncoder.matches(password, sysUser.getPassword())) {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(sysUser.getId());

        List<SysRole> roles = new ArrayList<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            roles = sysRoleMapper.selectBatchIds(roleIds);
        }

        List<String> roleKeys = roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toList());

        List<String> permissions = sysRolePermissionMapper.selectPermKeysByRoleIds(roleIds);
        if (permissions == null) {
            permissions = new ArrayList<>();
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", sysUser.getId());
        userData.put("username", sysUser.getUsername());
        userData.put("name", sysUser.getName());
        userData.put("gender", sysUser.getGender());
        userData.put("phone", sysUser.getPhone());
        userData.put("email", sysUser.getEmail());
        userData.put("avatar", sysUser.getAvatar());
        userData.put("roles", roleKeys);
        userData.put("permissions", permissions);

        String mainRole = "reader";
        if (roleKeys.contains("admin")) {
            mainRole = "admin";
        } else if (roleKeys.contains("librarian")) {
            mainRole = "librarian";
        }
        String userType = "reader".equals(mainRole) ? "reader" : "staff";

        String token = JwtUtils.generateToken(sysUser.getId(), sysUser.getUsername(), mainRole);

        result.put("success", true);
        result.put("message", "登录成功");
        result.put("userType", userType);
        result.put("role", mainRole);
        result.put("token", token);
        result.put("data", userData);

        return result;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "退出成功");
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        String name = (String) params.get("name");
        Integer gender = params.get("gender") != null ? Integer.valueOf(params.get("gender").toString()) : 0;
        String phone = (String) params.get("phone");
        String email = (String) params.get("email");

        if (username == null || username.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "用户名不能为空");
            return result;
        }
        if (password == null || password.length() < 6) {
            result.put("success", false);
            result.put("message", "密码长度不能少于6位");
            return result;
        }

        QueryWrapper<SysUser> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("username", username);
        SysUser existingUser = sysUserMapper.selectOne(checkWrapper);
        if (existingUser != null) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }

        SysUser newUser = new SysUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setName(name);
        newUser.setGender(gender);
        newUser.setPhone(phone);
        newUser.setEmail(email);
        newUser.setStatus(0);
        newUser.setCreateTime(java.time.LocalDateTime.now());
        newUser.setUpdateTime(java.time.LocalDateTime.now());
        sysUserMapper.insert(newUser);

        sysUserRoleMapper.insertUserRole(newUser.getId(), 3L);

        result.put("success", true);
        result.put("message", "注册成功");
        return result;
    }
}
