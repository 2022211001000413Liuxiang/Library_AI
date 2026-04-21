package com.library.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.service.AiConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AiService {

    @Autowired
    private AiConversationService conversationService;

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.endpoint:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String endpoint;

    @Value("${ai.model:qwen-turbo}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 智能推荐图书
     */
    public String recommendBooks(String userPreference, List<com.library.entity.Book> allBooks, Long userId, String sessionId) {
        if (allBooks == null || allBooks.isEmpty()) {
            return "图书馆暂无藏书，无法提供推荐。";
        }

        // 获取或创建会话
        AiConversationService.Conversation conversation = getOrCreateConversation(userId, sessionId);

        // 构建图书信息列表
        String bookList = allBooks.stream()
                .map(book -> String.format("书名: %s, 作者: %s, 分类: %s, 库存: %d",
                        book.getName(), book.getAuthor(), book.getCategory(), book.getStock()))
                .collect(Collectors.joining("\n"));

        // 构建提示词
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("你是图书馆的智能图书推荐助手。请根据用户的偏好推荐图书。\n\n");
        promptBuilder.append("图书馆藏书列表:\n").append(bookList).append("\n\n");

        // 添加历史摘要（如果有）
        System.out.println("=== 上下文加载 ===");
        System.out.println("conversation.id: " + conversation.id);
        System.out.println("conversation.summary: " + conversation.summary);
        System.out.println("conversation.messageCount: " + conversation.messageCount);
        if (conversation.summary != null && !conversation.summary.isEmpty()) {
            promptBuilder.append("对话历史摘要: ").append(conversation.summary).append("\n\n");
        } else if (conversation.messageCount > 0) {
            // 没有摘要时，添加历史消息
            String historyText = conversationService.getHistoryText(conversation.id);
            System.out.println("历史消息: " + historyText);
            if (historyText != null && !historyText.isEmpty()) {
                promptBuilder.append("对话历史:\n").append(historyText).append("\n\n");
            }
        }

        promptBuilder.append("用户偏好: ").append(userPreference).append("\n\n");
        promptBuilder.append("请从以上藏书中推荐5本最符合用户偏好的图书，并简要说明推荐理由。\n");
        promptBuilder.append("请用中文回复。推荐格式如下：\n");
        promptBuilder.append("1. 书名：《书名》 - 作者：xxx - 推荐理由：xxx\n");

        String response = callAiApi(promptBuilder.toString());

        // 保存对话
        conversationService.addMessage(conversation.id, "user", userPreference);
        conversationService.addMessage(conversation.id, "assistant", response);

        return response;
    }

    /**
     * 图书咨询
     */
    public String answerQuestion(String question, List<com.library.entity.Book> allBooks, Long userId, String sessionId) {
        if (allBooks == null || allBooks.isEmpty()) {
            return "图书馆暂无藏书，无法回答您的问题。";
        }

        // 获取或创建会话
        AiConversationService.Conversation conversation = getOrCreateConversation(userId, sessionId);

        // 构建图书信息列表
        String bookList = allBooks.stream()
                .map(book -> String.format("书名: %s, 作者: %s, 分类: %s, 库存: %d, 描述: %s",
                        book.getName(), book.getAuthor(), book.getCategory(),
                        book.getStock(), book.getDescription() != null ? book.getDescription() : ""))
                .collect(Collectors.joining("\n"));

        // 构建提示词
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("你是图书馆的智能助手。请根据图书馆的藏书回答用户的问题。\n\n");
        promptBuilder.append("图书馆藏书列表:\n").append(bookList).append("\n\n");

        // 添加历史摘要（如果有）
        System.out.println("=== 上下文加载 ===");
        System.out.println("conversation.id: " + conversation.id);
        System.out.println("conversation.summary: " + conversation.summary);
        System.out.println("conversation.messageCount: " + conversation.messageCount);
        if (conversation.summary != null && !conversation.summary.isEmpty()) {
            promptBuilder.append("对话历史摘要: ").append(conversation.summary).append("\n\n");
        } else if (conversation.messageCount > 0) {
            // 没有摘要时，添加历史消息
            String historyText = conversationService.getHistoryText(conversation.id);
            System.out.println("历史消息: " + historyText);
            if (historyText != null && !historyText.isEmpty()) {
                promptBuilder.append("对话历史:\n").append(historyText).append("\n\n");
            }
        }

        promptBuilder.append("用户问题: ").append(question).append("\n\n");
        promptBuilder.append("请根据以上藏书信息回答用户的问题。如果用户询问的图书不存在，请如实告知。\n");
        promptBuilder.append("请用中文回复，回复要简洁明了。");

        String response = callAiApi(promptBuilder.toString());

        // 保存对话
        conversationService.addMessage(conversation.id, "user", question);
        conversationService.addMessage(conversation.id, "assistant", response);

        return response;
    }

    /**
     * 获取或创建会话
     */
    private AiConversationService.Conversation getOrCreateConversation(Long userId, String sessionId) {
        AiConversationService.Conversation result = new AiConversationService.Conversation();
        result.sessionId = sessionId;

        if (sessionId != null && !sessionId.isEmpty()) {
            var conv = conversationService.getConversation(sessionId);
            if (conv != null) {
                result.id = conv.getId();
                result.sessionId = conv.getSessionId();
                result.summary = conv.getSummary();
                result.messageCount = conv.getMessageCount();
                return result;
            }
        }

        // 创建新会话
        var newConv = conversationService.createConversation(userId);
        result.id = newConv.getId();
        result.sessionId = newConv.getSessionId();
        result.summary = null;
        result.messageCount = 0;
        return result;
    }

    /**
     * 调用 AI API
     */
    private String callAiApi(String prompt) {
        String url = endpoint + "/chat/completions";
        System.out.println("=== AI API 调用 ===");
        System.out.println("URL: " + url);
        System.out.println("Model: " + model);
        System.out.println("API Key: " + (apiKey != null && !apiKey.isEmpty() ? "已设置" : "未设置"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个友好的图书馆智能助手，擅长推荐图书和回答关于图书馆藏书的问题。请记住用户告诉你的任何个人信息（如名字、喜好等），并在后续对话中适当使用这些信息。");
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

        try {
            System.out.println("发送请求...");
            String response = restTemplate.postForObject(url, entity, String.class);
            System.out.println("响应: " + response);

            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");

            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                return (String) message.get("content");
            }
        } catch (Exception e) {
            System.err.println("AI API 调用失败: " + e.getMessage());
            e.printStackTrace();
        }

        return "抱歉，我暂时无法回答这个问题。";
    }
}
