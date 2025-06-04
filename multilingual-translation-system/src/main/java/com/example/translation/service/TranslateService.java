package com.example.translation.service;

import com.example.translation.model.WhisperTranslateInfo;

/**
 * 翻译服务接口类
 */
public interface TranslateService {

    WhisperTranslateInfo translate(String lang, String sourceTxt);

}
