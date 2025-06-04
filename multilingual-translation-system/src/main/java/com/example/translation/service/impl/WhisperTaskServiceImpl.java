package com.example.translation.service.impl;

import com.example.translation.coinfig.LangConfig;
import com.example.translation.model.WhisperTaskStatus;
import com.example.translation.model.WhisperTask;
import com.example.translation.model.WhisperTranslateInfo;
import com.example.translation.service.TranslateService;
import com.example.translation.service.WhisperTaskService;
import com.example.translation.util.LLMUtils;
import com.example.translation.util.WhisperClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Optional;

import com.example.translation.model.WhisperData;

/**
 * whisper 服务实现类
 */
@Service
public class WhisperTaskServiceImpl implements WhisperTaskService {

    @Resource
    private WhisperClient whisperClient;

    @Resource
    private TranslateService translateService;

    /**
     * 创建任务
     */
    @Override
    public WhisperTask createTask() {
        WhisperTask task = new WhisperTask();
        WhisperData.whisperTaskStore.put(task.getId(), task);
        return task;
    }

    /**
     * 使用whisperTaskExecutor线程池来执行任务
     */
    @Override
    @Async("whisperTaskExecutor")
    public void processTaskAsync(WhisperTask whisperTask, File audioFile) {
        try {
            // 标识任务处理中，处理中的不可取消
            whisperTask.setStatus(WhisperTaskStatus.PROCESSING);
            whisperTask.setUpdateTime(java.time.LocalDateTime.now());

            // 调用 whisper 服务进行语音识别，并将识别后的内容写入到 task中去，方便接口回查
            String sttResult = whisperClient.recognize(audioFile);
            whisperTask.setOriginalText(sttResult);

            // 调用 chatGpt进行 LLM审核，当然这里可以再提交到新的任务池中去异步执行
            boolean llmResult = LLMUtils.llm(whisperTask.getOriginalText(), sttResult);
            whisperTask.setLlmResult(llmResult);

            // 调用 google进行翻译，这里调用三个语言的进行翻译，当然这里可以再提交到新的任务池中去异步执行
            LangConfig.langs.forEach(lang -> {
                WhisperTranslateInfo whisperTranslateInfo = translateService.translate(lang, sttResult);
                whisperTask.getTranslatedList().add(whisperTranslateInfo);
            });

            // 最后写改完成状态，失败的重新再扫任务池进行新的语音识别流程
            whisperTask.setStatus(WhisperTaskStatus.COMPLETED);
            whisperTask.setErrorMessage("");
        } catch (Exception e) {
            // 标记为失败，由定时任务扫描WhisperTask重新进行语音识别、LLM、翻译处理
            whisperTask.setStatus(WhisperTaskStatus.FAILED);
            whisperTask.setErrorMessage(e.getMessage());
        } finally {
            whisperTask.setUpdateTime(java.time.LocalDateTime.now());
        }
    }

    /**
     * 获取任务
     */
    @Override
    public Optional<WhisperTask> getTask(String taskId) {
        return Optional.ofNullable(WhisperData.whisperTaskStore.get(taskId));
    }

    /**
     * 取消任务
     */
    @Override
    public boolean cancelTask(String taskId) {
        WhisperTask task = WhisperData.whisperTaskStore.get(taskId);
        if (task != null && task.getStatus() == WhisperTaskStatus.PENDING) {
            task.setStatus(WhisperTaskStatus.CANCELLED);
            task.setUpdateTime(java.time.LocalDateTime.now());
            return true;
        }
        return false;
    }
}