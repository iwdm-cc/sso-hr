const express = require('express');
const session = require('express-session');
const cookieParser = require('cookie-parser');
const axios = require('axios');
const path = require('path');

const app = express();
const port = 9001;

// 配置SSO服务器地址 - 使用0.0.0.0替代localhost以确保连接性
const SSO_SERVER = 'http://0.0.0.0:8000/api';
const SSO_AUTH_URL = `${SSO_SERVER}/sso/auth`;
const SSO_USERINFO_URL = `${SSO_SERVER}/sso/userinfo`;
const SSO_LOGOUT_URL = `${SSO_SERVER}/sso/logout`;

// 客户端回调地址 - 使用0.0.0.0替代localhost
const CLIENT_CALLBACK_URL = `http://0.0.0.0:${port}/sso/callback`;

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
  // 如果已经登录，直接返回首页
  if (req.session.user) {
    return res.redirect('/');
  }
  
  // 重定向到SSO认证服务器
  const redirectUrl = `${SSO_AUTH_URL}?redirect=${encodeURIComponent(CLIENT_CALLBACK_URL)}`;
  res.redirect(redirectUrl);
});

// SSO 回调处理
app.get('/sso/callback', async (req, res) => {
  const { token, userId } = req.query;
  console.log('收到SSO回调，token:', token, '用户ID:', userId);
  
  if (!token) {
    return res.redirect('/?error=no_token');
  }
  
  try {
    // 保存令牌和用户ID到会话
    req.session.token = token;
    
    // 获取用户信息
    const response = await axios.get(`${SSO_USERINFO_URL}?token=${token}`);
    if (response.data && response.data.code === 200) {
      req.session.user = response.data.data;
      return res.redirect('/');
    } else {
      throw new Error('获取用户信息失败');
    }
  } catch (error) {
    console.error('SSO回调处理错误:', error);
    return res.redirect('/?error=auth_failed');
  }
});

// SSO 登出
app.get('/sso/logout', (req, res) => {
  // 清除本地会话
  req.session.destroy();
  
  // 重定向到SSO服务器进行注销
  const redirectUrl = `${SSO_LOGOUT_URL}?redirect=${encodeURIComponent(`http://localhost:${port}/`)}`;
  res.redirect(redirectUrl);
});

// 启动服务器
app.listen(port, () => {
  console.log(`SSO客户端应用已启动，访问地址: http://localhost:${port}`);
});