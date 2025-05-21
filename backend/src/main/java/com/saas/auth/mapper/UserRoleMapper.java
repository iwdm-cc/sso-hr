package com.saas.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saas.auth.entity.UserRole;
import org.springframework.stereotype.Repository;

/**
 * 用户角色关联数据访问接口
 */
@Repository
public interface UserRoleMapper extends BaseMapper<UserRole> {
}
