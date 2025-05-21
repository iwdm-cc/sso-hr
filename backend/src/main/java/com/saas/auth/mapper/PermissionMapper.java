package com.saas.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saas.auth.entity.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限Mapper接口
 */
public interface PermissionMapper extends BaseMapper<Permission> {
    
    /**
     * 查询权限列表
     */
    List<Permission> selectPermissionList(@Param("type") String type, @Param("keyword") String keyword);
    
    /**
     * 查询角色的权限
     */
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId, @Param("tenantId") Long tenantId);
    
    /**
     * 查询角色的权限ID列表
     */
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId, @Param("tenantId") Long tenantId);
}