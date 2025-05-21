package com.saas.permission.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.saas.permission.dto.PermissionDTO;
import com.saas.permission.entity.Permission;

import java.util.List;
import java.util.Map;

/**
 * 权限服务接口
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 分页获取权限列表
     */
    IPage<Permission> getPermissionsByPage(String name, String code, String type, Integer current, Integer size);

    /**
     * 根据ID获取权限
     */
    Permission getPermissionById(Long id);

    /**
     * 创建权限
     */
    Permission createPermission(PermissionDTO permissionDTO);

    /**
     * 更新权限
     */
    Permission updatePermission(Long id, PermissionDTO permissionDTO);

    /**
     * 删除权限
     */
    void deletePermission(Long id);

    /**
     * 获取所有权限（不分页）
     */
    List<Permission> getAllPermissions();

    /**
     * 获取权限树
     */
    List<Map<String, Object>> getPermissionTree();

    /**
     * 根据角色ID列表获取权限列表
     */
    List<Permission> getPermissionsByRoleIds(List<Long> roleIds);
}
