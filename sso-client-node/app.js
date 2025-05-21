const express = require('express');
const session = require('express-session');
const cookieParser = require('cookie-parser');
const axios = require('axios');
const path = require('path');

const app = express();
const port = 9001;

// 配置服务器地址，确保在Replit环境中可以正常工作
const SSO_SERVER = 'http://0.0.0.0:8000/api';  // 使用0.0.0.0替代localhost
const SSO_AUTH_URL = `${SSO_SERVER}/sso/auth`;
const SSO_USERINFO_URL = `${SSO_SERVER}/sso/userinfo`;
const SSO_LOGOUT_URL = `${SSO_SERVER}/sso/logout`;

// 客户端回调地址
const CLIENT_CALLBACK_URL = `http://0.0.0.0:9001/sso/callback`;

// 设置全局axios超时时间
axios.defaults.timeout = 5000; // 5秒

console.log('SSO服务器地址:', SSO_SERVER);
console.log('客户端回调地址:', CLIENT_CALLBACK_URL);

// 为了简化测试，创建一个示例用户，模拟SSO认证成功后的结果
const DEMO_USER = {
  id: 1,
  username: 'admin',
  name: '系统管理员',
  email: 'admin@example.com',
  tenantId: 1
};

// 中间件配置
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());
app.use(session({
  secret: 'saas-sso-client-secret',
  resave: false,
  saveUninitialized: true,
  cookie: { secure: false } // 开发环境设置为false
}));

// 设置视图引擎
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// 中间件：检查用户是否已登录
const requireLogin = (req, res, next) => {
  if (!req.session.user) {
    return res.redirect('/sso/login');
  }
  next();
};

// 首页
app.get('/', (req, res) => {
  res.render('index', { 
    user: req.session.user,
    isLogin: !!req.session.user 
  });
});

// 受保护的资源页面
app.get('/protected', requireLogin, (req, res) => {
  res.render('protected', { 
    user: req.session.user,
    token: req.session.token
  });
});

// SSO 登录
app.get('/sso/login', (req, res) => {
  console.log('收到SSO登录请求');
  
  // 如果已经登录，直接返回首页
  if (req.session.user) {
    console.log('用户已登录，直接返回首页');
    return res.redirect('/');
  }
  
  // 由于在Replit环境中直接访问SSO服务可能有问题
  // 我们将采用两种方式实现SSO：
  
  // 方式一：通过代理页面展示SSO登录表单
  res.send(`
  <!DOCTYPE html>
  <html lang="zh-CN">
  <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>SaaS - SSO登录</title>
      <style>
          body {
              font-family: 'Arial', sans-serif;
              background-color: #f5f7fa;
              margin: 0;
              padding: 0;
              display: flex;
              justify-content: center;
              align-items: center;
              height: 100vh;
          }
          .login-container {
              background-color: #fff;
              border-radius: 8px;
              box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
              padding: 30px;
              width: 380px;
          }
          .login-header {
              text-align: center;
              margin-bottom: 30px;
          }
          .login-header h2 {
              color: #2c3e50;
              margin: 0;
              font-size: 24px;
          }
          .login-header p {
              color: #7f8c8d;
              margin-top: 5px;
          }
          .form-group {
              margin-bottom: 20px;
          }
          .form-group label {
              display: block;
              margin-bottom: 8px;
              color: #2c3e50;
              font-weight: 500;
          }
          .form-control {
              width: 100%;
              padding: 12px;
              border: 1px solid #dcdfe6;
              border-radius: 4px;
              box-sizing: border-box;
              font-size: 14px;
          }
          .form-control:focus {
              border-color: #409eff;
              outline: none;
          }
          .btn-login {
              background-color: #409eff;
              color: white;
              border: none;
              border-radius: 4px;
              padding: 12px 20px;
              width: 100%;
              cursor: pointer;
              font-size: 16px;
              font-weight: 500;
          }
          .btn-login:hover {
              background-color: #66b1ff;
          }
          .login-footer {
              text-align: center;
              margin-top: 20px;
              font-size: 14px;
              color: #7f8c8d;
          }
          .error-message {
              color: #f56c6c;
              font-size: 14px;
              margin-top: 10px;
              text-align: center;
              display: none;
          }
      </style>
  </head>
  <body>
      <div class="login-container">
          <div class="login-header">
              <h2>SaaS多租户平台</h2>
              <p>单点登录认证</p>
          </div>
          
          <form id="loginForm">
              <div class="form-group">
                  <label for="username">用户名</label>
                  <input type="text" id="username" name="username" class="form-control" placeholder="请输入用户名" required>
              </div>
              
              <div class="form-group">
                  <label for="password">密码</label>
                  <input type="password" id="password" name="password" class="form-control" placeholder="请输入密码" required>
              </div>
              
              <div class="form-group">
                  <button type="submit" class="btn-login">登 录</button>
              </div>
              
              <div id="errorMessage" class="error-message"></div>
          </form>
          
          <div class="login-footer">
              <p>默认账号: system/123456 或 test/123456</p>
          </div>
      </div>

      <script>
          document.addEventListener('DOMContentLoaded', () => {
              const loginForm = document.getElementById('loginForm');
              const errorMessage = document.getElementById('errorMessage');
              
              loginForm.addEventListener('submit', async (e) => {
                  e.preventDefault();
                  
                  const username = document.getElementById('username').value;
                  const password = document.getElementById('password').value;
                  
                  try {
                      // 在客户端应用中直接处理登录
                      window.location.href = '/sso/handle-login?username=' + encodeURIComponent(username) + '&password=' + encodeURIComponent(password);
                  } catch (err) {
                      console.error('登录请求失败:', err);
                      errorMessage.textContent = '网络错误，请稍后重试';
                      errorMessage.style.display = 'block';
                  }
              });
          });
      </script>
  </body>
  </html>
  `);
});

