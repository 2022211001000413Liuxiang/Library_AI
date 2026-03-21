package com.library.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.entity.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AiService {

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
    public Map<String, Object> recommendBooks(String userPreference, List<Book> allBooks) {
        Map<String, Object> result = new HashMap<>();

        if (allBooks == null || allBooks.isEmpty()) {
            result.put("success", false);
            result.put("message", "图书馆暂无藏书");
            return result;
        }

        // 构建图书信息列表
        String bookList = allBooks.stream()
                .map(book -> String.format("书名: %s, 作者: %s, 分类: %s, 库存: %d",
                        book.getName(), book.getAuthor(), book.getCategory(), book.getStock()))
                .collect(Collectors.joining("\n"));

        String prompt = String.format("你是图书馆的智能图书推荐助手。请根据用户的偏好推荐图书。\n\n" +
                "图书馆藏书列表:\n%s\n\n" +
                "用户偏好: %s\n\n" +
                "请从以上藏书中推荐5本最符合用户偏好的图书，并简要说明推荐理由。\n" +
                "请用中文回复。推荐格式如下：\n" +
                "1. 书名：《书名》 - 作者：xxx - 推荐理由：xxx\n" +
                "2. 书名：《书名》 - 作者：xxx - 推荐理由：xxx\n" +
                "...", bookList, userPreference);

        try {
            String aiResponse = callAiApi(prompt);
            result.put("success", true);
            result.put("recommendation", aiResponse);
            result.put("books", allBooks.stream().limit(10).collect(Collectors.toList()));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "AI 推荐失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 图书咨询
     */
    public Map<String, Object> answerQuestion(String question, List<Book> allBooks) {
        Map<String, Object> result = new HashMap<>();

        if (allBooks == null || allBooks.isEmpty()) {
            result.put("success", false);
            result.put("message", "图书馆暂无藏书");
            return result;
        }

        // 构建图书信息列表
        String bookList = allBooks.stream()
                .map(book -> String.format("书名: %s, 作者: %s, 分类: %s, 库存: %d, 描述: %s",
                        book.getName(), book.getAuthor(), book.getCategory(),
                        book.getStock(), book.getDescription() != null ? book.getDescription() : ""))
                .collect(Collectors.joining("\n"));

        String prompt = String.format("你是图书馆的智能助手。请根据图书馆的藏书回答用户的问题。\n\n" +
                "图书馆藏书列表:\n%s\n\n" +
                "用户问题: %s\n\n" +
                "请根据以上藏书信息回答用户的问题。如果用户询问的图书不存在，请如实告知。\n" +
                "请用中文回复，回复要简洁明了。", bookList, question);

        try {
            String aiResponse = callAiApi(prompt);
            result.put("success", true);
            result.put("answer", aiResponse);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "AI 回答失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 调用 AI API
     */
    private String callAiApi(String prompt) throws Exception {
        String url = endpoint + "/chat/completions";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个友好的图书馆智能助手，擅长推荐图书和回答关于图书馆藏书的问题。");
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        // 设置请求头
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        org.springframework.http.HttpEntity<Map<String, Object>> entity =
                new org.springframework.http.HttpEntity<>(requestBody, headers);

        String response = restTemplate.postForObject(url, entity, String.class);

        // 解析响应
        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");

        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> choice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) choice.get("message");
            return (String) message.get("content");
        }

        return "抱歉，我暂时无法回答这个问题。";
    }
}
