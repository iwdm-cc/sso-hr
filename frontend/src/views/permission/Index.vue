<template>
  <div class="page-container">
    <div class="page-header">
      <h2>权限管理</h2>
      <el-button type="primary" @click="handleCreatePermission">创建权限</el-button>
    </div>
    
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="权限名称">
        <el-input v-model="searchForm.name" placeholder="请输入权限名称"></el-input>
      </el-form-item>
      <el-form-item label="权限代码">
        <el-input v-model="searchForm.code" placeholder="请输入权限代码"></el-input>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
          <el-option label="菜单" value="menu"></el-option>
          <el-option label="按钮" value="button"></el-option>
          <el-option label="API" value="api"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 权限列表 -->
    <el-table :data="permissions" border stripe style="width: 100%" v-loading="loading" row-key="id">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="权限名称" width="150"></el-table-column>
      <el-table-column prop="code" label="权限代码" width="180"></el-table-column>
      <el-table-column prop="type" label="类型" width="100">
        <template slot-scope="scope">
          <el-tag :type="getTypeTag(scope.row.type)">
            {{ scope.row.type === 'menu' ? '菜单' : scope.row.type === 'button' ? '按钮' : 'API' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="url" label="资源路径" width="200"></el-table-column>
      <el-table-column prop="parentId" label="父权限ID" width="100"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button 
            size="mini" 
            type="danger" 
            @click="handleDelete(scope.row)">删除</el-button>
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
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'PermissionIndex',
  data() {
    return {
      searchForm: {
        name: '',
        code: '',
        type: ''
      },
      pageParams: {
        current: 1,
        size: 10
      },
      loading: false
    };
  },
  computed: {
    ...mapState({
      permissions: state => state.permission.permissions,
      total: state => state.permission.total
    })
  },
  created() {
    this.fetchPermissions();
  },
  methods: {
    // 获取权限列表
    fetchPermissions() {
      this.loading = true;
      this.$store.dispatch('permission/getPermissions', {
        ...this.searchForm,
        ...this.pageParams
      }).finally(() => {
        this.loading = false;
      });
    },
    
    // 处理搜索
    handleSearch() {
      this.pageParams.current = 1;
      this.fetchPermissions();
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        name: '',
        code: '',
        type: ''
      };
      this.handleSearch();
    },
    
    // 处理页面大小变化
    handleSizeChange(val) {
      this.pageParams.size = val;
      this.fetchPermissions();
    },
    
    // 处理当前页变化
    handleCurrentChange(val) {
      this.pageParams.current = val;
      this.fetchPermissions();
    },
    
    // 创建权限
    handleCreatePermission() {
      this.$router.push('/permission/create');
    },
    
    // 编辑权限
    handleEdit(row) {
      this.$router.push(`/permission/edit/${row.id}`);
    },
    
    // 删除权限
    handleDelete(row) {
      this.$confirm(`确定要删除权限 ${row.name} 吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await this.$store.dispatch('permission/deletePermission', row.id);
          this.$message.success('删除成功');
          this.fetchPermissions();
        } catch (error) {
          console.error('Failed to delete permission:', error);
          this.$message.error('删除失败');
        }
      }).catch(() => {
        // 取消删除
      });
    },
    
    // 根据权限类型获取标签类型
    getTypeTag(type) {
      switch (type) {
        case 'menu':
          return 'success';
        case 'button':
          return 'warning';
        case 'api':
          return 'info';
        default:
          return '';
      }
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
