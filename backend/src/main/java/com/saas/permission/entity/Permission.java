package com.saas.permission.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 权限实体类
 */
@Data
@TableName("sys_permission")
public class Permission {

    /**
     * 权限ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限代码
     */
    private String code;

    /**
     * 权限类型（menu-菜单，button-按钮，api-接口）
     */
    private String type;

    /**
     * 资源路径
     */
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
    private Integer sort;

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
