package com.saas.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.saas.auth.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
public class Tenant extends BaseEntity {
    
    /**
     * 租户名称
     */
    private String name;
    
    /**
     * 租户编码
     */
    private String code;
    
    /**
     * 租户描述
     */
    private String description;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}