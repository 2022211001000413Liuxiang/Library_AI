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
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

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

        // 从 sys_user 表验证用户
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.eq("password", password);
        wrapper.eq("status", 0);
        SysUser sysUser = sysUserMapper.selectOne(wrapper);

        if (sysUser == null) {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }

        // 查询用户关联的角色ID列表
        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(sysUser.getId());

        // 查询角色信息
        List<SysRole> roles = new ArrayList<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            roles = sysRoleMapper.selectBatchIds(roleIds);
        }

        // 获取角色key列表
        List<String> roleKeys = roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toList());

        // 查询角色关联的权限列表
        List<String> permissions = sysRolePermissionMapper.selectPermKeysByRoleIds(roleIds);
        if (permissions == null) {
            permissions = new ArrayList<>();
        }

        // 构建返回数据
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

        // 确定主要角色（用于userType和role）
        // 角色优先级：admin > librarian > reader
        String mainRole = "reader";
        if (roleKeys.contains("admin")) {
            mainRole = "admin";
        } else if (roleKeys.contains("librarian")) {
            mainRole = "librarian";
        }
        String userType = "reader".equals(mainRole) ? "reader" : "admin";

        // 生成 JWT token
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
}
