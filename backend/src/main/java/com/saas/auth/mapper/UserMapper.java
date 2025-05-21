package com.saas.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.auth.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 分页查询用户列表
     */
    Page<User> selectUserPage(Page<User> page, @Param("keyword") String keyword, 
                             @Param("status") Integer status, @Param("tenantId") Long tenantId);
    
    /**
     * 根据用户名和租户ID查询用户
     */
    User selectByUsernameAndTenantId(@Param("username") String username, @Param("tenantId") Long tenantId);
    
    /**
     * 获取用户的所有权限代码
     */
    List<String> selectUserPermissions(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}