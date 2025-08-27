package com.mehatronics.axle_load.domain.entities.enums;

public enum AppLanguage {
    EN("en"),
    RU("ru"),
    BE("be"),
    FR("fr");

    private final String code;

    AppLanguage(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AppLanguage fromCode(String code) {
        for (AppLanguage lang : values()) {
            if (lang.code.equals(code)) return lang;
        }
        return EN;
    }
}