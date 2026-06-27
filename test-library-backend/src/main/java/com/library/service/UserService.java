package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.SysUser;
import com.library.mapper.SysUserMapper;
import com.library.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public IPage<SysUser> getUsers(int current, int size, String name, String username) {
        Page<SysUser> page = new Page<>(current, size);
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like("name", name);
        }
        if (StringUtils.hasText(username)) {
            wrapper.like("username", username);
        }
        wrapper.orderByDesc("create_time");
        IPage<SysUser> result = sysUserMapper.selectPage(page, wrapper);

        for (SysUser user : result.getRecords()) {
            List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(user.getId());
            user.setRole(getRoleKeyById(roleIds));
        }

        return result;
    }

    public SysUser getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user != null) {
            List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(id);
            user.setRole(getRoleKeyById(roleIds));
        }
        return user;
    }

    @Transactional
    public boolean save(SysUser user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus(0);
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode("123456"));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        sysUserMapper.insert(user);

        Long roleId = getRoleIdByRoleKey(user.getRole());
        if (roleId != null) {
            sysUserRoleMapper.insertUserRole(user.getId(), roleId);
        }

        return true;
    }

    @Transactional
    public boolean update(SysUser user) {
        user.setUpdateTime(LocalDateTime.now());
        // 不允许通过更新接口修改密码
        user.setPassword(null);
        sysUserMapper.updateById(user);

        if (user.getRole() != null) {
            Long roleId = getRoleIdByRoleKey(user.getRole());
            if (roleId != null) {
                sysUserRoleMapper.deleteRoleIdsByUserId(user.getId());
                sysUserRoleMapper.insertUserRole(user.getId(), roleId);
            }
        }

        return true;
    }

    @Transactional
    public boolean delete(Long id) {
        sysUserRoleMapper.deleteRoleIdsByUserId(id);
        sysUserMapper.deleteById(id);
        return true;
    }

    private Long getRoleIdByRoleKey(String roleKey) {
        if (roleKey == null) return 3L;
        switch (roleKey) {
            case "admin": return 1L;
            case "librarian": return 2L;
            case "reader": return 3L;
            default: return 3L;
        }
    }

    private String getRoleKeyById(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return "reader";
        if (roleIds.contains(1L)) return "admin";
        if (roleIds.contains(2L)) return "librarian";
        return "reader";
    }

    public Long getTotalCount() {
        return sysUserMapper.selectCount(null);
    }
}