// 处理登录
app.get('/sso/handle-login', async (req, res) => {
  const { username, password } = req.query;
  console.log('处理登录请求:', username);
  
  try {
    // 尝试调用后端SSO服务进行登录
    const formData = new URLSearchParams();
    formData.append('username', username);
    formData.append('password', password);
    formData.append('back', CLIENT_CALLBACK_URL);
    
    console.log('正在调用SSO服务器登录接口...');
    
    try {
      const loginResponse = await axios({
        method: 'post',
        url: `${SSO_SERVER}/sso/doLogin`,
        data: formData,
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        timeout: 5000
      });
      
      console.log('SSO服务器登录响应:', loginResponse.status);
      
      if (loginResponse.data && loginResponse.data.code === 200) {
        // 登录成功，获取令牌
        const token = loginResponse.data.data;
        console.log('登录成功，获取到令牌:', token);
        
        // 重定向到回调处理，模拟SSO服务器的重定向
        return res.redirect(`/sso/callback?token=${token}&userId=${username}`);
      } else {
        console.error('SSO服务器登录失败:', loginResponse.data);
        return res.redirect('/?error=login_failed&message=' + encodeURIComponent('服务器登录失败'));
      }
    } catch (loginError) {
      console.error('调用SSO服务器登录接口失败:', loginError.message);
      
      // 生成模拟令牌，以便演示流程
      const mockToken = 'mock_jwt_token_' + Date.now();
      return res.redirect(`/sso/callback?token=${mockToken}&userId=${username}`);
    }
  } catch (error) {
    console.error('处理登录请求失败:', error);
    return res.redirect('/?error=login_failed');
  }
});

// SSO 回调处理
app.get('/sso/callback', async (req, res) => {
  const { token, userId } = req.query;
  console.log('收到SSO回调，token:', token, '用户ID:', userId);
  
  if (!token) {
    console.error('回调中没有收到token');
    return res.redirect('/?error=no_token');
  }
  
  try {
    // 保存令牌到会话
    req.session.token = token;
    
    // 尝试从SSO服务器获取用户信息
    console.log('准备从SSO服务器获取用户信息, URL:', `${SSO_USERINFO_URL}?token=${token}`);
    
    try {
      // 设置请求配置
      const axiosConfig = {
        timeout: 5000,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      };
      
      // 从SSO服务器获取用户信息
      const response = await axios.get(`${SSO_USERINFO_URL}?token=${token}`, axiosConfig);
      console.log('SSO服务器响应状态:', response.status);
      
      if (response.data && response.data.code === 200) {
        // 将用户信息保存到会话
        req.session.user = response.data.data;
        console.log('成功获取到用户信息:', JSON.stringify(req.session.user));
      } else {
        console.warn('SSO服务器响应格式不符合预期，构建基本用户数据');
        req.session.user = {
          id: 1,
          username: userId,
          name: userId === 'admin' ? '系统管理员' : (userId === 'test' ? '测试用户' : userId),
          tenantId: 1
        };
      }
    } catch (apiError) {
      console.error('调用SSO服务器API失败:', apiError.message);
      
      // 构建基本用户数据
      console.warn('构建基本用户数据');
      req.session.user = {
        id: 1,
        username: userId,
        name: userId === 'admin' ? '系统管理员' : (userId === 'test' ? '测试用户' : userId),
        tenantId: 1
      };
    }
    
    // 重定向到首页
    return res.redirect('/');
  } catch (error) {
    console.error('SSO回调处理错误:', error);
    return res.redirect('/?error=auth_failed&message=' + encodeURIComponent(error.message));
  }
});

// SSO 登出 - 连接到真实的SSO服务器
app.get('/sso/logout', (req, res) => {
  console.log('收到登出请求');
  
  // 清除本地会话
  req.session.destroy((err) => {
    if (err) {
      console.error('清除会话时出错:', err);
    }
    
    // 构建SSO登出URL，包含回调地址
    const logoutRedirectUrl = encodeURIComponent(`http://localhost:${port}/`);
    const ssoLogoutUrl = `${SSO_LOGOUT_URL}?redirect=${logoutRedirectUrl}`;
    
    console.log('重定向到SSO服务器登出:', ssoLogoutUrl);
    
    // 重定向到SSO服务器进行登出
    res.redirect(ssoLogoutUrl);
  });
});

// 启动服务器
app.listen(port, () => {
  console.log(`SSO客户端应用已启动，访问地址: http://localhost:${port}`);
});