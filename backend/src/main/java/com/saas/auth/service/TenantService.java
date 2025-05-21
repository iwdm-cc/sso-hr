package com.saas.auth.service;

import com.saas.auth.dto.TenantDTO;

import java.util.Map;

/**
 * 租户服务接口
 */
public interface TenantService {

    /**
     * 获取租户列表
     * 
     * @param page 页码
     * @param pageSize 每页记录数
     * @param keyword 关键词
     * @param status 状态
     * @return 租户列表数据
     */
    Map<String, Object> getTenantList(int page, int pageSize, String keyword, Boolean status);

    /**
     * 根据ID获取租户
     * 
     * @param id 租户ID
     * @return 租户对象
     */
    TenantDTO getTenantById(Long id);

    /**
     * 创建租户
     * 
     * @param tenantDTO 租户数据
     */
    void createTenant(TenantDTO tenantDTO);

    /**
     * 更新租户
     * 
     * @param tenantDTO 租户数据
     */
    void updateTenant(TenantDTO tenantDTO);

    /**
     * 删除租户
     * 
     * @param id 租户ID
     */
    void deleteTenant(Long id);

    /**
     * 切换租户
     * 
     * @param tenantId 租户ID
     */
    void switchTenant(Long tenantId);
}
