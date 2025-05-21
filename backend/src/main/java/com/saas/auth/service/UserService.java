package com.saas.auth.service;

import com.saas.auth.dto.UserDTO;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名和租户ID获取用户
     * 
     * @param username 用户名
     * @param tenantId 租户ID
     * @return 用户对象
     */
    UserDTO getUserByUsername(String username, Long tenantId);

    /**
     * 获取用户列表
     * 
     * @param page 页码
     * @param pageSize 每页记录数
     * @param keyword 关键词
     * @param status 状态
     * @return 用户列表数据
     */
    Map<String, Object> getUserList(int page, int pageSize, String keyword, Boolean status);

    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户对象
     */
    UserDTO getUserById(Long id);

    /**
     * 创建用户
     * 
     * @param userDTO 用户数据
     */
    void createUser(UserDTO userDTO);

    /**
     * 更新用户
     * 
     * @param userDTO 用户数据
     */
    void updateUser(UserDTO userDTO);

    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 分配角色
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取当前用户信息
     * 
     * @return 用户信息
     */
    Map<String, Object> getCurrentUserInfo();

    /**
     * 获取用户权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 设置当前租户
     * 
     * @param tenantId 租户ID
     */
    void setCurrentTenant(Long tenantId);
}
