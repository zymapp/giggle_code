package com.example.translation.coinfig;

import java.util.ArrayList;
import java.util.List;

public class LangConfig {

    /**
     * 定义语句列表
     */
    public final static List<String> langs;
    static {
        langs = new ArrayList<>();
        langs.add("zh");
        langs.add("jp");
        langs.add("tw");
    }

}
