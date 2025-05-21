package com.saas.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saas.auth.entity.RolePermission;
import org.apache.ibatis.annotations.Param;

/**
 * 角色权限关系Mapper接口
 */
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    
    /**
     * 根据角色ID删除角色权限关系
     */
    int deleteByRoleId(@Param("roleId") Long roleId, @Param("tenantId") Long tenantId);
    
    /**
     * 根据权限ID删除角色权限关系
     */
    int deleteByPermissionId(@Param("permissionId") Long permissionId);
    
    /**
     * 批量新增角色权限关系
     */
    int batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") Long[] permissionIds, @Param("tenantId") Long tenantId);
}