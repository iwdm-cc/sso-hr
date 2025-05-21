package com.saas.auth.service;

import com.saas.auth.dto.RoleDTO;

import java.util.List;
import java.util.Map;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 获取角色列表
     * 
     * @param page 页码
     * @param pageSize 每页记录数
     * @param keyword 关键词
     * @param status 状态
     * @return 角色列表数据
     */
    Map<String, Object> getRoleList(int page, int pageSize, String keyword, Boolean status);

    /**
     * 根据ID获取角色
     * 
     * @param id 角色ID
     * @return 角色对象
     */
    RoleDTO getRoleById(Long id);

    /**
     * 创建角色
     * 
     * @param roleDTO 角色数据
     */
    void createRole(RoleDTO roleDTO);

    /**
     * 更新角色
     * 
     * @param roleDTO 角色数据
     */
    void updateRole(RoleDTO roleDTO);

    /**
     * 删除角色
     * 
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 分配权限
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取用户角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleDTO> getUserRoles(Long userId);

    /**
     * 获取角色权限ID列表
     * 
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);
}
