package com.telegrambot.marketplace.enums;

public enum TokenType {

    ACCESS("ACCESS"),
    REFRESH("REFRESH"),
    EMAIL("EMAIL");

    private final String type;

    TokenType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
