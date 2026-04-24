<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center;">
        <el-input v-model="search" placeholder="搜索放映厅名称" style="width:250px;" clearable @clear="loadData" @keyup.enter.native="loadData">
          <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
        </el-input>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增放映厅</el-button>
      </div>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="name" label="放映厅名称"></el-table-column>
        <el-table-column label="类型">
          <template slot-scope="scope">
            <el-tag :type="typeTag(scope.row.type)" size="small">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="座位布局">
          <template slot-scope="scope">{{ scope.row.seatRows }}行 × {{ scope.row.seatCols }}列 ({{ scope.row.seatRows * scope.row.seatCols }}座)</template>
        </el-table-column>
        <el-table-column label="状态">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">{{ scope.row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime"></el-table-column>
        <el-table-column label="操作" width="160">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;text-align:right;" @current-change="handlePageChange" :current-page="pageNum" :page-size="pageSize" :total="total" layout="total, prev, pager, next, jumper"></el-pagination>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="放映厅名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入放映厅名称"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" style="width:100%;">
            <el-option value="2D" label="2D"></el-option>
            <el-option value="3D" label="3D"></el-option>
            <el-option value="IMAX" label="IMAX"></el-option>
            <el-option value="4DX" label="4DX"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="座位行数" prop="seatRows">
          <el-input-number v-model="form.seatRows" :min="1" :max="20" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="座位列数" prop="seatCols">
          <el-input-number v-model="form.seatCols" :min="1" :max="30" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用"></el-switch>
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
  name: 'HallManage',
  data() {
    return {
      tableData: [], loading: false, search: '',
      pageNum: 1, pageSize: 10, total: 0,
      dialogVisible: false, dialogTitle: '新增放映厅',
      form: { name: '', type: '2D', seatRows: 8, seatCols: 10, status: 1 },
      rules: {
        name: [{ required: true, message: '请输入放映厅名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择类型', trigger: 'change' }]
      }
    }
  },
  mounted() { this.loadData() },
  methods: {
    typeTag(type) {
      const map = { 'IMAX': 'danger', '3D': 'warning', '4DX': 'success', '2D': '' }
      return map[type] || ''
    },
    loadData() {
      this.loading = true
      this.$http.get('/api/hall/page', { params: { pageNum: this.pageNum, pageSize: this.pageSize, name: this.search } }).then(res => {
        this.loading = false
        if (res.data.code === 200) { this.tableData = res.data.data.records; this.total = res.data.data.total }
      })
    },
    handlePageChange(val) { this.pageNum = val; this.loadData() },
    handleAdd() {
      this.dialogTitle = '新增放映厅'
      this.form = { name: '', type: '2D', seatRows: 8, seatCols: 10, status: 1 }
      this.dialogVisible = true
    },
    handleEdit(row) { this.dialogTitle = '编辑放映厅'; this.form = { ...row }; this.dialogVisible = true },
    handleDelete(id) {
      this.$confirm('确认删除该放映厅吗？同时会删除相关座位数据', '提示', { type: 'warning' }).then(() => {
        this.$http.delete('/api/hall/' + id).then(res => {
          if (res.data.code === 200) { this.$message.success('删除成功'); this.loadData() }
        })
      }).catch(() => {})
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const method = this.form.id ? 'put' : 'post'
        this.$http[method]('/api/hall', this.form).then(res => {
          if (res.data.code === 200) { this.$message.success('操作成功'); this.dialogVisible = false; this.loadData() }
        })
      })
    }
  }
}
</script>
