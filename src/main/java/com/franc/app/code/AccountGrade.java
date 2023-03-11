package com.franc.app.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountGrade {

    ADMIN("ADMIN", "관리자"),
    SELLER("SELLER", "판매자"),
    USER("USER", "사용자");

    private final String code;
    private final String name;

    public static AccountGrade of(final String code) {
        return AccountGrade.of(code);
    }


}
