package com.saas.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.saas.auth.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态（true:启用；false:禁用）
     */
    private Boolean status;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 角色的权限ID列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Long> permissionIds;

    /**
     * 角色的权限列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Permission> permissions;
}
