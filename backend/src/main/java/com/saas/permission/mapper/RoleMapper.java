package com.saas.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saas.permission.entity.Permission;
import com.saas.permission.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据用户ID获取角色列表
     */
    List<Role> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID获取权限列表
     */
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);
}
