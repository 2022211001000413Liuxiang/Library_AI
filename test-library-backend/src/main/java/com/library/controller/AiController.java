package com.library.controller;

import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.service.AiConversationService;
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

    @Autowired
    private AiConversationService conversationService;

    /**
     * 创建新会话
     */
    @PostMapping("/session")
    public Map<String, Object> createSession(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Long userId = Long.valueOf(params.get("userId").toString());

        var conversation = conversationService.createConversation(userId);
        result.put("success", true);
        result.put("sessionId", conversation.getSessionId());
        result.put("conversationId", conversation.getId());
        return result;
    }

    /**
     * 获取会话信息
     */
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

    /**
     * 获取历史消息
     */
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

    /**
     * 结束会话
     */
    @PostMapping("/session/end")
    public Map<String, Object> endSession(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String sessionId = params.get("sessionId");
        conversationService.endConversation(sessionId);
        result.put("success", true);
        return result;
    }

    /**
     * 获取用户的所有会话列表
     */
    @GetMapping("/sessions")
    public Map<String, Object> getUserSessions(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        var conversations = conversationService.getUserConversations(userId);
        result.put("success", true);
        result.put("conversations", conversations);
        return result;
    }

    /**
     * 智能推荐图书
     */
    @PostMapping("/recommend")
    public Map<String, Object> recommend(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String preference = (String) params.get("preference");
        Long userId = Long.valueOf(params.get("userId").toString());
        String sessionId = (String) params.get("sessionId");

        if (preference == null || preference.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入您的阅读偏好");
            return result;
        }

        // 查询所有图书
        List<Book> books = bookMapper.selectList(null);

        // 调用 AI 服务
        String recommendation = aiService.recommendBooks(preference, books, userId, sessionId);
        result.put("success", true);
        result.put("recommendation", recommendation);

        // 返回更新的会话信息
        var conversation = conversationService.getConversation(sessionId);
        if (conversation != null) {
            result.put("summary", conversation.getSummary());
            result.put("messageCount", conversation.getMessageCount());
        }

        return result;
    }

    /**
     * 图书咨询
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String question = (String) params.get("question");
        Long userId = Long.valueOf(params.get("userId").toString());
        String sessionId = (String) params.get("sessionId");

        if (question == null || question.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "请输入您的问题");
            return result;
        }

        // 查询所有图书
        List<Book> books = bookMapper.selectList(null);

        // 调用 AI 服务
        String answer = aiService.answerQuestion(question, books, userId, sessionId);
        result.put("success", true);
        result.put("answer", answer);

        // 返回更新的会话信息
        var conversation = conversationService.getConversation(sessionId);
        if (conversation != null) {
            result.put("summary", conversation.getSummary());
            result.put("messageCount", conversation.getMessageCount());
        }

        return result;
    }
}
