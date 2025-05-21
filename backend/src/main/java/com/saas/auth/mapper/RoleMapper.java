package com.saas.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.auth.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色数据访问接口
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 分页查询角色列表
     * 
     * @param page 分页对象
     * @param keyword 关键词
     * @param status 状态
     * @param tenantId 租户ID
     * @return 角色分页数据
     */
    IPage<Role> selectRolePage(Page<Role> page, @Param("keyword") String keyword,
                               @Param("status") Boolean status, @Param("tenantId") Long tenantId);

    /**
     * 查询用户的角色
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 角色列表
     */
    List<Role> selectRolesByUserId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);

    /**
     * 查询用户的角色ID列表
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}
