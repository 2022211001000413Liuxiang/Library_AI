package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BorrowService {

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private BookMapper bookMapper;

    public IPage<Borrow> getBorrows(int current, int size, String bookName, String userName, Integer status) {
        Page<Borrow> page = new Page<>(current, size);
        // 使用自定义XML查询
        IPage<Borrow> result = borrowMapper.selectBorrowPage(page, bookName, userName, status);
        // 手动设置总数，因为自定义XML查询可能不会自动计算总数
        result.setTotal(borrowMapper.selectCount(new QueryWrapper<Borrow>()));
        return result;
    }

    public IPage<Borrow> getBorrowsByUserId(int current, int size, Long userId, Integer status) {
        Page<Borrow> page = new Page<>(current, size);
        return borrowMapper.selectBorrowPageByUserId(page, userId, status);
    }

    public Borrow getById(Long id) {
        return borrowMapper.selectBorrowById(id);
    }

    @Transactional
    public boolean borrowBook(Borrow borrow) {
        // 检查图书库存
        Book book = bookMapper.selectById(borrow.getBookId());
        if (book == null || book.getStock() <= 0) {
            return false;
        }

        // 创建借阅记录
        borrow.setBorrowDate(LocalDate.now());
        borrow.setDueDate(LocalDate.now().plusDays(30));
        borrow.setStatus(0);
        borrow.setCreateTime(LocalDateTime.now());
        borrow.setUpdateTime(LocalDateTime.now());

        // 减少库存
        book.setStock(book.getStock() - 1);
        if (book.getStock() == 0) {
            book.setStatus(1);
        }
        book.setUpdateTime(LocalDateTime.now());
        bookMapper.updateById(book);

        return borrowMapper.insert(borrow) > 0;
    }

    @Transactional
    public boolean returnBook(Long id) {
        Borrow borrow = borrowMapper.selectBorrowById(id);
        if (borrow == null || borrow.getStatus() == 1) {
            return false;
        }

        // 更新借阅记录
        borrow.setReturnDate(LocalDate.now());
        borrow.setStatus(1);
        borrow.setUpdateTime(LocalDateTime.now());
        borrowMapper.updateById(borrow);

        // 增加库存
        Book book = bookMapper.selectById(borrow.getBookId());
        if (book != null) {
            book.setStock(book.getStock() + 1);
            book.setStatus(0);
            book.setUpdateTime(LocalDateTime.now());
            bookMapper.updateById(book);
        }

        return true;
    }

    public Long getBorrowingCount() {
        QueryWrapper<Borrow> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        return borrowMapper.selectCount(wrapper);
    }

    public Long getReturnedCount() {
        QueryWrapper<Borrow> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        return borrowMapper.selectCount(wrapper);
    }

    public Long getOverdueCount() {
        QueryWrapper<Borrow> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        wrapper.lt("due_date", LocalDate.now());
        return borrowMapper.selectCount(wrapper);
    }

    public Map<String, Long> getBorrowStats() {
        Map<String, Long> stats = new java.util.HashMap<>();
        QueryWrapper<Borrow> wrapperAll = new QueryWrapper<>();
        stats.put("total", borrowMapper.selectCount(wrapperAll));

        QueryWrapper<Borrow> wrapperBorrowing = new QueryWrapper<>();
        wrapperBorrowing.eq("status", 0);
        stats.put("borrowing", borrowMapper.selectCount(wrapperBorrowing));

        QueryWrapper<Borrow> wrapperReturned = new QueryWrapper<>();
        wrapperReturned.eq("status", 1);
        stats.put("returned", borrowMapper.selectCount(wrapperReturned));

        QueryWrapper<Borrow> wrapperOverdue = new QueryWrapper<>();
        wrapperOverdue.eq("status", 0);
        wrapperOverdue.lt("due_date", LocalDate.now());
        stats.put("overdue", borrowMapper.selectCount(wrapperOverdue));

        return stats;
    }

    public Map<String, Long> getBorrowStatsByUserId(Long userId) {
        Map<String, Long> stats = new java.util.HashMap<>();

        QueryWrapper<Borrow> wrapperAll = new QueryWrapper<>();
        wrapperAll.eq("user_id", userId);
        stats.put("total", borrowMapper.selectCount(wrapperAll));

        QueryWrapper<Borrow> wrapperBorrowing = new QueryWrapper<>();
        wrapperBorrowing.eq("user_id", userId).eq("status", 0);
        stats.put("borrowing", borrowMapper.selectCount(wrapperBorrowing));

        QueryWrapper<Borrow> wrapperReturned = new QueryWrapper<>();
        wrapperReturned.eq("user_id", userId).eq("status", 1);
        stats.put("returned", borrowMapper.selectCount(wrapperReturned));

        QueryWrapper<Borrow> wrapperOverdue = new QueryWrapper<>();
        wrapperOverdue.eq("user_id", userId).eq("status", 0);
        wrapperOverdue.lt("due_date", LocalDate.now());
        stats.put("overdue", borrowMapper.selectCount(wrapperOverdue));

        return stats;
    }

    public Long getBorrowingCountByUserId(Long userId) {
        QueryWrapper<Borrow> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("status", 0);
        return borrowMapper.selectCount(wrapper);
    }

    public Long getReturnedCountByUserId(Long userId) {
        QueryWrapper<Borrow> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("status", 1);
        return borrowMapper.selectCount(wrapper);
    }

    public Long getOverdueCountByUserId(Long userId) {
        QueryWrapper<Borrow> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("status", 0);
        wrapper.lt("due_date", LocalDate.now());
        return borrowMapper.selectCount(wrapper);
    }
}
