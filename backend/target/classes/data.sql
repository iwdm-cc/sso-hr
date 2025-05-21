-- 初始租户数据
INSERT INTO sys_tenant (id, name, code, description, status, create_time) 
VALUES (1, '系统管理租户', 'system', '系统默认租户', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_tenant (id, name, code, description, status, create_time) 
VALUES (2, '测试租户', 'test', '用于测试的租户', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 初始用户数据 (密码: 123456 -> BCrypt加密后)
INSERT INTO sys_user (id, username, password, name, email, status, tenant_id, create_time) 
VALUES (1, 'admin', '$2a$10$OG5tG0aM9QSjsoLYRzwOdeiR5wFl/JvKbGq8.j39tAztcpXc9ZMR2', '系统管理员', 'admin@example.com', 1, 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_user (id, username, password, name, email, status, tenant_id, create_time) 
VALUES (2, 'test', '$2a$10$OG5tG0aM9QSjsoLYRzwOdeiR5wFl/JvKbGq8.j39tAztcpXc9ZMR2', '测试用户', 'test@example.com', 1, 2, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 初始角色数据
INSERT INTO sys_role (id, name, code, description, status, tenant_id, create_time) 
VALUES (1, '超级管理员', 'ROLE_SUPER_ADMIN', '拥有所有权限', 1, 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_role (id, name, code, description, status, tenant_id, create_time) 
VALUES (2, '管理员', 'ROLE_ADMIN', '管理员角色', 1, 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_role (id, name, code, description, status, tenant_id, create_time) 
VALUES (3, '测试角色', 'ROLE_TEST', '测试角色', 1, 2, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 初始权限数据
-- 菜单权限
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, icon, sort, create_time) 
VALUES (1, '系统管理', 'system:view', 'menu', 0, '/system', 'el-icon-setting', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, icon, sort, create_time) 
VALUES (2, '用户管理', 'user:view', 'menu', 1, '/system/user', 'el-icon-user', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, icon, sort, create_time) 
VALUES (3, '角色管理', 'role:view', 'menu', 1, '/system/role', 'el-icon-s-check', 2, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, icon, sort, create_time) 
VALUES (4, '权限管理', 'permission:view', 'menu', 1, '/system/permission', 'el-icon-lock', 3, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, icon, sort, create_time) 
VALUES (5, '租户管理', 'tenant:view', 'menu', 1, '/system/tenant', 'el-icon-office-building', 4, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 按钮权限
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (6, '用户新增', 'user:add', 'button', 2, '', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (7, '用户编辑', 'user:edit', 'button', 2, '', 2, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (8, '用户删除', 'user:delete', 'button', 2, '', 3, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (9, '角色新增', 'role:add', 'button', 3, '', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (10, '角色编辑', 'role:edit', 'button', 3, '', 2, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (11, '角色删除', 'role:delete', 'button', 3, '', 3, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (12, '权限新增', 'permission:add', 'button', 4, '', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (13, '权限编辑', 'permission:edit', 'button', 4, '', 2, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (14, '权限删除', 'permission:delete', 'button', 4, '', 3, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (15, '租户新增', 'tenant:add', 'button', 5, '', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (16, '租户编辑', 'tenant:edit', 'button', 5, '', 2, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (17, '租户删除', 'tenant:delete', 'button', 5, '', 3, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- API权限
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (18, '用户列表接口', 'api:user:list', 'api', 2, '/api/users', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (19, '角色列表接口', 'api:role:list', 'api', 3, '/api/roles', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (20, '权限列表接口', 'api:permission:list', 'api', 4, '/api/permissions', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
INSERT INTO sys_permission (id, name, code, type, parent_id, resource_path, sort, create_time) 
VALUES (21, '租户列表接口', 'api:tenant:list', 'api', 5, '/api/tenants', 1, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 用户角色关系
INSERT INTO sys_user_role (user_id, role_id, tenant_id, create_time) 
VALUES (1, 1, 1, CURRENT_TIMESTAMP)
ON CONFLICT (user_id, role_id, tenant_id) DO NOTHING;
INSERT INTO sys_user_role (user_id, role_id, tenant_id, create_time) 
VALUES (2, 3, 2, CURRENT_TIMESTAMP)
ON CONFLICT (user_id, role_id, tenant_id) DO NOTHING;

-- 角色权限关系 (超级管理员拥有所有权限)
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 1, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 2, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 3, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 4, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 5, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 6, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 7, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 8, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 9, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 10, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 11, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 12, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 13, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 14, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 15, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 16, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 17, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 18, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 19, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 20, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (1, 21, 1, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;

-- 测试角色拥有查看权限
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (3, 1, 2, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (3, 2, 2, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (3, 3, 2, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (3, 4, 2, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (3, 18, 2, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (3, 19, 2, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;
INSERT INTO sys_role_permission (role_id, permission_id, tenant_id, create_time) 
VALUES (3, 20, 2, CURRENT_TIMESTAMP)
ON CONFLICT (role_id, permission_id, tenant_id) DO NOTHING;