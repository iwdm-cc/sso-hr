<template>
  <div class="tenant-list">
    <div class="page-header">
      <div class="page-title">租户管理</div>
      <el-button type="primary" @click="handleCreate" v-if="hasPermission('tenant:create')">
        <i class="el-icon-plus"></i> 创建租户
      </el-button>
    </div>

    <el-card class="filter-container">
      <el-form :inline="true" :model="queryParams" ref="queryForm" size="small">
        <el-form-item label="租户名称" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入租户名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="租户状态" clearable>
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
        :data="tenantList"
        border
        style="width: 100%">
        <el-table-column prop="name" label="租户名称" width="180"></el-table-column>
        <el-table-column prop="code" label="租户编码" width="180"></el-table-column>
        <el-table-column prop="description" label="租户描述"></el-table-column>
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
              v-if="hasPermission('tenant:update')">
              编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="switchToTenant(scope.row)"
              v-if="currentTenantId != scope.row.id">
              切换到该租户
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleDelete(scope.row)"
              class="delete-btn"
              v-if="hasPermission('tenant:delete')">
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
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { listTenants, deleteTenant } from '@/api/tenant';

export default {
  name: 'TenantList',
  data() {
    return {
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: '',
        status: ''
      },
      // 租户列表
      tenantList: [],
      // 总记录数
      total: 0,
      // 加载状态
      loading: false
    };
  },
  computed: {
    ...mapGetters(['hasPermission', 'currentTenantId'])
  },
  created() {
    this.getList();
  },
  methods: {
    // 获取租户列表
    async getList() {
      this.loading = true;
      try {
        const response = await listTenants(this.queryParams);
        this.tenantList = response.data.list;
        this.total = response.data.total;
      } catch (error) {
        console.error('获取租户列表失败:', error);
        this.$message.error('获取租户列表失败');
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
    
    // 新建租户
    handleCreate() {
      this.$router.push('/tenants/create');
    },
    
    // 编辑租户
    handleEdit(row) {
      this.$router.push(`/tenants/edit/${row.id}`);
    },
    
    // 删除租户
    handleDelete(row) {
      if (row.id === this.currentTenantId) {
        this.$message.error('无法删除当前正在使用的租户');
        return;
      }
      
      this.$confirm(`确认删除租户"${row.name}"吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteTenant(row.id);
          this.$message.success('删除成功');
          this.getList();
        } catch (error) {
          console.error('删除租户失败:', error);
          this.$message.error('删除租户失败');
        }
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },
    
    // 切换到指定租户
    switchToTenant(row) {
      this.$confirm(`确认切换到租户"${row.name}"吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$store.dispatch('switchTenant', row.id);
        this.$router.push('/');
        this.$message.success(`已切换到租户: ${row.name}`);
      }).catch(() => {
        this.$message.info('已取消切换');
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
