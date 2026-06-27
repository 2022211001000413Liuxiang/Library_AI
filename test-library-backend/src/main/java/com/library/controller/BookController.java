package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.annotation.RequireRole;
import com.library.entity.Book;
import com.library.service.BookService;
import com.library.utils.ExcelImportUtils;
import com.library.utils.OssUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private OssUtils ossUtils;

    @GetMapping
    public Map<String, Object> getBooks(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category) {
        IPage<Book> page = bookService.getBooks(current, size, name, author, category);
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
        result.put("data", bookService.getById(id));
        return result;
    }

    @RequireRole({"admin", "librarian"})
    @PostMapping
    public Map<String, Object> save(@RequestBody Book book) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", bookService.save(book));
        return result;
    }

    @RequireRole({"admin", "librarian"})
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", bookService.update(book));
        return result;
    }

    @RequireRole({"admin", "librarian"})
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", bookService.delete(id));
        return result;
    }

    @RequireRole({"admin", "librarian"})
    @PostMapping("/cover/upload")
    public Map<String, Object> uploadCover(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "文件不能为空");
            return result;
        }
        try {
            String coverUrl = ossUtils.uploadFile(file, "book-covers");
            result.put("success", true);
            result.put("url", coverUrl);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "上传失败: " + e.getMessage());
        }
        return result;
    }

    @RequireRole({"admin", "librarian"})
    @PostMapping("/import")
    public Map<String, Object> importBooks(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "文件不能为空");
            return result;
        }
        try {
            Map<String, Object> parseResult = ExcelImportUtils.parseBooks(file);
            if (!(boolean) parseResult.get("success")) {
                return parseResult;
            }
            @SuppressWarnings("unchecked")
            List<Book> books = (List<Book>) parseResult.get("books");
            int imported = 0;
            for (Book book : books) {
                bookService.save(book);
                imported++;
            }
            result.put("success", true);
            result.put("imported", imported);
            result.put("totalRows", parseResult.get("totalRows"));
            @SuppressWarnings("unchecked")
            List<String> errors = (List<String>) parseResult.get("errors");
            result.put("errors", errors);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "导入失败: " + e.getMessage());
        }
        return result;
    }
}
