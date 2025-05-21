package com.saas.permission.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.saas.permission.dto.RoleDTO;
import com.saas.permission.entity.Role;

import java.util.List;
import java.util.Map;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {

    /**
     * 分页获取角色列表
     */
    IPage<Role> getRolesByPage(String name, Integer status, Integer current, Integer size);

    /**
     * 根据ID获取角色
     */
    Role getRoleById(Long id);

    /**
     * 创建角色
     */
    Role createRole(RoleDTO roleDTO);

    /**
     * 更新角色
     */
    Role updateRole(Long id, RoleDTO roleDTO);

    /**
     * 删除角色
     */
    void deleteRole(Long id);

    /**
     * 获取角色权限
     */
    List<Map<String, Object>> getRolePermissions(Long roleId);

    /**
     * 分配角色权限
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取所有角色（不分页）
     */
    List<Role> getAllRoles();

    /**
     * 根据用户ID获取角色列表
     */
    List<Role> getRolesByUserId(Long userId);
}
