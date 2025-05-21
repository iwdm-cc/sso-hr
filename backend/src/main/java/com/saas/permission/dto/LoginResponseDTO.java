package com.saas.permission.dto;

import com.saas.permission.entity.User;
import lombok.Data;

/**
 * 登录响应数据传输对象
 */
@Data
public class LoginResponseDTO {

    /**
     * JWT token
     */
    private String token;

    /**
     * 用户信息
     */
    private User userInfo;

    /**
     * 构造方法
     */
    public LoginResponseDTO(String token, User userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }
}
