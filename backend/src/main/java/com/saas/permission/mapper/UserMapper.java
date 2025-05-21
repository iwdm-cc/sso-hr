package com.saas.permission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.saas.permission.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户ID获取权限码列表
     */
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    /**
     * 获取最近登录的用户列表
     */
    List<User> selectRecentLoginUsers(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);
}
