<template>
  <div class="page-container">
    <div class="page-header">
      <h2>租户管理</h2>
      <el-button type="primary" @click="handleCreateTenant">创建租户</el-button>
    </div>
    
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="租户名称">
        <el-input v-model="searchForm.name" placeholder="请输入租户名称"></el-input>
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
    
    <!-- 租户列表 -->
    <el-table :data="tenants" border stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80"></el-table-column>
      <el-table-column prop="name" label="租户名称" width="180"></el-table-column>
      <el-table-column prop="description" label="租户描述"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button 
            size="mini" 
            type="danger" 
            @click="handleDelete(scope.row)"
            :disabled="currentTenant && currentTenant.id === scope.row.id">删除</el-button>
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
  name: 'TenantIndex',
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
      loading: false
    };
  },
  computed: {
    ...mapState({
      tenants: state => state.tenant.tenants,
      total: state => state.tenant.total,
      currentTenant: state => state.tenant.currentTenant
    })
  },
  created() {
    this.fetchTenants();
  },
  methods: {
    // 获取租户列表
    fetchTenants() {
      this.loading = true;
      this.$store.dispatch('tenant/getTenants', {
        ...this.searchForm,
        ...this.pageParams
      }).finally(() => {
        this.loading = false;
      });
    },
    
    // 处理搜索
    handleSearch() {
      this.pageParams.current = 1;
      this.fetchTenants();
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
      this.fetchTenants();
    },
    
    // 处理当前页变化
    handleCurrentChange(val) {
      this.pageParams.current = val;
      this.fetchTenants();
    },
    
    // 创建租户
    handleCreateTenant() {
      this.$router.push('/tenant/create');
    },
    
    // 编辑租户
    handleEdit(row) {
      this.$router.push(`/tenant/edit/${row.id}`);
    },
    
    // 删除租户
    handleDelete(row) {
      if (this.currentTenant && this.currentTenant.id === row.id) {
        this.$message.warning('不能删除当前使用的租户');
        return;
      }
      
      this.$confirm(`确定要删除租户 ${row.name} 吗? 此操作将同时删除该租户下的所有数据!`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await this.$store.dispatch('tenant/deleteTenant', row.id);
          this.$message.success('删除成功');
          this.fetchTenants();
        } catch (error) {
          console.error('Failed to delete tenant:', error);
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
