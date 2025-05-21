package com.saas.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 租户实体类
 */
@Data
@TableName("sys_tenant")
public class Tenant {

    /**
     * 租户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户名称
     */
    private String name;

    /**
     * 租户描述
     */
    private String description;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 逻辑删除标识（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer deleted;
}
