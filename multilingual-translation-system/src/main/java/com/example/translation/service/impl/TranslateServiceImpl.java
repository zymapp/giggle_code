package com.example.translation.service.impl;

import com.example.translation.model.WhisperTranslateInfo;
import com.example.translation.service.TranslateService;
import com.example.translation.util.GoogleTranslateUtil;
import org.springframework.stereotype.Service;

/**
 * 翻译 服务实现类
 */
@Service
public class TranslateServiceImpl implements TranslateService {

    @Override
    public WhisperTranslateInfo translate(String lang, String sourceTxt) {
        WhisperTranslateInfo result = new WhisperTranslateInfo();
        // 调用谷歌翻译进行翻译
        String TranslateResult = GoogleTranslateUtil.translateText(sourceTxt, "en", lang);
        result.setLangCode(lang);
        result.setTranslatedText(TranslateResult);
        return result;
    }
}
