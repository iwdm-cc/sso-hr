package com.saas.permission.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录数据传输对象
 */
@Data
public class LoginDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 租户ID
     */
    private Long tenantId;
}
