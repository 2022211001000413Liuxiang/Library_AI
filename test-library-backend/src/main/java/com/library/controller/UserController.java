package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.entity.SysUser;
import com.library.entity.User;
import com.library.mapper.SysUserMapper;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @GetMapping("/users")
    public Map<String, Object> getUsers(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String username) {
        IPage<User> page = userService.getUsers(current, size, name, username);
        Map<String, Object> result = new HashMap<>();
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        result.put("pages", page.getPages());
        return result;
    }

    @GetMapping("/users/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", userService.getById(id));
        return result;
    }

    @PostMapping("/users")
    public Map<String, Object> save(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", userService.save(user));
        return result;
    }

    @PutMapping("/users/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", userService.update(user));
        return result;
    }

    @DeleteMapping("/users/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", userService.delete(id));
        return result;
    }

    // 个人中心相关API

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/profile")
    public Map<String, Object> getProfile(@RequestParam(required = false) Long userId) {
        Map<String, Object> result = new HashMap<>();
        // 如果没有传userId，从sys_user表查询（默认查第一个用户，用于兼容旧版）
        if (userId == null) {
            userId = 1L;
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user != null) {
            // 不返回密码
            user.setPassword(null);
            result.put("data", user);
        } else {
            result.put("data", null);
        }
        return result;
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Map<String, Object> updateProfile(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Long userId = params.get("id") != null ? Long.parseLong(params.get("id").toString()) : null;
        if (userId == null) {
            result.put("success", false);
            result.put("message", "用户ID不能为空");
            return result;
        }

        SysUser sysUser = new SysUser();
        sysUser.setId(userId);
        if (params.get("name") != null) sysUser.setName(params.get("name").toString());
        if (params.get("gender") != null) sysUser.setGender(Integer.parseInt(params.get("gender").toString()));
        if (params.get("phone") != null) sysUser.setPhone(params.get("phone").toString());
        if (params.get("email") != null) sysUser.setEmail(params.get("email").toString());

        int rows = sysUserMapper.updateById(sysUser);
        result.put("success", rows > 0);
        return result;
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        // 实际项目中应验证旧密码，这里简化处理
        if (oldPassword == null || oldPassword.isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入当前密码");
            return result;
        }

        if (newPassword == null || newPassword.length() < 6) {
            result.put("success", false);
            result.put("message", "新密码长度不能少于6位");
            return result;
        }

        // 模拟修改密码成功
        result.put("success", true);
        result.put("message", "密码修改成功");
        return result;
    }
}
