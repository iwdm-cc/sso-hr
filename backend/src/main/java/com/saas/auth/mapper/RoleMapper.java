package com.saas.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.auth.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 */
public interface RoleMapper extends BaseMapper<Role> {
    
    /**
     * 分页查询角色列表
     */
    Page<Role> selectRolePage(Page<Role> page, @Param("keyword") String keyword, 
                             @Param("status") Integer status, @Param("tenantId") Long tenantId);
    
    /**
     * 查询用户的角色
     */
    List<Role> selectRolesByUserId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
    
    /**
     * 查询用户的角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}