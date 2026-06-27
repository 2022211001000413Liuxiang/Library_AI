package com.library.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.library.entity.AiConversation;
import com.library.entity.AiMessage;
import com.library.mapper.AiConversationMapper;
import com.library.mapper.AiMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *会话管理，不涉及业务，对接数据库
 */
@Service
public class AiConversationService {

    private static final Logger log = LoggerFactory.getLogger(AiConversationService.class);

    // 用于返回会话信息的内部类
    public static class Conversation {
        public Long id;
        public String sessionId;
        public String summary;
        public Integer messageCount;
    }

    @Autowired
    private AiConversationMapper conversationMapper;

    @Autowired
    private AiMessageMapper messageMapper;

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.endpoint:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String endpoint;

    @Value("${ai.model:qwen-turbo}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final int SUMMARY_THRESHOLD = 10;

    /**
     * 创建新会话
     */
    public AiConversation createConversation(Long userId) {
        AiConversation conversation = new AiConversation();
        conversation.setUserId(userId);
        conversation.setSessionId(UUID.randomUUID().toString());
        conversation.setMessageCount(0);
        conversation.setStatus(1);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        conversationMapper.insert(conversation);
        return conversation;
    }

    /**
     * 获取会话
     */
    public AiConversation getConversation(String sessionId) {
        QueryWrapper<AiConversation> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId);
        return conversationMapper.selectOne(wrapper);
    }

    /**
     * 获取用户的活跃会话
     */
    public AiConversation getActiveConversation(Long userId) {
        QueryWrapper<AiConversation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("status", 1);
        wrapper.orderByDesc("create_time");
        wrapper.last("LIMIT 1");
        return conversationMapper.selectOne(wrapper);
    }

    /**
     * 获取用户的所有会话
     */
    public List<AiConversation> getUserConversations(Long userId) {
        QueryWrapper<AiConversation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return conversationMapper.selectList(wrapper);
    }

    /**
     * 获取历史消息
     */
    public List<AiMessage> getHistory(Long conversationId) {
        QueryWrapper<AiMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("conversation_id", conversationId);
        wrapper.orderByAsc("create_time");
        return messageMapper.selectList(wrapper);
    }

    /**
     * 获取历史消息（文本格式）
     */
    public String getHistoryText(Long conversationId) {
        List<AiMessage> messages = getHistory(conversationId);
        return messages.stream()
                .map(m -> m.getRole().equals("user") ? "用户: " + m.getContent() : "助手: " + m.getContent())
                .collect(Collectors.joining("\n"));
    }

    /**
     * 添加消息
     */
    public void addMessage(Long conversationId, String role, String content) {
        AiMessage message = new AiMessage();
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);

        // 更新会话消息数量
        AiConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            int newCount = conversation.getMessageCount() + 1;
            conversation.setMessageCount(newCount);
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.updateById(conversation);

            // 超过阈值时生成摘要
            if (newCount > SUMMARY_THRESHOLD && conversation.getSummary() == null) {
                generateSummary(conversation);
            }
        }
    }

    /**
     * 生成摘要
     */
    private void generateSummary(AiConversation conversation) {
        List<AiMessage> messages = getHistory(conversation.getId());
        if (messages.isEmpty()) return;

        // 构建摘要 prompt
        StringBuilder sb = new StringBuilder();
        sb.append("请用50字以内总结以下对话的主要内容：\n");
        for (AiMessage msg : messages) {
            sb.append(msg.getRole().equals("user") ? "用户: " : "助手: ");
            sb.append(msg.getContent());
            sb.append("\n");
        }

        try {
            String summary = callAiApi(sb.toString());
            conversation.setSummary(summary);
            conversationMapper.updateById(conversation);

            // 清空详细消息
            QueryWrapper<AiMessage> wrapper = new QueryWrapper<>();
            wrapper.eq("conversation_id", conversation.getId());
            messageMapper.delete(wrapper);

            // 重置消息数量为摘要标记
            conversation.setMessageCount(1);
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.updateById(conversation);
        } catch (Exception e) {
            log.error("生成摘要失败", e);
        }
    }

    /**
     * 结束会话
     */
    public void endConversation(String sessionId) {
        AiConversation conversation = getConversation(sessionId);
        if (conversation != null) {
            conversation.setStatus(0);
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.updateById(conversation);
        }
    }

    /**
     * 调用 AI API 生成摘要
     */
    private String callAiApi(String prompt) throws Exception {
        String url = endpoint + "/chat/completions";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个对话摘要助手，请简洁总结对话内容，不超过50字。");
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        org.springframework.http.HttpEntity<Map<String, Object>> entity =
                new org.springframework.http.HttpEntity<>(requestBody, headers);

        String response = restTemplate.postForObject(url, entity, String.class);

        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");

        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> choice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) choice.get("message");
            return (String) message.get("content");
        }

        return "";
    }
}
