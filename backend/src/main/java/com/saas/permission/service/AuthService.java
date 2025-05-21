package com.saas.permission.service;

import com.saas.permission.dto.LoginDTO;
import com.saas.permission.dto.LoginResponseDTO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResponseDTO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 刷新Token
     */
    String refreshToken();

    /**
     * 修改密码
     */
    void changePassword(String oldPassword, String newPassword);
}
