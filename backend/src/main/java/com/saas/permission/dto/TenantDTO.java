package com.saas.permission.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 租户数据传输对象
 */
@Data
public class TenantDTO {

    /**
     * 租户名称
     */
    @NotBlank(message = "租户名称不能为空")
    @Size(min = 2, max = 50, message = "租户名称长度必须在2-50个字符之间")
    private String name;

    /**
     * 租户描述
     */
    @Size(max = 200, message = "租户描述长度不能超过200个字符")
    private String description;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 管理员用户名
     */
    @NotBlank(message = "管理员用户名不能为空")
    @Size(min = 3, max = 20, message = "管理员用户名长度必须在3-20个字符之间")
    private String adminUsername;

    /**
     * 管理员密码
     */
    @NotBlank(message = "管理员密码不能为空")
    @Size(min = 6, message = "管理员密码长度不能少于6个字符")
    private String adminPassword;

    /**
     * 管理员姓名
     */
    @NotBlank(message = "管理员姓名不能为空")
    private String adminName;

    /**
     * 管理员邮箱
     */
    @NotBlank(message = "管理员邮箱不能为空")
    @Email(message = "管理员邮箱格式不正确")
    private String adminEmail;
}
