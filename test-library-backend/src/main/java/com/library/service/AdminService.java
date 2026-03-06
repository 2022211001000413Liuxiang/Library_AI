package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.library.entity.Admin;
import com.library.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    public Admin login(String username, String password) {
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.eq("password", password);
        wrapper.eq("status", 0);
        Admin admin = adminMapper.selectOne(wrapper);

        // 更新最后登录时间
        if (admin != null) {
            admin.setLastLoginTime(LocalDateTime.now());
            adminMapper.updateById(admin);
        }
        return admin;
    }

    public Admin getById(Long id) {
        return adminMapper.selectById(id);
    }

    public Admin getByUsername(String username) {
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return adminMapper.selectOne(wrapper);
    }
}
