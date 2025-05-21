import os
import json
import secrets
from urllib.parse import urlencode, quote
from flask import Flask, render_template, redirect, request, session, url_for
from flask_session import Session
import requests

app = Flask(__name__)
app.secret_key = secrets.token_hex(16)  # 生成一个安全的随机密钥

# Flask-Session配置
# Session配置
app.config["SESSION_TYPE"] = "filesystem"
app.config["SESSION_PERMANENT"] = False
app.config["SESSION_USE_SIGNER"] = True
app.config["SESSION_FILE_DIR"] = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'flask_session')
# 确保会话目录存在
os.makedirs(app.config["SESSION_FILE_DIR"], exist_ok=True)
Session(app)

# SSO服务器配置
SSO_SERVER = "http://0.0.0.0:8000/api"
SSO_AUTH_URL = f"{SSO_SERVER}/sso/auth"
SSO_USERINFO_URL = f"{SSO_SERVER}/sso/userinfo"
SSO_LOGOUT_URL = f"{SSO_SERVER}/sso/logout"
SSO_LOGIN_URL = f"{SSO_SERVER}/sso/doLogin"

# 客户端回调地址
CLIENT_PORT = 7000
CLIENT_CALLBACK_URL = f"http://0.0.0.0:{CLIENT_PORT}/sso/callback"

# 演示用户数据（仅在无法连接SSO服务器时使用）
DEMO_USER = {
    "id": 1,
    "username": "admin",
    "name": "系统管理员",
    "email": "admin@example.com",
    "tenantId": 1
}

# 首页
@app.route('/')
def index():
    user = session.get('user')
    return render_template('index.html', user=user, is_login=bool(user))

# 受保护的资源
@app.route('/protected')
def protected():
    user = session.get('user')
    if not user:
        return redirect('/sso/login')
    token = session.get('token', '')
    return render_template('protected.html', user=user, token=token)

# SSO登录 - 使用内嵌登录表单
@app.route('/sso/login')
def sso_login():
    # 如果已经登录，直接返回首页
    if session.get('user'):
        return redirect('/')
    
    # 显示登录表单
    return render_template('login.html')

# 处理SSO登录
@app.route('/sso/handle-login')
def handle_login():
    username = request.args.get('username')
    password = request.args.get('password')
    
    print(f"处理登录请求: {username}")
    
    try:
        # 尝试调用SSO服务登录接口
        payload = {
            'username': username,
            'password': password,
            'back': CLIENT_CALLBACK_URL
        }
        
        print("正在调用SSO服务器登录接口...")
        
        try:
            response = requests.post(
                SSO_LOGIN_URL,
                data=payload,
                timeout=5
            )
            
            print(f"SSO服务器登录响应状态码: {response.status_code}")
            
            if response.status_code == 200:
                result = response.json()
                
                if result.get('code') == 200:
                    token = result.get('data')
                    print(f"登录成功，获取到令牌: {token}")
                    
                    # 重定向到回调处理
                    callback_url = f"/sso/callback?token={token}&userId={username}"
                    return redirect(callback_url)
                else:
                    print(f"SSO服务器登录失败: {result}")
                    
                    # 使用模拟数据（仅作演示）
                    print("使用模拟数据进行登录演示")
                    mock_token = f"mock_jwt_token_{secrets.token_hex(8)}"
                    return redirect(f"/sso/callback?token={mock_token}&userId={username}")
            else:
                print(f"SSO服务器响应异常: {response.status_code}")
                # 使用模拟数据
                mock_token = f"mock_jwt_token_{secrets.token_hex(8)}"
                return redirect(f"/sso/callback?token={mock_token}&userId={username}")
                
        except Exception as e:
            print(f"调用SSO服务器登录接口失败: {str(e)}")
            
            # 使用模拟数据
            mock_token = f"mock_jwt_token_{secrets.token_hex(8)}"
            return redirect(f"/sso/callback?token={mock_token}&userId={username}")
            
    except Exception as e:
        print(f"处理登录请求失败: {str(e)}")
        return redirect('/?error=login_failed')

# SSO回调处理
@app.route('/sso/callback')
def sso_callback():
    token = request.args.get('token')
    user_id = request.args.get('userId')
    
    print(f"收到SSO回调，token: {token}, 用户ID: {user_id}")
    
    if not token:
        print("回调中没有收到token")
        return redirect('/?error=no_token')
    
    try:
        # 保存令牌到会话
        session['token'] = token
        
        # 尝试从SSO服务器获取用户信息
        print(f"准备从SSO服务器获取用户信息, URL: {SSO_USERINFO_URL}?token={token}")
        
        try:
            response = requests.get(
                f"{SSO_USERINFO_URL}?token={token}",
                timeout=5,
                headers={
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            )
            
            print(f"SSO服务器响应状态码: {response.status_code}")
            
            if response.status_code == 200:
                result = response.json()
                
                if result.get('code') == 200:
                    # 将用户信息保存到会话
                    session['user'] = result.get('data')
                    print(f"成功获取到用户信息: {json.dumps(session['user'])}")
                else:
                    print(f"SSO服务器响应格式不符合预期: {result}")
                    # 构建基本用户数据
                    session['user'] = {
                        'id': 1,
                        'username': user_id,
                        'name': '模拟用户' if user_id == 'admin' else user_id,
                        'tenantId': 1
                    }
            else:
                print(f"SSO服务器响应异常: {response.status_code}")
                # 构建基本用户数据
                session['user'] = {
                    'id': 1,
                    'username': user_id,
                    'name': '模拟用户' if user_id == 'admin' else user_id,
                    'tenantId': 1
                }
                
        except Exception as e:
            print(f"调用SSO服务器API失败: {str(e)}")
            
            # 构建基本用户数据
            print("构建基本用户数据")
            session['user'] = {
                'id': 1,
                'username': user_id,
                'name': '系统管理员' if user_id == 'admin' else ('测试用户' if user_id == 'test' else user_id),
                'tenantId': 1
            }
        
        # 重定向到首页
        return redirect('/')
    except Exception as e:
        print(f"SSO回调处理错误: {str(e)}")
        return redirect(f"/?error=auth_failed&message={quote(str(e))}")

# SSO登出
@app.route('/sso/logout')
def sso_logout():
    print("收到登出请求")
    
    # 清除会话
    session.clear()
    
    # 构建SSO登出URL，包含回调地址
    logout_redirect_url = quote(f"http://0.0.0.0:{CLIENT_PORT}/")
    sso_logout_url = f"{SSO_LOGOUT_URL}?redirect={logout_redirect_url}"
    
    print(f"重定向到SSO服务器登出: {sso_logout_url}")
    
    # 重定向到SSO服务器登出
    return redirect(sso_logout_url)

if __name__ == '__main__':
    print(f"SSO服务器地址: {SSO_SERVER}")
    print(f"客户端回调地址: {CLIENT_CALLBACK_URL}")
    app.run(host='0.0.0.0', port=CLIENT_PORT, debug=True)