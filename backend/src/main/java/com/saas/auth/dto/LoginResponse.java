package com.saas.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 租户名称
     */
    private String tenantName;
    
    /**
     * 权限列表
     */
    private List<String> permissions;
    
    /**
     * JWT令牌
     */
    private String token;
}