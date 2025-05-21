<template>
  <div class="page-container">
    <div class="page-header">
      <h2>角色管理</h2>
      <el-button type="primary" @click="handleCreateRole">创建角色</el-button>
    </div>
    
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="角色名称">
        <el-input v-model="searchForm.name" placeholder="请输入角色名称"></el-input>
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
    
    <!-- 角色列表 -->
    <el-table :data="roles" border stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="角色名称" width="150"></el-table-column>
      <el-table-column prop="description" label="角色描述"></el-table-column>
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
          <el-button size="mini" type="success" @click="handlePermissions(scope.row)">分配权限</el-button>
          <el-button 
            size="mini" 
            type="danger" 
            @click="handleDelete(scope.row)"
            :disabled="scope.row.name === 'admin'">删除</el-button>
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
    
    <!-- 权限分配对话框 -->
    <el-dialog title="分配权限" :visible.sync="permissionDialogVisible" width="500px">
      <el-tree
        ref="permissionTree"
        :data="permissionTree"
        show-checkbox
        node-key="id"
        :props="{
          label: 'name',
          children: 'children'
        }">
      </el-tree>
      <span slot="footer" class="dialog-footer">
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRolePermissions">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import { getRolePermissions } from '@/api/role';
import { getAllPermissions } from '@/api/permission';

export default {
  name: 'RoleIndex',
  data() {
    return {
      searchForm: {
        name: '',
        status: ''
      },
      pageParams: {
        current: 1,
        size: 10
      },
      loading: false,
      permissionDialogVisible: false,
      permissionTree: [],
      currentRole: null
    };
  },
  computed: {
    ...mapState({
      roles: state => state.role.roles,
      total: state => state.role.total
    })
  },
  created() {
    this.fetchRoles();
  },
  methods: {
    // 获取角色列表
    fetchRoles() {
      this.loading = true;
      this.$store.dispatch('role/getRoles', {
        ...this.searchForm,
        ...this.pageParams
      }).finally(() => {
        this.loading = false;
      });
    },
    
    // 处理搜索
    handleSearch() {
      this.pageParams.current = 1;
      this.fetchRoles();
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        name: '',
        status: ''
      };
      this.handleSearch();
    },
    
    // 处理页面大小变化
    handleSizeChange(val) {
      this.pageParams.size = val;
      this.fetchRoles();
    },
    
    // 处理当前页变化
    handleCurrentChange(val) {
      this.pageParams.current = val;
      this.fetchRoles();
    },
    
    // 创建角色
    handleCreateRole() {
      this.$router.push('/role/create');
    },
    
    // 编辑角色
    handleEdit(row) {
      this.$router.push(`/role/edit/${row.id}`);
    },
    
    // 打开权限分配对话框
    async handlePermissions(row) {
      this.currentRole = row;
      this.permissionDialogVisible = true;
      
      try {
        // 获取所有权限
        const permissionsResponse = await getAllPermissions();
        this.buildPermissionTree(permissionsResponse.data);
        
        // 获取角色已分配的权限
        const rolePermissionsResponse = await getRolePermissions(row.id);
        const rolePermissionIds = rolePermissionsResponse.data.map(perm => perm.id);
        
        // 设置已选权限
        this.$nextTick(() => {
          this.$refs.permissionTree.setCheckedKeys(rolePermissionIds);
        });
      } catch (error) {
        console.error('Failed to fetch permissions:', error);
        this.$message.error('获取权限数据失败');
      }
    },
    
    // 构建权限树
    buildPermissionTree(permissions) {
      // 根据权限类型和路径构建树形结构
      const permMap = {};
      const tree = [];
      
      // 第一次遍历，将所有权限放入map
      permissions.forEach(perm => {
        permMap[perm.id] = {
          ...perm,
          children: []
        };
      });
      
      // 第二次遍历，构建树
      permissions.forEach(perm => {
        const currentNode = permMap[perm.id];
        
        if (perm.parentId === 0 || !perm.parentId) {
          // 根节点
          tree.push(currentNode);
        } else {
          // 子节点
          const parentNode = permMap[perm.parentId];
          if (parentNode) {
            parentNode.children.push(currentNode);
          }
        }
      });
      
      this.permissionTree = tree;
    },
    
    // 保存角色权限分配
    async saveRolePermissions() {
      const checkedKeys = this.$refs.permissionTree.getCheckedKeys();
      const halfCheckedKeys = this.$refs.permissionTree.getHalfCheckedKeys();
      const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys];
      
      try {
        await this.$store.dispatch('role/assignPermissions', {
          roleId: this.currentRole.id,
          permissionIds: allCheckedKeys
        });
        
        this.$message.success('权限分配成功');
        this.permissionDialogVisible = false;
      } catch (error) {
        console.error('Failed to assign permissions:', error);
        this.$message.error('权限分配失败');
      }
    },
    
    // 删除角色
    handleDelete(row) {
      this.$confirm(`确定要删除角色 ${row.name} 吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await this.$store.dispatch('role/deleteRole', row.id);
          this.$message.success('删除成功');
          this.fetchRoles();
        } catch (error) {
          console.error('Failed to delete role:', error);
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
