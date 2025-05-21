const express = require('express');
const session = require('express-session');
const cookieParser = require('cookie-parser');
const axios = require('axios');
const path = require('path');

const app = express();
const port = 9001;

// 使用固定地址配置
const SSO_SERVER = 'http://localhost:8000/api';
const SSO_AUTH_URL = `${SSO_SERVER}/sso/auth`;
const SSO_USERINFO_URL = `${SSO_SERVER}/sso/userinfo`;
const SSO_LOGOUT_URL = `${SSO_SERVER}/sso/logout`;

// 客户端回调地址
const CLIENT_CALLBACK_URL = `http://localhost:9001/sso/callback`;

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

// SSO 登录 - 简化版，用于测试
app.get('/sso/login', (req, res) => {
  console.log('收到SSO登录请求');
  
  // 如果已经登录，直接返回首页
  if (req.session.user) {
    console.log('用户已登录，直接返回首页');
    return res.redirect('/');
  }
  
  // 在实际情况中，这里会重定向到SSO服务器
  // 但为了简化测试，我们直接模拟登录成功，重定向到回调页面
  console.log('模拟SSO登录流程，直接重定向到回调页面');
  
  // 生成一个模拟令牌
  const mockToken = 'mock_jwt_token_' + Date.now();
  
  // 重定向到回调页面，带上模拟的令牌
  res.redirect(`/sso/callback?token=${mockToken}&userId=${DEMO_USER.username}`);
});

// SSO 回调处理 - 简化版，用于测试
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
    
    // 简化测试：使用预定义的用户信息，而不是从SSO服务器获取
    console.log('使用模拟用户数据，绕过SSO服务器');
    
    // 将示例用户保存到会话
    req.session.user = DEMO_USER;
    console.log('用户信息已保存到会话:', JSON.stringify(req.session.user));
    
    // 重定向到首页
    return res.redirect('/');
  } catch (error) {
    console.error('SSO回调处理错误:', error);
    return res.redirect('/?error=auth_failed&message=' + encodeURIComponent(error.message));
  }
});

// SSO 登出 - 简化版，用于测试
app.get('/sso/logout', (req, res) => {
  console.log('收到登出请求，清除本地会话');
  
  // 清除本地会话
  req.session.destroy((err) => {
    if (err) {
      console.error('清除会话时出错:', err);
    }
    
    console.log('已清除会话，重定向到首页');
    // 简化测试：直接重定向到首页，而不是访问SSO服务器
    res.redirect('/');
  });
});

// 启动服务器
app.listen(port, () => {
  console.log(`SSO客户端应用已启动，访问地址: http://localhost:${port}`);
});