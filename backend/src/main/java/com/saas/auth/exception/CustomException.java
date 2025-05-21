package com.saas.auth.exception;

import lombok.Getter;

/**
 * 自定义异常
 */
@Getter
public class CustomException extends RuntimeException {
    private final int code;
    
    public CustomException(String message) {
        super(message);
        this.code = 500;
    }
    
    public CustomException(String message, int code) {
        super(message);
        this.code = code;
    }
}