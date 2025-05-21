<template>
  <div class="permission-form">
    <div class="page-header">
      <div class="page-title">{{ isEdit ? '编辑权限' : '创建权限' }}</div>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-card>
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="上级权限">
          <el-cascader
            v-model="parentId"
            :options="permissionOptions"
            :props="{ checkStrictly: true, value: 'id', label: 'name' }"
            clearable
            placeholder="请选择上级权限">
          </el-cascader>
        </el-form-item>
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入权限名称"></el-input>
        </el-form-item>
        <el-form-item label="权限类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="menu">菜单</el-radio>
            <el-radio label="button">按钮</el-radio>
            <el-radio label="api">接口</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="权限标识" prop="code" v-if="form.type !== 'menu'">
          <el-input v-model="form.code" placeholder="请输入权限标识"></el-input>
          <div class="form-help-text">例如：user:create, role:update</div>
        </el-form-item>
        <el-form-item label="资源路径" prop="path" v-if="form.type === 'menu' || form.type === 'api'">
          <el-input v-model="form.path" placeholder="请输入资源路径"></el-input>
          <div class="form-help-text" v-if="form.type === 'menu'">前端路由路径，例如：/users</div>
          <div class="form-help-text" v-else>API路径，例如：/api/users</div>
        </el-form-item>
        <el-form-item label="图标" prop="icon" v-if="form.type === 'menu'">
          <el-input v-model="form.icon" placeholder="请输入图标类名"></el-input>
          <div class="form-help-text">Element UI图标，例如：el-icon-user</div>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999"></el-input-number>
          <div class="form-help-text">数字越小排序越靠前</div>
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
import { getPermission, addPermission, updatePermission, listPermissions } from '@/api/permission';

export default {
  name: 'PermissionForm',
  data() {
    return {
      // 是否编辑模式
      isEdit: false,
      // 表单数据
      form: {
        id: undefined,
        name: '',
        code: '',
        type: 'menu',
        path: '',
        parentId: null,
        icon: '',
        sort: 0
      },
      // 上级权限选择的值
      parentId: null,
      // 权限选项
      permissionOptions: [],
      // 表单验证规则
      rules: {
        name: [
          { required: true, message: '请输入权限名称', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择权限类型', trigger: 'change' }
        ],
        code: [
          { required: true, message: '请输入权限标识', trigger: 'blur' },
          { pattern: /^[a-z]+(:[a-z]+)*$/, message: '权限标识格式不正确', trigger: 'blur' }
        ],
        path: [
          { required: true, message: '请输入资源路径', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '请输入排序值', trigger: 'blur' }
        ]
      },
      // 加载状态
      loading: false
    };
  },
  watch: {
    parentId(val) {
      if (val && val.length > 0) {
        this.form.parentId = val[val.length - 1];
      } else {
        this.form.parentId = null;
      }
    }
  },
  created() {
    this.loadPermissionOptions();
    
    // 根据路由参数判断是否为编辑模式
    const permissionId = this.$route.params.id;
    if (permissionId) {
      this.isEdit = true;
      this.getPermissionInfo(permissionId);
    } else {
      // 检查是否有父级ID参数
      const parentId = this.$route.query.parentId;
      if (parentId) {
        this.parentId = [parentId];
        this.form.parentId = parentId;
      }
    }
  },
  methods: {
    // 获取权限信息
    async getPermissionInfo(permissionId) {
      try {
        const response = await getPermission(permissionId);
        const permission = response.data;
        this.form = { ...this.form, ...permission };
        
        // 设置上级权限选择器的值
        if (this.form.parentId) {
          this.parentId = [this.form.parentId];
        }
      } catch (error) {
        console.error('获取权限信息失败:', error);
        this.$message.error('获取权限信息失败');
        this.goBack();
      }
    },
    
    // 加载权限选项
    async loadPermissionOptions() {
      try {
        const response = await listPermissions();
        this.permissionOptions = this.formatPermissionOptions(response.data);
      } catch (error) {
        console.error('获取权限选项失败:', error);
        this.$message.error('获取权限选项失败');
      }
    },
    
    // 格式化权限选项
    formatPermissionOptions(permissions) {
      return permissions.map(item => {
        const option = {
          id: item.id,
          name: item.name,
          value: item.id,
          label: item.name
        };
        
        if (item.children && item.children.length > 0) {
          option.children = this.formatPermissionOptions(item.children);
        }
        
        return option;
      });
    },
    
    // 表单提交
    submitForm() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.loading = true;
          try {
            if (this.isEdit) {
              await updatePermission(this.form);
              this.$message.success('修改权限成功');
            } else {
              await addPermission(this.form);
              this.$message.success('创建权限成功');
            }
            this.goBack();
          } catch (error) {
            console.error('保存权限失败:', error);
            let errMsg = '保存权限失败';
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
    
    // 返回权限列表
    goBack() {
      this.$router.push('/permissions');
    }
  }
};
</script>

<style scoped>
.permission-form {
  padding-bottom: 20px;
}

.form-help-text {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
