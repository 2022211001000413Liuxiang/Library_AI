package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    public IPage<Book> getBooks(int current, int size, String name, String author, String category) {
        Page<Book> page = new Page<>(current, size);
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like("name", name);
        }
        if (StringUtils.hasText(author)) {
            wrapper.like("author", author);
        }
        if (StringUtils.hasText(category)) {
            wrapper.eq("category", category);
        }
        wrapper.orderByDesc("create_time");
        return bookMapper.selectPage(page, wrapper);
    }

    public Book getById(Long id) {
        return bookMapper.selectById(id);
    }

    public boolean save(Book book) {
        book.setCreateTime(LocalDateTime.now());
        book.setUpdateTime(LocalDateTime.now());
        if (book.getStock() > 0) {
            book.setStatus(0);
        }
        return bookMapper.insert(book) > 0;
    }

    public boolean update(Book book) {
        book.setUpdateTime(LocalDateTime.now());
        return bookMapper.updateById(book) > 0;
    }

    public boolean delete(Long id) {
        return bookMapper.deleteById(id) > 0;
    }

    public Long getTotalCount() {
        return bookMapper.selectCount(null);
    }

    public Long getBorrowedCount() {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        return bookMapper.selectCount(wrapper);
    }
}
