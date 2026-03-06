package com.library.controller;

import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin
public class StatsController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private UserService userService;

    @GetMapping
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalBooks", bookService.getTotalCount());
        result.put("borrowedBooks", borrowService.getBorrowingCount());
        result.put("totalUsers", userService.getTotalCount());
        result.put("overdueBooks", borrowService.getOverdueCount());
        return result;
    }
}
