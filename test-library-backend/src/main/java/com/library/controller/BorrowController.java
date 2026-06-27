package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.annotation.RequireRole;
import com.library.entity.Borrow;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @RequireRole({"admin", "librarian"})
    @GetMapping
    public Map<String, Object> getBorrows(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String bookName,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) Integer status) {
        IPage<Borrow> page = borrowService.getBorrows(current, size, bookName, userName, status);
        Map<String, Object> result = new HashMap<>();
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        result.put("pages", page.getPages());
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", page.getTotal());
        stats.put("borrowing", borrowService.getBorrowingCount());
        stats.put("returned", borrowService.getReturnedCount());
        stats.put("overdue", borrowService.getOverdueCount());
        result.put("stats", stats);
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", borrowService.getById(id));
        return result;
    }

    @PostMapping
    public Map<String, Object> borrow(@RequestBody Borrow borrow, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", borrowService.borrowBook(borrow));
        return result;
    }

    @RequireRole({"admin", "librarian"})
    @PutMapping("/{id}/return")
    public Map<String, Object> returnBook(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", borrowService.returnBook(id));
        return result;
    }

    @GetMapping("/my")
    public Map<String, Object> getMyBorrows(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<Borrow> page = borrowService.getBorrowsByUserId(current, size, userId, status);
        Map<String, Object> result = new HashMap<>();
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        result.put("current", page.getCurrent());
        result.put("size", page.getSize());
        result.put("pages", page.getPages());
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", page.getTotal());
        stats.put("borrowing", borrowService.getBorrowingCountByUserId(userId));
        stats.put("returned", borrowService.getReturnedCountByUserId(userId));
        stats.put("overdue", borrowService.getOverdueCountByUserId(userId));
        result.put("stats", stats);
        return result;
    }
}
