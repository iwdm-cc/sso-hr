package com.saas.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saas.auth.entity.Permission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限数据访问接口
 */
@Repository
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 查询权限列表
     * 
     * @param type 权限类型
     * @param keyword 关键词
     * @return 权限列表
     */
    List<Permission> selectPermissionList(@Param("type") String type, @Param("keyword") String keyword);

    /**
     * 查询角色的权限
     * 
     * @param roleId 角色ID
     * @param tenantId 租户ID
     * @return 权限列表
     */
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId, @Param("tenantId") Long tenantId);

    /**
     * 查询角色的权限ID列表
     * 
     * @param roleId 角色ID
     * @param tenantId 租户ID
     * @return 权限ID列表
     */
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId, @Param("tenantId") Long tenantId);
}
