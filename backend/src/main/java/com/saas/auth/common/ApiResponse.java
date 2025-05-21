package com.saas.auth.common;

import lombok.Data;

/**
 * API统一响应体
 * 
 * @param <T> 数据类型
 */
@Data
public class ApiResponse<T> {

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功响应
     * 
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 成功响应
     * 
     * @param data 数据
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }

    /**
     * 失败响应
     * 
     * @param code 状态码
     * @param message 消息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
