<template>
  <div class="tenant-form">
    <div class="page-header">
      <div class="page-title">{{ isEdit ? '编辑租户' : '创建租户' }}</div>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-card>
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="租户名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入租户名称"></el-input>
        </el-form-item>
        <el-form-item label="租户编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入租户编码" :disabled="isEdit"></el-input>
          <div class="form-help-text" v-if="!isEdit">创建后不可修改，请谨慎填写</div>
        </el-form-item>
        <el-form-item label="租户描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入租户描述"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="管理员账号" prop="adminUsername" v-if="!isEdit">
          <el-input v-model="form.adminUsername" placeholder="请输入管理员账号"></el-input>
        </el-form-item>
        <el-form-item label="管理员密码" prop="adminPassword" v-if="!isEdit">
          <el-input v-model="form.adminPassword" type="password" placeholder="请输入管理员密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="!isEdit">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入管理员密码" show-password></el-input>
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
import { getTenant, addTenant, updateTenant } from '@/api/tenant';

export default {
  name: 'TenantForm',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.form.adminPassword) {
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
        name: '',
        code: '',
        description: '',
        status: '1',
        adminUsername: '',
        adminPassword: '',
        confirmPassword: ''
      },
      // 表单验证规则
      rules: {
        name: [
          { required: true, message: '请输入租户名称', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入租户编码', trigger: 'blur' },
          { pattern: /^[a-z][a-z0-9_]*$/, message: '租户编码只能由小写字母、数字和下划线组成，且必须以字母开头', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ],
        adminUsername: [
          { required: true, message: '请输入管理员账号', trigger: 'blur' },
          { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        adminPassword: [
          { required: true, message: '请输入管理员密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入管理员密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      // 加载状态
      loading: false
    };
  },
  created() {
    // 根据路由参数判断是否为编辑模式
    const tenantId = this.$route.params.id;
    if (tenantId) {
      this.isEdit = true;
      this.getTenantInfo(tenantId);
    }
  },
  methods: {
    // 获取租户信息
    async getTenantInfo(tenantId) {
      try {
        const response = await getTenant(tenantId);
        const tenant = response.data;
        this.form = {
          ...this.form,
          ...tenant
        };
      } catch (error) {
        console.error('获取租户信息失败:', error);
        this.$message.error('获取租户信息失败');
        this.goBack();
      }
    },
    
    // 表单提交
    submitForm() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.loading = true;
          try {
            if (this.isEdit) {
              await updateTenant(this.form);
              this.$message.success('修改租户成功');
            } else {
              await addTenant(this.form);
              this.$message.success('创建租户成功');
            }
            this.goBack();
          } catch (error) {
            console.error('保存租户失败:', error);
            let errMsg = '保存租户失败';
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
    
    // 返回租户列表
    goBack() {
      this.$router.push('/tenants');
    }
  }
};
</script>

<style scoped>
.tenant-form {
  padding-bottom: 20px;
}

.form-help-text {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
