# SaaS多租户用户权限管理系统

## 项目概述

一个企业级SaaS多租户用户权限管理系统，提供安全、灵活的分布式身份认证解决方案，支持单点登录(SSO)集成和细粒度访问控制。

## 技术栈

- **前端**: Vue.js, Vuex, Vue Router, Element UI
- **后端**: SpringBoot, MyBatis Plus, Node.js, Python Flask
- **数据库**: PostgreSQL
- **认证**: 多租户SSO架构, JWT令牌认证
- **安全**: 细粒度权限管理, 安全登录流程

## 项目结构

```
.
├── backend              # Java SpringBoot后端
├── frontend             # Vue.js前端
├── sso-client-node      # Node.js SSO客户端演示
└── sso-client-python    # Python SSO客户端演示
```

## 功能特性

1. **多租户架构**：支持多租户数据隔离
2. **用户管理**：完整的用户CRUD操作
3. **角色管理**：灵活的RBAC权限模型
4. **权限管理**：细粒度权限控制和分配
5. **单点登录**：统一的身份认证和授权
6. **SSO客户端集成**：支持不同技术栈的客户端应用

## 快速开始

### 后端服务器

```bash
cd backend
./mvnw spring-boot:run
```

### 前端应用

```bash
cd frontend
npm install
npm run serve
```

### Node.js SSO客户端

```bash
cd sso-client-node
npm install
node app.js
```

### Python SSO客户端

```bash
cd sso-client-python
pip install -r requirements.txt
python app.py
```

## 系统架构

1. **后端服务**：运行在端口8000，提供API和SSO服务
2. **前端应用**：运行在端口5000，提供用户界面
3. **Node.js SSO客户端**：运行在端口9001
4. **Python SSO客户端**：运行在端口7000

## 演示账号

- 用户名: zcc, 密码: 123456
- 用户名: test, 密码: 123456

## API接口

系统提供完整的REST API支持多端集成：

- 用户管理API
- 角色管理API
- 权限管理API
- 租户管理API
- SSO认证API

## SSO集成

### SSO服务端

SSO服务端提供以下接口：

- `/api/sso/auth` - SSO认证入口
- `/api/sso/doLogin` - 处理登录请求
- `/api/sso/userinfo` - 获取用户信息
- `/api/sso/logout` - 注销登录

### SSO客户端

系统提供两种不同技术栈的SSO客户端实现，方便集成到不同环境：

1. **Node.js客户端**
   - Express框架实现
   - 完整的SSO流程演示

2. **Python客户端**
   - Flask框架实现
   - 符合SSO规范的集成流程

## 许可证

[MIT](LICENSE)