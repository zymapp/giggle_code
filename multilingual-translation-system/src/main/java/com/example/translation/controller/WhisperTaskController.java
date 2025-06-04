package com.example.translation.controller;

import com.example.translation.model.WhisperTask;
import com.example.translation.service.WhisperTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * 语音相关任务Controller
 */
@RestController
@RequestMapping("/api/whisper/tasks")
public class WhisperTaskController {

    @Resource
    private WhisperTaskService whisperTaskService;

    /**
     * 创建语音识别任务
     */
    @PostMapping("/create")
    public ResponseEntity<WhisperTask> createTask(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("audio-", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);

        // 创建任务
        WhisperTask task = whisperTaskService.createTask();

        // 将任务提交到线程池中异步去执行识别
        whisperTaskService.processTaskAsync(task, tempFile);
        return ResponseEntity.ok(task);
    }

    /**
     * 语音识别任务查找
     */
    @PostMapping("/{taskId}")
    public ResponseEntity<?> getTask(@PathVariable String taskId) {
        Optional<WhisperTask> task = whisperTaskService.getTask(taskId);
        return task.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 取消识别任务
     */
    @PostMapping("/{taskId}/cancel")
    public ResponseEntity<String> cancelTask(@PathVariable String taskId) {
        boolean cancelled = whisperTaskService.cancelTask(taskId);
        if (cancelled) {
            return ResponseEntity.ok("语音识别任务取消成功");
        } else {
            return ResponseEntity.badRequest().body("语音识别任务已完成不需取消");
        }
    }

}