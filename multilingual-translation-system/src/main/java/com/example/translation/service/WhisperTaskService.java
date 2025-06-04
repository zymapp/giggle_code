package com.example.translation.service;

import com.example.translation.model.WhisperTask;

import java.io.File;
import java.util.Optional;

/**
 * whisper 服务接口类
 */
public interface WhisperTaskService {
    /**
     * 创建任务
     */
    WhisperTask createTask();

    /**
     * 获取任务
     */
    Optional<WhisperTask> getTask(String taskId);

    /**
     * 取消任务
     */
    boolean cancelTask(String taskId);

    /**
     * 异步执行任务
     */
    void processTaskAsync(WhisperTask whisperTask, File audioFile);
}