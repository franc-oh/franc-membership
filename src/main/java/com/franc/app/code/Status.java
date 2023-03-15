package com.franc.app.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    USE('1', "사용"),
    STOP('9', "정지"),
    WITHDRAWAL('0', "탈퇴");

    private final Character code;
    private final String name;

    public static Status of(final Character code) {
        return Status.of(code);
    }

}
