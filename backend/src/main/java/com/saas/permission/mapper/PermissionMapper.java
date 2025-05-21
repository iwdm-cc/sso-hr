package com.saas.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saas.permission.entity.Permission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限Mapper接口
 */
@Repository
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据角色ID列表获取权限列表
     */
    List<Permission> selectPermissionsByRoleIds(@Param("roleIds") List<Long> roleIds);
}
