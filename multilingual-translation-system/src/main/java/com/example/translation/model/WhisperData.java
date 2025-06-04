package com.example.translation.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WhisperData {

    /**
     * Whisper任务池，这里仅是为了方便实现，生产上这里最好是做持久化操作
     */
    public final static Map<String, WhisperTask> whisperTaskStore = new ConcurrentHashMap<>();

}
