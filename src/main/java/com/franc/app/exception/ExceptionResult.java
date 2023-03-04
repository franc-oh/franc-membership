package com.franc.app.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionResult {

    CREATE_BARCODE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "바코드 생성에 실패했습니다."),
    ALREADY_JOIN_MEMBERSHIP(HttpStatus.BAD_REQUEST, "이미 가입한 멤버십입니다."),
    RE_JOIN_NOT_POSSIBLE_WITHDRAWAL(HttpStatus.BAD_REQUEST, "탈퇴 후 하루가 지나야 재가입이 가능합니다."),
    NOT_ACTIVE_ACCOUNT(HttpStatus.BAD_REQUEST, "해당 회원은 현재 정지(탈퇴) 상태 입니다."),
    NOT_FOUND_ACCOUNT(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),

    PARAMETER_NOT_VALID(HttpStatus.BAD_REQUEST, "잘못된 요청 데이터입니다."),
    UNKNOWN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "오류가 발생했습니다. <br/>고객센터(1588-9999)로 문의주세요.");

    private final HttpStatus code;
    private final String message;
}
