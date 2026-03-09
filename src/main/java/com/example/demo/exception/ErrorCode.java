package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS("000", "success"),
    FILE_EMPTY("001", "File is empty"),
    FILE_NOT_ACCEPTED("002", "File is not accepted"),
    FILE_TIME_OUT("003", "File time out"),;

    private final String code;
    private final String message;
}
