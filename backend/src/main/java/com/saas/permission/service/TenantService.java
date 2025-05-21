package com.saas.permission.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.saas.permission.dto.TenantDTO;
import com.saas.permission.entity.Tenant;

import java.util.List;

/**
 * 租户服务接口
 */
public interface TenantService extends IService<Tenant> {

    /**
     * 分页获取租户列表
     */
    IPage<Tenant> getTenantsByPage(String name, Integer status, Integer current, Integer size);

    /**
     * 根据ID获取租户
     */
    Tenant getTenantById(Long id);

    /**
     * 创建租户
     */
    Tenant createTenant(TenantDTO tenantDTO);

    /**
     * 更新租户
     */
    Tenant updateTenant(Long id, TenantDTO tenantDTO);

    /**
     * 删除租户
     */
    void deleteTenant(Long id);

    /**
     * 获取所有租户（不分页）
     */
    List<Tenant> getAllTenants();

    /**
     * 初始化租户数据
     */
    void initTenantData(Long tenantId);
}
