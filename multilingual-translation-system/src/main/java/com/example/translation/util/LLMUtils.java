package com.example.translation.util;

import com.example.translation.coinfig.SystemConfig;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 使用 LLM 进行语义比较 whisper 识别出来的内容 与文本内容是否相近，这里是用的 chatGpt做 LLM审核
 */
public class LLMUtils {

    private static final String OPENAI_API_KEY = "3x72wgz68znce9el";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private static final RestTemplate restTemplate = new RestTemplate();

    public static boolean llm(String original, String translated) {
        // 是否测试环境
        if (SystemConfig.isDev) {
            return true;
        }

        // 构造 prompt
        String prompt = "原文是：" + original + "\n whisper译文是：" + translated +
                "\n请判断译文是否准确表达了原文意思，只回答“是”或“否”。";

        // 构造请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4");
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    OPENAI_API_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> body = response.getBody();
                if (body != null) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        String content = (String) message.get("content");
                        return content != null && content.trim().contains("是");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("调用 OpenAI 接口失败：" + e.getMessage());
        }

        return false;
    }

}
