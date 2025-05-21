<template>
  <div class="page-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="handleCreateUser">创建用户</el-button>
    </div>
    
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="用户名">
        <el-input v-model="searchForm.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="searchForm.name" placeholder="请输入姓名"></el-input>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option label="启用" value="1"></el-option>
          <el-option label="禁用" value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 用户列表 -->
    <el-table :data="users" border stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="username" label="用户名" width="120"></el-table-column>
      <el-table-column prop="name" label="姓名" width="120"></el-table-column>
      <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
      <el-table-column prop="phone" label="电话" width="120"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="250">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="success" @click="handleRoles(scope.row)">分配角色</el-button>
          <el-button 
            size="mini" 
            type="danger" 
            @click="handleDelete(scope.row)"
            :disabled="scope.row.username === 'admin'">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageParams.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageParams.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
    
    <!-- 角色分配对话框 -->
    <el-dialog title="分配角色" :visible.sync="roleDialogVisible" width="500px">
      <el-transfer
        v-model="selectedRoles"
        :data="allRoles"
        :titles="['可选角色', '已选角色']"
        :props="{
          key: 'id',
          label: 'name'
        }">
      </el-transfer>
      <span slot="footer" class="dialog-footer">
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUserRoles">确定</el-button>
      </span>
    </el-dialog>
    
    <!-- 用户创建/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="userDialogVisible" width="40%">
      <el-form :model="userForm" :rules="userRules" ref="userForm" label-width="100px" class="user-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="!isCreate"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="isCreate">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        
        <el-form-item label="电话" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入电话"></el-input>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUserForm" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'UserIndex',
  data() {
    return {
      searchForm: {
        username: '',
        name: '',
        status: ''
      },
      pageParams: {
        current: 1,
        size: 10
      },
      loading: false,
      roleDialogVisible: false,
      allRoles: [],
      selectedRoles: [],
      currentUser: null,
      // 用户表单相关
      userDialogVisible: false,
      isCreate: true,
      dialogTitle: '创建用户',
      submitting: false,
      userForm: {
        id: undefined,
        username: '',
        password: '',
        name: '',
        email: '',
        phone: '',
        status: 1
      },
      userRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    ...mapState({
      users: state => state.user.userList || [],
      total: state => state.user.total || 0
    })
  },
  created() {
    this.fetchUsers();
  },
  methods: {
    // 获取用户列表
    fetchUsers() {
      this.loading = true;
      this.$store.dispatch('user/getUsers', {
        ...this.searchForm,
        ...this.pageParams
      }).finally(() => {
        this.loading = false;
      });
    },
    
    // 处理搜索
    handleSearch() {
      this.pageParams.current = 1;
      this.fetchUsers();
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        username: '',
        name: '',
        status: ''
      };
      this.handleSearch();
    },
    
    // 处理页面大小变化
    handleSizeChange(val) {
      this.pageParams.size = val;
      this.fetchUsers();
    },
    
    // 处理当前页变化
    handleCurrentChange(val) {
      this.pageParams.current = val;
      this.fetchUsers();
    },
    
    // 创建用户
    handleCreateUser() {
      this.isCreate = true;
      this.dialogTitle = '创建用户';
      this.userForm = {
        id: undefined,
        username: '',
        password: '',
        name: '',
        email: '',
        phone: '',
        status: 1
      };
      this.userDialogVisible = true;
      // 在弹窗打开后重置表单验证
      this.$nextTick(() => {
        if (this.$refs.userForm) {
          this.$refs.userForm.clearValidate();
        }
      });
    },
    
    // 编辑用户
    handleEdit(row) {
      this.isCreate = false;
      this.dialogTitle = '编辑用户';
      // 获取用户详情
      this.$store.dispatch('user/getUserById', row.id).then(user => {
        this.userForm = { ...user, password: '' };  // 不回显密码
        this.userDialogVisible = true;
        // 在弹窗打开后重置表单验证
        this.$nextTick(() => {
          if (this.$refs.userForm) {
            this.$refs.userForm.clearValidate();
          }
        });
      }).catch(error => {
        console.error('获取用户详情失败:', error);
        this.$message.error('获取用户详情失败');
      });
    },
    
    // 提交用户表单
    submitUserForm() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          this.submitting = true;
          const action = this.isCreate ? 'user/createUser' : 'user/updateUser';
          
          this.$store.dispatch(action, this.userForm)
            .then(() => {
              this.$message.success(this.isCreate ? '用户创建成功' : '用户编辑成功');
              this.userDialogVisible = false;
              this.fetchUsers(); // 刷新列表
            })
            .catch(error => {
              console.error(this.isCreate ? '创建用户失败:' : '编辑用户失败:', error);
              this.$message.error(this.isCreate ? '创建用户失败' : '编辑用户失败');
            })
            .finally(() => {
              this.submitting = false;
            });
        }
      });
    },
    
    // 打开角色分配对话框
    async handleRoles(row) {
      this.currentUser = row;
      this.roleDialogVisible = true;
      
      try {
        // 使用模拟数据展示功能
        this.allRoles = [
          { id: 1, name: '超级管理员' },
          { id: 2, name: '系统管理员' },
          { id: 3, name: '普通用户' },
          { id: 4, name: '访客' }
        ];
        
        // 设置默认选中角色
        this.selectedRoles = [2, 3];
      } catch (error) {
        console.error('Failed to fetch roles:', error);
        this.$message.error('获取角色数据失败');
      }
    },
    
    // 保存用户角色分配
    async saveUserRoles() {
      try {
        await this.$store.dispatch('user/assignRoles', {
          userId: this.currentUser.id,
          roleIds: this.selectedRoles
        });
        
        this.$message.success('角色分配成功');
        this.roleDialogVisible = false;
      } catch (error) {
        console.error('Failed to assign roles:', error);
        this.$message.error('角色分配失败');
      }
    },
    
    // 删除用户
    handleDelete(row) {
      this.$confirm(`确定要删除用户 ${row.username} 吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await this.$store.dispatch('user/deleteUser', row.id);
          this.$message.success('删除成功');
          this.fetchUsers();
        } catch (error) {
          console.error('Failed to delete user:', error);
          this.$message.error('删除失败');
        }
      }).catch(() => {
        // 取消删除
      });
    }
  }
};
</script>

<style scoped>
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style>
