package com.library.controller;

import com.library.mapper.BookMapper;
import com.library.mapper.BorrowMapper;
import com.library.service.BookService;
import com.library.service.BorrowService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private UserService userService;

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private BookMapper bookMapper;

    @GetMapping
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalBooks", bookService.getTotalCount());
        result.put("borrowedBooks", borrowService.getBorrowingCount());
        result.put("totalUsers", userService.getTotalCount());
        result.put("overdueBooks", borrowService.getOverdueCount());

        // 月度借阅趋势（近6个月）
        result.put("monthlyTrend", borrowMapper.selectMonthlyTrend(6));

        // 热门图书 Top 5
        result.put("topBooks", borrowMapper.selectTopBooks(5));

        // 图书分类分布
        result.put("categoryDistribution", bookMapper.selectCategoryDistribution());

        return result;
    }
}
