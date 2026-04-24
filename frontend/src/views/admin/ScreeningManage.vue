<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center;">
        <span style="font-weight:600;font-size:15px;">场次排片列表</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增场次</el-button>
      </div>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column label="影片海报" width="80">
          <template slot-scope="scope">
            <el-image :src="scope.row.moviePoster" style="width:40px;height:56px;border-radius:4px;" fit="cover"></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="movieName" label="影片名称"></el-table-column>
        <el-table-column prop="hallName" label="放映厅"></el-table-column>
        <el-table-column prop="hallType" label="放映类型">
          <template slot-scope="scope">
            <el-tag size="mini" :type="scope.row.hallType === 'IMAX' ? 'danger' : scope.row.hallType === '3D' ? 'warning' : ''">{{ scope.row.hallType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="放映时间">
          <template slot-scope="scope">
            <div>{{ formatTime(scope.row.startTime) }}</div>
            <div style="color:#909399;font-size:12px;">至 {{ formatTime(scope.row.endTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="票价">
          <template slot-scope="scope">
            <div>¥{{ scope.row.price }}</div>
            <div style="color:#e6a23c;font-size:12px;" v-if="scope.row.discountPrice">会员价: ¥{{ scope.row.discountPrice }}</div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : scope.row.status === 2 ? 'info' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '售票中' : scope.row.status === 2 ? '已结束' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="影片" prop="movieId">
          <el-select v-model="form.movieId" filterable placeholder="选择影片" style="width:100%;" @change="onMovieChange">
            <el-option v-for="m in movies" :key="m.id" :label="m.name" :value="m.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="放映厅" prop="hallId">
          <el-select v-model="form.hallId" placeholder="选择放映厅" style="width:100%;">
            <el-option v-for="h in halls" :key="h.id" :label="h.name + ' (' + h.type + ')'" :value="h.id"></el-option>
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="开始时间" style="width:100%;" @change="calcEndTime"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="结束时间" style="width:100%;"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="票价" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员价">
              <el-input-number v-model="form.discountPrice" :min="0" :precision="2" style="width:100%;"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%;">
            <el-option :value="1" label="售票中"></el-option>
            <el-option :value="2" label="已结束"></el-option>
            <el-option :value="0" label="已取消"></el-option>
          </el-select>
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
  name: 'ScreeningManage',
  data() {
    return {
      tableData: [], loading: false,
      movies: [], halls: [], selectedMovie: null,
      dialogVisible: false, dialogTitle: '新增场次',
      form: { movieId: '', hallId: '', startTime: '', endTime: '', price: 50, discountPrice: 42, status: 1 },
      rules: {
        movieId: [{ required: true, message: '请选择影片', trigger: 'change' }],
        hallId: [{ required: true, message: '请选择放映厅', trigger: 'change' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        price: [{ required: true, message: '请输入票价', trigger: 'blur' }]
      }
    }
  },
  mounted() { this.loadData(); this.loadMovies(); this.loadHalls() },
  methods: {
    formatTime(t) { return t ? t.replace('T', ' ').substring(0, 16) : '' },
    loadData() {
      this.loading = true
      this.$http.get('/api/screening/list').then(res => {
        this.loading = false
        if (res.data.code === 200) this.tableData = res.data.data
      })
    },
    loadMovies() { this.$http.get('/api/movie/list').then(res => { if (res.data.code === 200) this.movies = res.data.data }) },
    loadHalls() { this.$http.get('/api/hall/list').then(res => { if (res.data.code === 200) this.halls = res.data.data }) },
    onMovieChange(id) { this.selectedMovie = this.movies.find(m => m.id === id) },
    calcEndTime() {
      if (this.form.startTime && this.selectedMovie && this.selectedMovie.duration) {
        const start = new Date(this.form.startTime)
        start.setMinutes(start.getMinutes() + this.selectedMovie.duration)
        const pad = n => n.toString().padStart(2, '0')
        this.form.endTime = `${start.getFullYear()}-${pad(start.getMonth()+1)}-${pad(start.getDate())} ${pad(start.getHours())}:${pad(start.getMinutes())}:${pad(start.getSeconds())}`
      }
    },
    handleAdd() {
      this.dialogTitle = '新增场次'
      this.form = { movieId: '', hallId: '', startTime: '', endTime: '', price: 50, discountPrice: 42, status: 1 }
      this.dialogVisible = true
    },
    handleEdit(row) { this.dialogTitle = '编辑场次'; this.form = { ...row }; this.dialogVisible = true },
    handleDelete(id) {
      this.$confirm('确认删除该场次吗？', '提示', { type: 'warning' }).then(() => {
        this.$http.delete('/api/screening/' + id).then(res => {
          if (res.data.code === 200) { this.$message.success('删除成功'); this.loadData() }
        })
      }).catch(() => {})
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const method = this.form.id ? 'put' : 'post'
        this.$http[method]('/api/screening', this.form).then(res => {
          if (res.data.code === 200) { this.$message.success('操作成功'); this.dialogVisible = false; this.loadData() }
        })
      })
    }
  }
}
</script>
