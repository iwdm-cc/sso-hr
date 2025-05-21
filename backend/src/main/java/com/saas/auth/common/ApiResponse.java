package com.saas.auth.common;

import lombok.Data;

/**
 * API统一响应格式
 */
@Data
public class ApiResponse<T> {
    private static final int SUCCESS_CODE = 200;
    private static final int ERROR_CODE = 500;
    
    private int code;
    private String message;
    private T data;
    
    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 成功响应，无数据
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(SUCCESS_CODE, "操作成功", null);
    }
    
    /**
     * 成功响应，带数据
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SUCCESS_CODE, "操作成功", data);
    }
    
    /**
     * 成功响应，带消息和数据
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(SUCCESS_CODE, message, data);
    }
    
    /**
     * 错误响应，无数据
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ERROR_CODE, message, null);
    }
    
    /**
     * 错误响应，带错误码
     */
    public static <T> ApiResponse<T> error(String message, int code) {
        return new ApiResponse<>(code, message, null);
    }
}