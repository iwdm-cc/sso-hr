package com.saas.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.auth.entity.Tenant;
import org.apache.ibatis.annotations.Param;

/**
 * 租户Mapper接口
 */
public interface TenantMapper extends BaseMapper<Tenant> {
    
    /**
     * 分页查询租户列表
     */
    Page<Tenant> selectTenantPage(Page<Tenant> page, @Param("keyword") String keyword, @Param("status") Integer status);
    
    /**
     * 根据租户编码查询租户
     */
    Tenant selectByCode(@Param("code") String code);
}