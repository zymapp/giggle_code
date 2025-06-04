package com.example.translation.model;

import lombok.Data;

@Data
public class WhisperTranslateInfo {

    // 语言编码码，如zh（中文）、tw（繁体）、jp（日语）
    private String langCode;

    // 内容
    private String translatedText;

}
