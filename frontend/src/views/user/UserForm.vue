<template>
  <div class="user-form">
    <div class="page-header">
      <div class="page-title">{{ isEdit ? '编辑用户' : '创建用户' }}</div>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-card>
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="!isEdit">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in roleOptions"
              :key="role.id"
              :label="role.name"
              :value="role.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getUser, addUser, updateUser } from '@/api/user';
import { listRoles } from '@/api/role';

export default {
  name: 'UserForm',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      // 是否编辑模式
      isEdit: false,
      // 表单数据
      form: {
        id: undefined,
        username: '',
        name: '',
        password: '',
        confirmPassword: '',
        email: '',
        phone: '',
        status: '1',
        roleIds: []
      },
      // 角色选项
      roleOptions: [],
      // 表单验证规则
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号码', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      },
      // 加载状态
      loading: false
    };
  },
  created() {
    this.loadRoles();
    // 根据路由参数判断是否为编辑模式
    const userId = this.$route.params.id;
    if (userId) {
      this.isEdit = true;
      this.getUserInfo(userId);
    }
  },
  methods: {
    // 获取用户信息
    async getUserInfo(userId) {
      try {
        const response = await getUser(userId);
        const user = response.data;
        this.form = {
          ...this.form,
          ...user
        };
      } catch (error) {
        console.error('获取用户信息失败:', error);
        this.$message.error('获取用户信息失败');
        this.goBack();
      }
    },
    
    // 加载角色列表
    async loadRoles() {
      try {
        const response = await listRoles({ pageSize: 999 });
        this.roleOptions = response.data.list;
      } catch (error) {
        console.error('获取角色列表失败:', error);
        this.$message.error('获取角色列表失败');
      }
    },
    
    // 表单提交
    submitForm() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.loading = true;
          try {
            if (this.isEdit) {
              await updateUser(this.form);
              this.$message.success('修改用户成功');
            } else {
              await addUser(this.form);
              this.$message.success('创建用户成功');
            }
            this.goBack();
          } catch (error) {
            console.error('保存用户失败:', error);
            let errMsg = '保存用户失败';
            if (error.response && error.response.data && error.response.data.message) {
              errMsg = error.response.data.message;
            }
            this.$message.error(errMsg);
          } finally {
            this.loading = false;
          }
        }
      });
    },
    
    // 返回用户列表
    goBack() {
      this.$router.push('/users');
    }
  }
};
</script>

<style scoped>
.user-form {
  padding-bottom: 20px;
}
</style>
