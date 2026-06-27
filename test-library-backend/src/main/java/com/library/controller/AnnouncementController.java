package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.annotation.RequireRole;
import com.library.entity.Announcement;
import com.library.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @RequireRole({"admin"})
    @GetMapping
    public Map<String, Object> getAnnouncements(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) Integer status) {
        IPage<Announcement> page = announcementService.getAnnouncements(current, size, title, priority, status);
        Map<String, Object> result = new HashMap<>();
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        result.put("pages", page.getPages());
        return result;
    }

    @GetMapping("/home")
    public Map<String, Object> getHomeAnnouncements(@RequestParam(defaultValue = "5") int limit) {
        List<Announcement> list = announcementService.getHomeAnnouncements(limit);
        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", announcementService.getById(id));
        return result;
    }

    @RequireRole({"admin"})
    @PostMapping
    public Map<String, Object> save(@RequestBody Announcement announcement) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", announcementService.save(announcement));
        return result;
    }

    @RequireRole({"admin"})
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", announcementService.update(announcement));
        return result;
    }

    @RequireRole({"admin"})
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", announcementService.delete(id));
        return result;
    }
}
