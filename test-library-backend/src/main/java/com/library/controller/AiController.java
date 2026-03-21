package com.library.controller;

import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.utils.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    @Autowired
    private AiService aiService;

    @Autowired
    private BookMapper bookMapper;

    /**
     * 智能推荐图书
     */
    @PostMapping("/recommend")
    public Map<String, Object> recommend(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String preference = params.get("preference");

        if (preference == null || preference.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入您的阅读偏好");
            return result;
        }

        // 查询所有图书
        List<Book> books = bookMapper.selectList(null);

        // 调用 AI 服务
        return aiService.recommendBooks(preference, books);
    }

    /**
     * 图书咨询
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String question = params.get("question");

        if (question == null || question.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入您的问题");
            return result;
        }

        // 查询所有图书
        List<Book> books = bookMapper.selectList(null);

        // 调用 AI 服务
        return aiService.answerQuestion(question, books);
    }
}
