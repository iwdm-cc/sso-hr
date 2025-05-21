package com.saas.auth.service;

import com.saas.auth.dto.PermissionDTO;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 获取权限列表
     * 
     * @param type 权限类型
     * @param keyword 关键词
     * @return 权限列表
     */
    List<PermissionDTO> getPermissionList(String type, String keyword);

    /**
     * 根据ID获取权限
     * 
     * @param id 权限ID
     * @return 权限对象
     */
    PermissionDTO getPermissionById(Long id);

    /**
     * 创建权限
     * 
     * @param permissionDTO 权限数据
     */
    void createPermission(PermissionDTO permissionDTO);

    /**
     * 更新权限
     * 
     * @param permissionDTO 权限数据
     */
    void updatePermission(PermissionDTO permissionDTO);

    /**
     * 删除权限
     * 
     * @param id 权限ID
     */
    void deletePermission(Long id);

    /**
     * 获取角色权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<PermissionDTO> getRolePermissions(Long roleId);
}
