package com.example.translation.model;

/**
 * 任务状态
 */
public enum WhisperTaskStatus {
    // 等待识别
    PENDING,
    // 识别中
    PROCESSING,
    // 识别完成
    COMPLETED,
    // 识别失败
    FAILED,
    // 用户取消
    CANCELLED
}