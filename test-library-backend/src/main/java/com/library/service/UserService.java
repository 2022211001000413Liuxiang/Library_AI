package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public IPage<User> getUsers(int current, int size, String name, String username) {
        Page<User> page = new Page<>(current, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like("name", name);
        }
        if (StringUtils.hasText(username)) {
            wrapper.like("username", username);
        }
        wrapper.orderByDesc("create_time");
        return userMapper.selectPage(page, wrapper);
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    public boolean save(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus(0);
        return userMapper.insert(user) > 0;
    }

    public boolean update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }

    public boolean delete(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    public Long getTotalCount() {
        return userMapper.selectCount(null);
    }
}
