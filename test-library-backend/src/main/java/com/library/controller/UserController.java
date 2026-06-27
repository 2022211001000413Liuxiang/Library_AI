package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.annotation.RequireRole;
import com.library.entity.SysUser;
import com.library.mapper.SysUserMapper;
import com.library.service.UserService;
import com.library.utils.OssUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private OssUtils ossUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequireRole({"admin", "librarian"})
    @GetMapping("/users")
    public Map<String, Object> getUsers(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String username) {
        IPage<SysUser> page = userService.getUsers(current, size, name, username);
        Map<String, Object> result = new HashMap<>();
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        result.put("pages", page.getPages());
        return result;
    }

    @RequireRole({"admin"})
    @GetMapping("/users/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", userService.getById(id));
        return result;
    }

    @RequireRole({"admin"})
    @PostMapping("/users")
    public Map<String, Object> save(@RequestBody SysUser user) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", userService.save(user));
        return result;
    }

    @RequireRole({"admin"})
    @PutMapping("/users/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", userService.update(user));
        return result;
    }

    @RequireRole({"admin"})
    @DeleteMapping("/users/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", userService.delete(id));
        return result;
    }

    @GetMapping("/profile")
    public Map<String, Object> getProfile(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            result.put("data", null);
            return result;
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
            result.put("data", user);
        } else {
            result.put("data", null);
        }
        return result;
    }

    @PutMapping("/profile")
    public Map<String, Object> updateProfile(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            result.put("success", false);
            result.put("message", "未登录");
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

    @PutMapping("/password")
    public Map<String, Object> changePassword(@RequestBody Map<String, String> params,
                                              HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

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

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            result.put("success", false);
            result.put("message", "当前密码错误");
            return result;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(java.time.LocalDateTime.now());
        int rows = sysUserMapper.updateById(user);
        result.put("success", rows > 0);
        result.put("message", rows > 0 ? "密码修改成功" : "密码修改失败");
        return result;
    }

    @PostMapping("/avatar/upload")
    public Map<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "文件不能为空");
            return result;
        }

        try {
            String avatarUrl = ossUtils.uploadFile(file, "avatars");
            result.put("success", true);
            result.put("url", avatarUrl);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "上传失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping("/avatar")
    public Map<String, Object> updateAvatar(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = (Long) request.getAttribute("userId");
        String avatarUrl = params.get("avatarUrl") != null ? params.get("avatarUrl").toString() : null;

        if (userId == null || avatarUrl == null) {
            result.put("success", false);
            result.put("message", "参数不完整");
            return result;
        }

        SysUser sysUser = new SysUser();
        sysUser.setId(userId);
        sysUser.setAvatar(avatarUrl);

        int rows = sysUserMapper.updateById(sysUser);
        result.put("success", rows > 0);
        return result;
    }
}
