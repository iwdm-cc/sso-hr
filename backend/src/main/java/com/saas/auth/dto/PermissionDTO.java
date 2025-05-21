package com.saas.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限DTO
 */
@Data
public class PermissionDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    @Size(min = 2, max = 50, message = "权限名称长度必须在2-50个字符之间")
    private String name;

    /**
     * 权限代码
     */
    @NotBlank(message = "权限代码不能为空")
    @Size(min = 2, max = 100, message = "权限代码长度必须在2-100个字符之间")
    private String code;

    /**
     * 资源路径
     */
    private String resourcePath;

    /**
     * 权限类型（MENU:菜单；BUTTON:按钮；API:接口）
     */
    @NotBlank(message = "权限类型不能为空")
    private String type;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 子权限列表
     */
    private List<PermissionDTO> children;
}
