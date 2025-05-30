package com.mehatronics.axle_load.entities.enums;

public enum AppLanguage {
    EN("en"),
    RU("ru"),
    UZ("uz");

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
