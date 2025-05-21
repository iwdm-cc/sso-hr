package com.saas.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saas.auth.entity.UserRole;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关系Mapper接口
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {
    
    /**
     * 根据用户ID删除用户角色关系
     */
    int deleteByUserId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
    
    /**
     * 根据角色ID删除用户角色关系
     */
    int deleteByRoleId(@Param("roleId") Long roleId, @Param("tenantId") Long tenantId);
    
    /**
     * 批量新增用户角色关系
     */
    int batchInsert(@Param("userId") Long userId, @Param("roleIds") Long[] roleIds, @Param("tenantId") Long tenantId);
}