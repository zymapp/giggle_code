package com.example.translation.util;

import com.example.translation.coinfig.SystemConfig;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

/**
 * 谷歌翻译，主要是将 whisper 识别出来的文本内容翻译成各种语言
 */
public class GoogleTranslateUtil {

    private static final Translate translate;

    static {
        // 使用 GOOGLE_APPLICATION_CREDENTIALS 环境变量指定服务账号 JSON 路径
        // 或者手动加载凭据：TranslateOptions.newBuilder().setCredentials(...).build()
        translate = TranslateOptions.getDefaultInstance().getService();
    }

    /**
     * 翻译文本
     *
     * @param text       要翻译的文本
     * @param sourceLang 源语言代码，如 "zh"
     * @param targetLang 目标语言代码，如 "en"
     * @return 翻译后的文本
     */
    public static String translateText(String text, String sourceLang, String targetLang) {
        // 是否测试环境
        if (SystemConfig.isDev) {
            return "";
        }

        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage(sourceLang),
                Translate.TranslateOption.targetLanguage(targetLang),
                Translate.TranslateOption.format("text")
        );
        return translation.getTranslatedText();
    }

    public static void main(String[] args) {
        String text = "你好，世界！";
        String translated = translateText(text, "zh", "en");
        System.out.println("翻译结果：" + translated);
    }

}
