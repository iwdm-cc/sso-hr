package com.saas.permission.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.saas.permission.dto.UserDTO;
import com.saas.permission.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 获取当前用户信息
     */
    User getCurrentUser();

    /**
     * 分页获取用户列表
     */
    IPage<User> getUsersByPage(String username, String name, Integer status, Integer current, Integer size);

    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);

    /**
     * 创建用户
     */
    User createUser(UserDTO userDTO);

    /**
     * 更新用户
     */
    User updateUser(Long id, UserDTO userDTO);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 获取用户角色
     */
    List<Map<String, Object>> getUserRoles(Long userId);

    /**
     * 分配用户角色
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 根据用户名获取用户
     */
    User getUserByUsername(String username);

    /**
     * 获取所有用户（不分页）
     */
    List<User> getAllUsers();

    /**
     * 查询用户权限码列表
     */
    List<String> getUserPermissionCodes(Long userId);
}
