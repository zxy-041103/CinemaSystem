<template>
  <div class="detail-page" v-if="movie">
    <div class="detail-banner">
      <div class="detail-poster">
        <el-image :src="movie.poster" style="width:260px;height:380px;border-radius:12px;" fit="cover" :preview-src-list="[movie.poster]"></el-image>
      </div>
      <div class="detail-info">
        <h1 class="detail-title">{{ movie.name }}</h1>
        <div class="detail-tags">
          <el-tag v-for="g in genres" :key="g" size="small" effect="dark" style="margin-right:6px;background:rgba(102,126,234,0.3);border:none;color:#acb6f0;">{{ g }}</el-tag>
          <el-tag v-if="movie.country" size="small" effect="dark" style="background:rgba(230,162,60,0.3);border:none;color:#f0d9ac;">{{ movie.country }}</el-tag>
        </div>
        <div class="detail-meta">
          <div class="meta-item"><i class="el-icon-user"></i> 导演：{{ movie.director }}</div>
          <div class="meta-item"><i class="el-icon-star-on"></i> 主演：{{ movie.actors }}</div>
          <div class="meta-item"><i class="el-icon-time"></i> 时长：{{ movie.duration }}分钟</div>
          <div class="meta-item"><i class="el-icon-date"></i> 上映：{{ movie.releaseDate }}</div>
        </div>
        <div class="detail-rating" v-if="movie.rating > 0">
          <span class="rating-num">{{ movie.rating }}</span>
          <el-rate :value="movie.rating / 2" disabled text-color="#ff9900" style="display:inline-flex;vertical-align:middle;margin-left:8px;"></el-rate>
        </div>
        <div class="detail-actions">
          <el-button type="primary" size="medium" round icon="el-icon-star-off" @click="toggleCollect" :loading="collectLoading">
            {{ isCollected ? '已收藏' : '收藏' }}
          </el-button>
        </div>
      </div>
    </div>

    <div class="detail-desc">
      <h3>影片简介</h3>
      <p>{{ movie.description || '暂无简介' }}</p>
    </div>

    <!-- 场次列表 -->
    <div class="screening-section" v-if="movie.status === 1">
      <h3><i class="el-icon-date" style="color:#667eea;"></i> 选择场次</h3>
      <div v-if="screenings.length === 0" class="no-screening">
        <i class="el-icon-warning-outline" style="font-size:40px;color:#909399;"></i>
        <p>暂无可用场次</p>
      </div>
      <div class="screening-list">
        <div v-for="s in screenings" :key="s.id" class="screening-card" @click="goSeatSelect(s.id)">
          <div class="sc-time">
            <div class="sc-start">{{ formatTime(s.startTime) }}</div>
            <div class="sc-end">{{ formatTime(s.endTime) }} 散场</div>
          </div>
          <div class="sc-info">
            <div class="sc-hall">
              <el-tag size="mini" :type="s.hallType === 'IMAX' ? 'danger' : s.hallType === '3D' ? 'warning' : ''">{{ s.hallType }}</el-tag>
              <span>{{ s.hallName }}</span>
            </div>
          </div>
          <div class="sc-price">
            <div class="price-main">¥{{ s.price }}</div>
            <div class="price-vip" v-if="s.discountPrice">会员价 ¥{{ s.discountPrice }}</div>
          </div>
          <el-button type="primary" size="small" round>选座购票</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MovieDetail',
  data() {
    return { movie: null, screenings: [], isCollected: false, collectLoading: false }
  },
  computed: {
    genres() { return this.movie && this.movie.genre ? this.movie.genre.split('/') : [] },
    userId() { return this.$store.getters.userId }
  },
  mounted() {
    const id = this.$route.params.id
    this.$http.get('/api/movie/' + id).then(res => {
      if (res.data.code === 200) this.movie = res.data.data
    })
    this.$http.get('/api/screening/movie/' + id).then(res => {
      if (res.data.code === 200) this.screenings = res.data.data
    })
    if (this.userId) {
      this.$http.get('/api/collection/check', { params: { userId: this.userId, movieId: id } }).then(res => {
        if (res.data.code === 200) this.isCollected = res.data.data
      })
    }
  },
  methods: {
    formatTime(t) { return t ? t.replace('T', ' ').substring(11, 16) : '' },
    goSeatSelect(screeningId) {
      if (!this.$store.getters.isLogin) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.$router.push('/seat-select/' + screeningId)
    },
    toggleCollect() {
      if (!this.userId) { this.$message.warning('请先登录'); return }
      this.collectLoading = true
      if (this.isCollected) {
        this.$http.delete('/api/collection', { params: { userId: this.userId, movieId: this.movie.id } }).then(res => {
          this.collectLoading = false
          if (res.data.code === 200) { this.isCollected = false; this.$message.success('已取消收藏') }
        })
      } else {
        this.$http.post('/api/collection', { userId: this.userId, movieId: this.movie.id }).then(res => {
          this.collectLoading = false
          if (res.data.code === 200) { this.isCollected = true; this.$message.success('收藏成功') }
          else this.$message.error(res.data.msg)
        })
      }
    }
  }
}
</script>

<style scoped>
.detail-banner {
  display: flex;
  gap: 40px;
  margin-bottom: 30px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.05));
  border-radius: 16px;
  padding: 30px;
}
.detail-title { font-size: 30px; color: #fff; margin-bottom: 12px; }
.detail-tags { margin-bottom: 16px; }
.detail-meta { display: flex; flex-direction: column; gap: 10px; margin-bottom: 20px; }
.meta-item { color: rgba(255,255,255,0.65); font-size: 14px; }
.meta-item i { color: #667eea; margin-right: 6px; }
.detail-rating { margin-bottom: 20px; }
.rating-num { font-size: 36px; font-weight: 700; color: #ff9900; }
.detail-desc { background: rgba(255,255,255,0.03); border-radius: 12px; padding: 24px; margin-bottom: 30px; }
.detail-desc h3 { color: #fff; margin-bottom: 12px; font-size: 18px; }
.detail-desc p { color: rgba(255,255,255,0.6); line-height: 1.8; font-size: 14px; }

.screening-section h3 { color: #fff; font-size: 18px; margin-bottom: 16px; display: flex; align-items: center; gap: 8px; }
.no-screening { text-align: center; padding: 40px; color: #909399; }
.screening-list { display: flex; flex-direction: column; gap: 12px; }
.screening-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 12px;
  padding: 20px 24px;
  cursor: pointer;
  transition: all 0.3s;
}
.screening-card:hover {
  background: rgba(102, 126, 234, 0.08);
  border-color: rgba(102, 126, 234, 0.3);
  transform: translateX(4px);
}
.sc-start { font-size: 22px; font-weight: 700; color: #fff; }
.sc-end { font-size: 12px; color: rgba(255,255,255,0.4); margin-top: 2px; }
.sc-hall { display: flex; align-items: center; gap: 8px; color: rgba(255,255,255,0.7); font-size: 14px; }
.price-main { font-size: 20px; font-weight: 700; color: #f56c6c; text-align: right; }
.price-vip { font-size: 12px; color: #e6a23c; text-align: right; margin-top: 2px; }
</style>
