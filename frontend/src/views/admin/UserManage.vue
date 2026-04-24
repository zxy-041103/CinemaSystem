<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center;">
        <el-input v-model="search" placeholder="搜索用户名" style="width:250px;" clearable @clear="loadData" @keyup.enter.native="loadData">
          <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
        </el-input>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增用户</el-button>
      </div>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column label="头像" width="70">
          <template slot-scope="scope">
            <el-avatar :size="36" :src="scope.row.avatar" icon="el-icon-user-solid"></el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="nickname" label="昵称"></el-table-column>
        <el-table-column prop="phone" label="手机号"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="role" label="角色">
          <template slot-scope="scope">
            <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : ''" size="small">{{ scope.row.role === 'ADMIN' ? '管理员' : '普通用户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="积分">
          <template slot-scope="scope">
            <span style="color:#e6a23c;font-weight:600;">{{ scope.row.points }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">{{ scope.row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" prop="createTime"></el-table-column>
        <el-table-column label="操作" width="160">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(scope.row.id)" :disabled="scope.row.role === 'ADMIN'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;text-align:right;" @current-change="handlePageChange" :current-page="pageNum" :page-size="pageSize" :total="total" layout="total, prev, pager, next, jumper"></el-pagination>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="form" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width:100%;">
            <el-option value="USER" label="普通用户"></el-option>
            <el-option value="ADMIN" label="管理员"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="积分">
          <el-input-number v-model="form.points" :min="0" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'UserManage',
  data() {
    return {
      tableData: [], loading: false, search: '',
      pageNum: 1, pageSize: 10, total: 0,
      dialogVisible: false, dialogTitle: '新增用户',
      form: { username: '', password: '', nickname: '', phone: '', email: '', role: 'USER', points: 0, status: 1 },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
      }
    }
  },
  mounted() { this.loadData() },
  methods: {
    loadData() {
      this.loading = true
      this.$http.get('/api/user/page', { params: { pageNum: this.pageNum, pageSize: this.pageSize, username: this.search } }).then(res => {
        this.loading = false
        if (res.data.code === 200) { this.tableData = res.data.data.records; this.total = res.data.data.total }
      })
    },
    handlePageChange(val) { this.pageNum = val; this.loadData() },
    handleAdd() {
      this.dialogTitle = '新增用户'
      this.form = { username: '', password: '', nickname: '', phone: '', email: '', role: 'USER', points: 0, status: 1 }
      this.dialogVisible = true
    },
    handleEdit(row) { this.dialogTitle = '编辑用户'; this.form = { ...row }; this.dialogVisible = true },
    handleDelete(id) {
      this.$confirm('确认删除该用户吗？', '提示', { type: 'warning' }).then(() => {
        this.$http.delete('/api/user/' + id).then(res => {
          if (res.data.code === 200) { this.$message.success('删除成功'); this.loadData() }
        })
      }).catch(() => {})
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const method = this.form.id ? 'put' : 'post'
        this.$http[method]('/api/user', this.form).then(res => {
          if (res.data.code === 200) { this.$message.success('操作成功'); this.dialogVisible = false; this.loadData() } else { this.$message.error(res.data.msg) }
        })
      })
    }
  }
}
</script>
