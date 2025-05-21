package com.saas.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saas.auth.entity.RolePermission;
import org.springframework.stereotype.Repository;

/**
 * 角色权限关联数据访问接口
 */
@Repository
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}
