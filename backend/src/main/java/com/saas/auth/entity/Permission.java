package com.saas.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.saas.auth.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 权限实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BaseEntity {

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限代码
     */
    private String code;

    /**
     * 资源路径
     */
    private String resourcePath;

    /**
     * 权限类型（MENU:菜单；BUTTON:按钮；API:接口）
     */
    private String type;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 子权限列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Permission> children;
}
