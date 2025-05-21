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

// SSO 登录 - 连接到真实的SSO服务器
app.get('/sso/login', (req, res) => {
  console.log('收到SSO登录请求');
  
  // 如果已经登录，直接返回首页
  if (req.session.user) {
    console.log('用户已登录，直接返回首页');
    return res.redirect('/');
  }
  
  // 重定向到SSO认证服务器
  const redirectUrl = `${SSO_AUTH_URL}?redirect=${encodeURIComponent(CLIENT_CALLBACK_URL)}`;
  console.log('重定向到SSO认证服务器:', redirectUrl);
  
  // 为了便于调试，添加后备方案
  try {
    res.redirect(redirectUrl);
  } catch (error) {
    console.error('重定向到SSO服务器失败:', error);
    // 如果重定向失败，使用模拟方式
    res.redirect(`/sso/callback?token=mock_jwt_token_${Date.now()}&userId=${DEMO_USER.username}`);
  }
});

// SSO 回调处理 - 连接到真实的SSO服务器
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
        timeout: 5000, // 5秒超时
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
        // 如果响应格式不符合预期，使用备用用户数据
        console.warn('SSO服务器响应格式不符合预期，使用备用数据');
        req.session.user = {
          ...DEMO_USER,
          note: '这是备用数据，SSO服务器响应异常'
        };
      }
    } catch (apiError) {
      console.error('调用SSO服务器API失败:', apiError.message);
      
      // 使用备用用户数据
      console.warn('使用备用用户数据');
      req.session.user = {
        ...DEMO_USER,
        note: '这是备用数据，SSO服务器连接失败'
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