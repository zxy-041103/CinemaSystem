<template>
  <div class="user-center">
    <el-row :gutter="24">
      <!-- 个人信息 -->
      <el-col :span="8">
        <div class="profile-card">
          <div class="profile-avatar-wrap">
            <el-avatar :size="80" :src="user.avatar" icon="el-icon-user-solid"></el-avatar>
            <el-upload action="/api/file/upload" :show-file-list="false" :on-success="onAvatarUpload" accept="image/*">
              <el-button type="text" size="small" class="change-avatar">更换头像</el-button>
            </el-upload>
          </div>
          <h3 class="profile-name">{{ user.nickname || user.username }}</h3>
          <p class="profile-username">@{{ user.username }}</p>
          <div class="profile-stats">
            <div class="ps-item">
              <div class="ps-value" style="color:#e6a23c;">{{ user.points || 0 }}</div>
              <div class="ps-label">积分</div>
            </div>
            <div class="ps-item">
              <div class="ps-value" style="color:#f56c6c;">{{ orders.length }}</div>
              <div class="ps-label">订单</div>
            </div>
            <div class="ps-item">
              <div class="ps-value" style="color:#667eea;">{{ collections.length }}</div>
              <div class="ps-label">收藏</div>
            </div>
          </div>
        </div>

        <!-- 修改密码 -->
        <div class="section-card" style="margin-top:16px;">
          <h4><i class="el-icon-lock" style="color:#667eea;"></i> 修改密码</h4>
          <el-form :model="pwdForm" size="small" label-width="70px" style="margin-top:12px;">
            <el-form-item label="原密码">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="small" @click="changePassword">确认修改</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-col>

      <!-- 右侧信息 -->
      <el-col :span="16">
        <!-- 编辑信息 -->
        <div class="section-card">
          <h4><i class="el-icon-edit" style="color:#667eea;"></i> 个人信息</h4>
          <el-form :model="editForm" size="small" label-width="70px" style="margin-top:12px;">
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="昵称">
                  <el-input v-model="editForm.nickname" placeholder="输入昵称"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号">
                  <el-input v-model="editForm.phone" placeholder="输入手机号"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="邮箱">
              <el-input v-model="editForm.email" placeholder="输入邮箱"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="small" @click="updateProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 我的收藏 -->
        <div class="section-card" style="margin-top:16px;">
          <h4><i class="el-icon-star-on" style="color:#e6a23c;"></i> 我的收藏</h4>
          <div v-if="collections.length === 0" style="text-align:center;padding:30px;color:#888;">
            暂无收藏影片
          </div>
          <div class="collect-grid">
            <div v-for="c in collections" :key="c.id" class="collect-item" @click="$router.push('/movie/' + c.movieId)">
              <el-image :src="c.moviePoster" style="width:60px;height:84px;border-radius:6px;flex-shrink:0;" fit="cover"></el-image>
              <div class="ci-info">
                <div class="ci-name">{{ c.movieName }}</div>
                <div class="ci-genre">{{ c.genre }}</div>
                <div class="ci-rating" v-if="c.rating">评分: {{ c.rating }}</div>
              </div>
              <el-button type="text" icon="el-icon-close" style="color:#f56c6c;" @click.stop="removeCollect(c.movieId)"></el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'UserCenter',
  data() {
    return {
      orders: [],
      collections: [],
      editForm: { nickname: '', phone: '', email: '' },
      pwdForm: { oldPassword: '', newPassword: '' }
    }
  },
  computed: {
    user() { return this.$store.state.user || {} }
  },
  mounted() {
    this.editForm = { nickname: this.user.nickname, phone: this.user.phone, email: this.user.email }
    this.loadOrders()
    this.loadCollections()
  },
  methods: {
    loadOrders() {
      this.$http.get('/api/order/user/' + this.user.id).then(res => {
        if (res.data.code === 200) this.orders = res.data.data
      })
    },
    loadCollections() {
      this.$http.get('/api/collection/user/' + this.user.id).then(res => {
        if (res.data.code === 200) this.collections = res.data.data
      })
    },
    onAvatarUpload(res) {
      if (res.code === 200) {
        const updated = { ...this.user, avatar: res.data }
        this.$http.put('/api/user', updated).then(r => {
          if (r.data.code === 200) {
            this.$store.dispatch('login', updated)
            this.$message.success('头像更新成功')
          }
        })
      }
    },
    updateProfile() {
      const updated = { ...this.user, ...this.editForm }
      this.$http.put('/api/user', updated).then(res => {
        if (res.data.code === 200) {
          this.$store.dispatch('login', updated)
          this.$message.success('信息更新成功')
        }
      })
    },
    changePassword() {
      if (!this.pwdForm.oldPassword || !this.pwdForm.newPassword) {
        this.$message.warning('请填写完整密码信息')
        return
      }
      this.$http.put('/api/updatePassword', {
        userId: this.user.id.toString(),
        oldPassword: this.pwdForm.oldPassword,
        newPassword: this.pwdForm.newPassword
      }).then(res => {
        if (res.data.code === 200) {
          this.$message.success('密码修改成功，请重新登录')
          this.$store.dispatch('logout')
          this.$router.push('/login')
        } else {
          this.$message.error(res.data.msg)
        }
      })
    },
    removeCollect(movieId) {
      this.$http.delete('/api/collection', { params: { userId: this.user.id, movieId } }).then(res => {
        if (res.data.code === 200) { this.$message.success('取消收藏'); this.loadCollections() }
      })
    }
  }
}
</script>

<style scoped>
.profile-card {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.08));
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 16px;
  padding: 30px;
  text-align: center;
}
.profile-avatar-wrap { margin-bottom: 12px; }
.change-avatar { color: #667eea !important; }
.profile-name { color: #fff; font-size: 20px; margin-bottom: 4px; }
.profile-username { color: rgba(255,255,255,0.4); font-size: 13px; margin-bottom: 20px; }
.profile-stats { display: flex; justify-content: space-around; }
.ps-item { text-align: center; }
.ps-value { font-size: 22px; font-weight: 700; }
.ps-label { font-size: 12px; color: rgba(255,255,255,0.4); margin-top: 2px; }

.section-card {
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 14px;
  padding: 20px 24px;
}
.section-card h4 { color: #fff; font-size: 16px; display: flex; align-items: center; gap: 8px; }

.collect-grid { display: flex; flex-direction: column; gap: 10px; margin-top: 12px; }
.collect-item {
  display: flex; align-items: center; gap: 12px;
  padding: 10px 12px;
  background: rgba(255,255,255,0.03);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}
.collect-item:hover { background: rgba(102, 126, 234, 0.08); }
.ci-info { flex: 1; }
.ci-name { color: #fff; font-size: 14px; font-weight: 600; margin-bottom: 4px; }
.ci-genre { color: rgba(255,255,255,0.4); font-size: 12px; }
.ci-rating { color: #ff9900; font-size: 12px; margin-top: 2px; }

/* 表单样式适配暗色主题 */
.section-card >>> .el-input__inner {
  background: rgba(255,255,255,0.05);
  border-color: rgba(255,255,255,0.1);
  color: #e0e0e0;
}
.section-card >>> .el-form-item__label {
  color: rgba(255,255,255,0.6);
}
</style>
