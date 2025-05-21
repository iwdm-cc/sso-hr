package com.saas.permission.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 角色数据传输对象
 */
@Data
public class RoleDTO {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 50, message = "角色名称长度必须在2-50个字符之间")
    private String name;

    /**
     * 角色描述
     */
    @Size(max = 200, message = "角色描述长度不能超过200个字符")
    private String description;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 权限ID列表
     */
    private List<Long> permissionIds;
}
