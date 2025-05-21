package com.saas.permission.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 权限数据传输对象
 */
@Data
public class PermissionDTO {

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
     * 权限类型（menu-菜单，button-按钮，api-接口）
     */
    @NotBlank(message = "权限类型不能为空")
    private String type;

    /**
     * 资源路径
     */
    @NotBlank(message = "资源路径不能为空")
    private String url;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;
}
