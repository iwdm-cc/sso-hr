package com.saas.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.auth.dto.TenantDTO;
import com.saas.auth.entity.Tenant;
import com.saas.auth.exception.CustomException;
import com.saas.auth.mapper.TenantMapper;
import com.saas.auth.security.TenantContext;
import com.saas.auth.service.TenantService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 租户服务实现类
 */
@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public Map<String, Object> getTenantList(int page, int pageSize, String keyword, Boolean status) {
        Page<Tenant> pageParam = new Page<>(page, pageSize);
        Integer statusValue = (status != null) ? (status ? 1 : 0) : null;
        IPage<Tenant> pageResult = tenantMapper.selectTenantPage(pageParam, keyword, statusValue);
        
        List<TenantDTO> tenantDTOs = pageResult.getRecords().stream().map(tenant -> {
            TenantDTO tenantDTO = new TenantDTO();
            BeanUtils.copyProperties(tenant, tenantDTO);
            return tenantDTO;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", tenantDTOs);
        result.put("total", pageResult.getTotal());
        return result;
    }

    @Override
    public TenantDTO getTenantById(Long id) {
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw new CustomException("租户不存在");
        }
        
        TenantDTO tenantDTO = new TenantDTO();
        BeanUtils.copyProperties(tenant, tenantDTO);
        return tenantDTO;
    }

    @Override
    @Transactional
    public void createTenant(TenantDTO tenantDTO) {
        // 检查租户代码是否已存在
        QueryWrapper<Tenant> wrapper = new QueryWrapper<>();
        wrapper.eq("code", tenantDTO.getCode());
        if (tenantMapper.selectCount(wrapper) > 0) {
            throw new CustomException("租户代码已存在");
        }
        
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(tenantDTO, tenant);
        tenantMapper.insert(tenant);
    }

    @Override
    @Transactional
    public void updateTenant(TenantDTO tenantDTO) {
        Tenant tenant = tenantMapper.selectById(tenantDTO.getId());
        if (tenant == null) {
            throw new CustomException("租户不存在");
        }
        
        // 租户代码不允许修改
        tenantDTO.setCode(tenant.getCode());
        
        BeanUtils.copyProperties(tenantDTO, tenant);
        tenantMapper.updateById(tenant);
    }

    @Override
    @Transactional
    public void deleteTenant(Long id) {
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw new CustomException("租户不存在");
        }
        
        // TODO: 删除租户下的所有数据，包括用户、角色、权限等
        
        tenantMapper.deleteById(id);
    }

    @Override
    public void switchTenant(Long tenantId) {
        Tenant tenant = tenantMapper.selectById(tenantId);
        if (tenant == null) {
            throw new CustomException("租户不存在");
        }
        
        if (tenant.getStatus() != 1) {
            throw new CustomException("租户已禁用");
        }
        
        TenantContext.setCurrentTenant(tenantId);
    }
}
