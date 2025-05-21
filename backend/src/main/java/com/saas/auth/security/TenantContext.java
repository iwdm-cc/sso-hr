package com.saas.auth.security;

/**
 * 租户上下文，用于存储当前线程的租户ID
 */
public class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * 获取当前租户ID
     * 
     * @return 租户ID
     */
    public static Long getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    /**
     * 设置当前租户ID
     * 
     * @param tenantId 租户ID
     */
    public static void setCurrentTenant(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * 设置当前租户ID
     * 
     * @param tenantId 租户ID字符串
     */
    public static void setCurrentTenant(String tenantId) {
        CURRENT_TENANT.set(Long.valueOf(tenantId));
    }

    /**
     * 清除当前租户ID
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
