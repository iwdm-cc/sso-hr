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
  </div>
</template>

<script>
import { mapState } from 'vuex';
import { getUserRoles } from '@/api/user';
import { getAllRoles } from '@/api/role';

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
      currentUser: null
    };
  },
  computed: {
    ...mapState({
      users: state => state.user.users,
      total: state => state.user.total
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
      this.$router.push('/user/create');
    },
    
    // 编辑用户
    handleEdit(row) {
      this.$router.push(`/user/edit/${row.id}`);
    },
    
    // 打开角色分配对话框
    async handleRoles(row) {
      this.currentUser = row;
      this.roleDialogVisible = true;
      
      try {
        // 获取所有角色
        const rolesResponse = await getAllRoles();
        this.allRoles = rolesResponse.data;
        
        // 获取用户已分配的角色
        const userRolesResponse = await getUserRoles(row.id);
        this.selectedRoles = userRolesResponse.data.map(role => role.id);
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
