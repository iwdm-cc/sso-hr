package com.saas.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.auth.entity.Tenant;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 租户数据访问接口
 */
@Repository
public interface TenantMapper extends BaseMapper<Tenant> {

    /**
     * 分页查询租户列表
     * 
     * @param page 分页对象
     * @param keyword 关键词
     * @param status 状态
     * @return 租户分页数据
     */
    IPage<Tenant> selectTenantPage(Page<Tenant> page, @Param("keyword") String keyword,
                                  @Param("status") Boolean status);
}
