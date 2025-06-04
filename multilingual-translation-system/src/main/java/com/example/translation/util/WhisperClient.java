package com.example.translation.util;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * whisper 语音文件识别工具类
 */
@Component
public class WhisperClient {

    private static final String WHISPER_URL = "http://119.45.168.124:8000/transcribe/";
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 调用 whisper 接口做语音文件识别
     */
    public String recognize(File audioFile) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(audioFile));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(WHISPER_URL, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Whisper service failed: " + response.getStatusCode());
        }
    }
}