package com.saas.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.auth.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询用户列表
     * 
     * @param page 分页对象
     * @param keyword 关键词
     * @param status 状态
     * @param tenantId 租户ID
     * @return 用户分页数据
     */
    IPage<User> selectUserPage(Page<User> page, @Param("keyword") String keyword,
                              @Param("status") Boolean status, @Param("tenantId") Long tenantId);

    /**
     * 根据用户名和租户ID查询用户
     * 
     * @param username 用户名
     * @param tenantId 租户ID
     * @return 用户对象
     */
    User selectByUsernameAndTenantId(@Param("username") String username, @Param("tenantId") Long tenantId);

    /**
     * 获取用户的所有权限代码
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 权限代码列表
     */
    List<String> selectUserPermissions(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}
