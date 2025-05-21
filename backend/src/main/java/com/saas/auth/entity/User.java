package com.saas.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.saas.auth.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 状态（true:启用；false:禁用）
     */
    private Boolean status;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户的角色ID列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Long> roleIds;

    /**
     * 用户的角色列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Role> roles;
}
