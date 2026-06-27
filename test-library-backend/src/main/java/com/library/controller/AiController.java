package com.library.controller;

import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.service.AiConversationService;
import com.library.utils.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private AiConversationService conversationService;

    @PostMapping("/session")
    public Map<String, Object> createSession(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = (Long) request.getAttribute("userId");

        var conversation = conversationService.createConversation(userId);
        result.put("success", true);
        result.put("sessionId", conversation.getSessionId());
        result.put("conversationId", conversation.getId());
        return result;
    }

    @GetMapping("/session/{sessionId}")
    public Map<String, Object> getSession(@PathVariable String sessionId) {
        Map<String, Object> result = new HashMap<>();
        var conversation = conversationService.getConversation(sessionId);
        if (conversation == null) {
            result.put("success", false);
            result.put("message", "会话不存在");
            return result;
        }
        result.put("success", true);
        result.put("conversationId", conversation.getId());
        result.put("sessionId", conversation.getSessionId());
        result.put("summary", conversation.getSummary());
        result.put("messageCount", conversation.getMessageCount());
        return result;
    }

    @GetMapping("/history/{sessionId}")
    public Map<String, Object> getHistory(@PathVariable String sessionId) {
        Map<String, Object> result = new HashMap<>();
        var conversation = conversationService.getConversation(sessionId);
        if (conversation == null) {
            result.put("success", false);
            result.put("message", "会话不存在");
            return result;
        }
        var messages = conversationService.getHistory(conversation.getId());
        result.put("success", true);
        result.put("messages", messages);
        result.put("summary", conversation.getSummary());
        return result;
    }

    @PostMapping("/session/end")
    public Map<String, Object> endSession(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String sessionId = params.get("sessionId");
        conversationService.endConversation(sessionId);
        result.put("success", true);
        return result;
    }

    @GetMapping("/sessions")
    public Map<String, Object> getUserSessions(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        Long userId = (Long) request.getAttribute("userId");
        var conversations = conversationService.getUserConversations(userId);
        result.put("success", true);
        result.put("conversations", conversations);
        return result;
    }

    @PostMapping("/recommend")
    public Map<String, Object> recommend(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String preference = (String) params.get("preference");
        Long userId = (Long) request.getAttribute("userId");
        String sessionId = (String) params.get("sessionId");

        if (preference == null || preference.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入您的阅读偏好");
            return result;
        }

        List<Book> books = bookMapper.selectList(null);

        String recommendation = aiService.recommendBooks(preference, books, userId, sessionId);
        result.put("success", true);
        result.put("recommendation", recommendation);

        var conversation = conversationService.getConversation(sessionId);
        if (conversation != null) {
            result.put("summary", conversation.getSummary());
            result.put("messageCount", conversation.getMessageCount());
        }

        return result;
    }

    @PostMapping("/summarize")
    public Map<String, Object> summarize(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String bookName = (String) params.get("bookName");
        Long userId = (Long) request.getAttribute("userId");
        String sessionId = (String) params.get("sessionId");

        if (bookName == null || bookName.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入书名");
            return result;
        }

        List<Book> books = bookMapper.selectList(null);
        String summary = aiService.summarizeBook(bookName, books, userId, sessionId);
        result.put("success", true);
        result.put("summary", summary);

        var conversation = conversationService.getConversation(sessionId);
        if (conversation != null) {
            result.put("conversationSummary", conversation.getSummary());
            result.put("messageCount", conversation.getMessageCount());
        }

        return result;
    }

    @PostMapping("/similar")
    public Map<String, Object> similar(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String bookName = (String) params.get("bookName");
        Long userId = (Long) request.getAttribute("userId");
        String sessionId = (String) params.get("sessionId");

        if (bookName == null || bookName.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入书名");
            return result;
        }

        List<Book> books = bookMapper.selectList(null);
        String recommendation = aiService.recommendSimilar(bookName, books, userId, sessionId);
        result.put("success", true);
        result.put("recommendation", recommendation);

        var conversation = conversationService.getConversation(sessionId);
        if (conversation != null) {
            result.put("conversationSummary", conversation.getSummary());
            result.put("messageCount", conversation.getMessageCount());
        }

        return result;
    }

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String question = (String) params.get("question");
        Long userId = (Long) request.getAttribute("userId");
        String sessionId = (String) params.get("sessionId");

        if (question == null || question.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入您的问题");
            return result;
        }

        List<Book> books = bookMapper.selectList(null);

        String answer = aiService.answerQuestion(question, books, userId, sessionId);
        result.put("success", true);
        result.put("answer", answer);

        var conversation = conversationService.getConversation(sessionId);
        if (conversation != null) {
            result.put("summary", conversation.getSummary());
            result.put("messageCount", conversation.getMessageCount());
        }

        return result;
    }
}
