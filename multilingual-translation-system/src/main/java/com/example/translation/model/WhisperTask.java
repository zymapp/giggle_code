package com.example.translation.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 语音识别任务
 */
@Data
public class WhisperTask {
    // 任务唯一id
    private String id;

    // 任务状态
    private WhisperTaskStatus status;

    // 识别结果
    private String originalText;

    // 识别结果 LLM 审核，true代表结果语义相近，false代表不相近
    private Boolean llmResult;

    // 译文内容列表，如zh（中文）、tw（繁体）、jp（日语）
    private List<WhisperTranslateInfo> translatedList = new ArrayList<>();

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;

    // 识别出错信息
    private String errorMessage;

    public WhisperTask() {
        this.id = UUID.randomUUID().toString();
        this.status = WhisperTaskStatus.PENDING;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

}