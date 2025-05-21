package com.saas.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saas.permission.entity.UserRole;
import org.springframework.stereotype.Repository;

/**
 * 用户-角色关联Mapper接口
 */
@Repository
public interface UserRoleMapper extends BaseMapper<UserRole> {

}
