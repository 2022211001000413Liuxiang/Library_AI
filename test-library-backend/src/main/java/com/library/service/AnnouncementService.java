package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Announcement;
import com.library.mapper.AnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    public Page<Announcement> getAnnouncements(int current, int size, String title, Integer priority) {
        Page<Announcement> page = new Page<>(current, size);
        QueryWrapper<Announcement> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        if (StringUtils.hasText(title)) {
            wrapper.like("title", title);
        }
        if (priority != null) {
            wrapper.eq("priority", priority);
        }
        wrapper.orderByDesc("priority").orderByDesc("publish_time");
        return announcementMapper.selectPage(page, wrapper);
    }

    public List<Announcement> getHomeAnnouncements(int limit) {
        QueryWrapper<Announcement> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1)
               .orderByDesc("priority")
               .orderByDesc("publish_time")
               .last("LIMIT " + limit);
        return announcementMapper.selectList(wrapper);
    }

    public Announcement getById(Long id) {
        return announcementMapper.selectById(id);
    }

    public boolean save(Announcement announcement) {
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        if (announcement.getPublishTime() == null) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        if (announcement.getStatus() == null) {
            announcement.setStatus(1);
        }
        return announcementMapper.insert(announcement) > 0;
    }

    public boolean update(Announcement announcement) {
        announcement.setUpdateTime(LocalDateTime.now());
        return announcementMapper.updateById(announcement) > 0;
    }

    public boolean delete(Long id) {
        return announcementMapper.deleteById(id) > 0;
    }
}
