package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.entity.Borrow;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/borrows")
@CrossOrigin
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

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
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", borrowService.getById(id));
        return result;
    }

    @PostMapping
    public Map<String, Object> borrow(@RequestBody Borrow borrow) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", borrowService.borrowBook(borrow));
        return result;
    }

    @PutMapping("/{id}/return")
    public Map<String, Object> returnBook(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", borrowService.returnBook(id));
        return result;
    }
}
