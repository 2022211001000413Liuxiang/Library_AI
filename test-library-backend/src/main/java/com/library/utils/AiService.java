package com.library.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.service.AiConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI功能业务层，与controller对接，对接阿里云api
 */
@Component
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

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
        log.debug("上下文加载 - conversationId: {}, summary: {}, messageCount: {}", conversation.id, conversation.summary, conversation.messageCount);
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
        promptBuilder.append("2. 书名：《书名》 - 作者：xxx - 推荐理由：xxx\n");
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
        log.debug("上下文加载 - conversationId: {}, summary: {}, messageCount: {}", conversation.id, conversation.summary, conversation.messageCount);
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
     * 图书摘要生成
     */
    public String summarizeBook(String bookName, List<com.library.entity.Book> allBooks, Long userId, String sessionId) {
        AiConversationService.Conversation conversation = getOrCreateConversation(userId, sessionId);

        String bookList = allBooks.stream()
                .map(book -> String.format("书名: %s, 作者: %s, 分类: %s, 描述: %s",
                        book.getName(), book.getAuthor(), book.getCategory(),
                        book.getDescription() != null ? book.getDescription() : ""))
                .collect(Collectors.joining("\n"));

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("你是图书馆的智能助手。\n\n");
        promptBuilder.append("图书馆藏书列表:\n").append(bookList).append("\n\n");
        promptBuilder.append("用户想要了解《").append(bookName).append("》这本书。\n\n");
        promptBuilder.append("请根据藏书信息，为这本书生成一段简洁的内容摘要（150字左右）。\n");
        promptBuilder.append("如果这本书不在藏书中，请如实告知。\n");
        promptBuilder.append("请用中文回复。");

        String response = callAiApi(promptBuilder.toString());

        conversationService.addMessage(conversation.id, "user", "请帮我总结《" + bookName + "》");
        conversationService.addMessage(conversation.id, "assistant", response);

        return response;
    }

    /**
     * 相似图书推荐
     */
    public String recommendSimilar(String bookName, List<com.library.entity.Book> allBooks, Long userId, String sessionId) {
        AiConversationService.Conversation conversation = getOrCreateConversation(userId, sessionId);

        String bookList = allBooks.stream()
                .map(book -> String.format("书名: %s, 作者: %s, 分类: %s",
                        book.getName(), book.getAuthor(), book.getCategory()))
                .collect(Collectors.joining("\n"));

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("你是图书馆的智能推荐助手。\n\n");
        promptBuilder.append("图书馆藏书列表:\n").append(bookList).append("\n\n");
        promptBuilder.append("用户喜欢《").append(bookName).append("》这本书。\n\n");
        promptBuilder.append("请从藏书中推荐3本与该书风格或主题相似的图书，并简要说明相似之处。\n");
        promptBuilder.append("如果找不到相似图书，请推荐同分类的其他图书。\n");
        promptBuilder.append("请用中文回复。推荐格式如下：\n");
        promptBuilder.append("1. 书名：《书名》 - 相似原因：xxx\n");

        String response = callAiApi(promptBuilder.toString());

        conversationService.addMessage(conversation.id, "user", "推荐类似《" + bookName + "》的书");
        conversationService.addMessage(conversation.id, "assistant", response);

        return response;
    }

    /**
     * 调用 AI API
     */
    private String callAiApi(String prompt) {
        String url = endpoint + "/chat/completions";
        log.debug("AI API 调用 - URL: {}, Model: {}, API Key: {}", url, model, apiKey != null && !apiKey.isEmpty() ? "已设置" : "未设置");

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
            log.debug("发送AI请求...");
            String response = restTemplate.postForObject(url, entity, String.class);
            log.debug("AI响应: {}", response);
    //将json格式的响应转换为Map
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");

            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                return (String) message.get("content");
            }
        } catch (Exception e) {
            log.error("AI API 调用失败", e);
        }

        return "抱歉，我暂时无法回答这个问题。";
    }
}
