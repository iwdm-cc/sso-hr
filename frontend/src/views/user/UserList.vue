<template>
  <div class="user-list">
    <div class="page-header">
      <div class="page-title">用户管理</div>
      <el-button type="primary" @click="handleCreate" v-if="hasPermission('user:create')">
        <i class="el-icon-plus"></i> 创建用户
      </el-button>
    </div>

    <el-card class="filter-container">
      <el-form :inline="true" :model="queryParams" ref="queryForm" size="small">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" value="1"></el-option>
            <el-option label="禁用" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <i class="el-icon-search"></i> 搜索
          </el-button>
          <el-button @click="resetQuery">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-container">
      <el-table
        v-loading="loading"
        :data="userList"
        border
        style="width: 100%">
        <el-table-column prop="username" label="用户名" width="180"></el-table-column>
        <el-table-column prop="name" label="姓名" width="180"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
              {{ scope.row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template slot-scope="scope">
            <span>{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleEdit(scope.row)"
              v-if="hasPermission('user:update')">
              编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleRoleAssign(scope.row)"
              v-if="hasPermission('user:assign')">
              分配角色
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleResetPassword(scope.row)"
              v-if="hasPermission('user:reset-pwd')">
              重置密码
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleDelete(scope.row)"
              class="delete-btn"
              v-if="hasPermission('user:delete')">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryParams.pageNum"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="queryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 角色分配对话框 -->
    <el-dialog title="分配角色" :visible.sync="roleDialogVisible" width="500px">
      <el-transfer
        v-model="selectedRoles"
        :data="roleOptions"
        :titles="['可选角色', '已选角色']"
        :props="{ key: 'id', label: 'name' }">
      </el-transfer>
      <div slot="footer" class="dialog-footer">
        <el-button @click="roleDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmRoleAssign">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog title="重置密码" :visible.sync="resetPwdDialogVisible" width="400px">
      <el-form :model="resetPwdForm" :rules="resetPwdRules" ref="resetPwdForm" label-width="100px">
        <el-form-item label="新密码" prop="password">
          <el-input v-model="resetPwdForm.password" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="resetPwdForm.confirmPassword" type="password" show-password></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="resetPwdDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmResetPassword">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { listUsers, deleteUser, resetPassword, assignRoles } from '@/api/user';
import { listRoles } from '@/api/role';

export default {
  name: 'UserList',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.resetPwdForm.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        username: '',
        name: '',
        status: ''
      },
      // 用户列表
      userList: [],
      // 总记录数
      total: 0,
      // 加载状态
      loading: false,
      // 角色分配对话框
      roleDialogVisible: false,
      // 角色选项
      roleOptions: [],
      // 已选择的角色
      selectedRoles: [],
      // 当前选中的用户ID
      currentUserId: null,
      // 重置密码对话框
      resetPwdDialogVisible: false,
      // 重置密码表单
      resetPwdForm: {
        userId: null,
        password: '',
        confirmPassword: ''
      },
      // 重置密码表单验证规则
      resetPwdRules: {
        password: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    ...mapGetters(['hasPermission', 'currentTenantId'])
  },
  created() {
    this.getList();
  },
  methods: {
    // 获取用户列表
    async getList() {
      this.loading = true;
      try {
        const response = await listUsers(this.queryParams);
        this.userList = response.data.list;
        this.total = response.data.total;
      } catch (error) {
        console.error('获取用户列表失败:', error);
        this.$message.error('获取用户列表失败');
      } finally {
        this.loading = false;
      }
    },
    
    // 搜索按钮点击事件
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    
    // 重置搜索表单
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    
    // 新建用户
    handleCreate() {
      this.$router.push('/users/create');
    },
    
    // 编辑用户
    handleEdit(row) {
      this.$router.push(`/users/edit/${row.id}`);
    },
    
    // 删除用户
    handleDelete(row) {
      this.$confirm(`确认删除用户"${row.name}"吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteUser(row.id);
          this.$message.success('删除成功');
          this.getList();
        } catch (error) {
          console.error('删除用户失败:', error);
          this.$message.error('删除用户失败');
        }
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },
    
    // 分配角色按钮点击事件
    async handleRoleAssign(row) {
      this.currentUserId = row.id;
      try {
        // 加载所有角色
        const roleResponse = await listRoles({ pageSize: 999 });
        this.roleOptions = roleResponse.data.list;
        
        // 获取用户已分配的角色
        this.selectedRoles = row.roleIds || [];
        this.roleDialogVisible = true;
      } catch (error) {
        console.error('获取角色数据失败:', error);
        this.$message.error('获取角色数据失败');
      }
    },
    
    // 确认角色分配
    async confirmRoleAssign() {
      try {
        await assignRoles(this.currentUserId, this.selectedRoles);
        this.$message.success('角色分配成功');
        this.roleDialogVisible = false;
        this.getList();
      } catch (error) {
        console.error('分配角色失败:', error);
        this.$message.error('分配角色失败');
      }
    },
    
    // 重置密码按钮点击事件
    handleResetPassword(row) {
      this.resetPwdForm.userId = row.id;
      this.resetPwdForm.password = '';
      this.resetPwdForm.confirmPassword = '';
      this.resetPwdDialogVisible = true;
    },
    
    // 确认重置密码
    confirmResetPassword() {
      this.$refs.resetPwdForm.validate(async valid => {
        if (valid) {
          try {
            await resetPassword(this.resetPwdForm.userId, this.resetPwdForm.password);
            this.$message.success('密码重置成功');
            this.resetPwdDialogVisible = false;
          } catch (error) {
            console.error('重置密码失败:', error);
            this.$message.error('重置密码失败');
          }
        }
      });
    },
    
    // 每页条数改变
    handleSizeChange(size) {
      this.queryParams.pageSize = size;
      this.getList();
    },
    
    // 当前页改变
    handleCurrentChange(page) {
      this.queryParams.pageNum = page;
      this.getList();
    }
  }
};
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}

.table-container {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.delete-btn {
  color: #F56C6C;
}
</style>
