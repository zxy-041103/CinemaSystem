<template>
  <div class="home-page">
    <!-- 顶部Banner -->
    <div class="hero-banner">
      <div class="hero-content">
        <h1 class="hero-title">探索光影世界</h1>
        <p class="hero-subtitle">精选好片，在线选座，享受极致观影体验</p>
        <div class="hero-stats">
          <div class="hero-stat"><span class="stat-num">{{ showingMovies.length }}</span><span class="stat-desc">正在热映</span></div>
          <div class="hero-stat"><span class="stat-num">{{ upcomingMovies.length }}</span><span class="stat-desc">即将上映</span></div>
          <div class="hero-stat"><span class="stat-num">6</span><span class="stat-desc">放映厅</span></div>
        </div>
      </div>
      <div class="hero-poster" v-if="showingMovies.length > 0">
        <div class="poster-stack">
          <img v-for="(m, i) in showingMovies.slice(0, 3)" :key="m.id" :src="m.poster" class="stacked-poster" :style="{ transform: `translateX(${i * 30}px) rotate(${(i - 1) * 5}deg)`, zIndex: 3 - i }" @click="$router.push('/movie/' + m.id)" />
        </div>
      </div>
    </div>

    <!-- 正在热映 -->
    <section class="section">
      <div class="section-header">
        <h2><i class="el-icon-video-camera-solid" style="color:#f56c6c;"></i> 正在热映</h2>
        <span class="section-badge">{{ showingMovies.length }}部影片</span>
      </div>
      <div class="movie-grid">
        <div v-for="movie in showingMovies" :key="movie.id" class="movie-card" @click="$router.push('/movie/' + movie.id)">
          <div class="poster-wrap">
            <img :src="movie.poster" class="poster" :alt="movie.name" />
            <div class="poster-overlay">
              <el-button type="primary" size="small" round>立即购票</el-button>
            </div>
            <div class="rating-badge" v-if="movie.rating > 0">{{ movie.rating }}</div>
          </div>
          <div class="movie-info">
            <h3 class="movie-name">{{ movie.name }}</h3>
            <p class="movie-meta">{{ movie.genre }}</p>
            <p class="movie-meta">{{ movie.director }} 导演 | {{ movie.duration }}分钟</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 即将上映 -->
    <section class="section" v-if="upcomingMovies.length > 0">
      <div class="section-header">
        <h2><i class="el-icon-time" style="color:#e6a23c;"></i> 即将上映</h2>
        <span class="section-badge upcoming">{{ upcomingMovies.length }}部影片</span>
      </div>
      <div class="movie-grid">
        <div v-for="movie in upcomingMovies" :key="movie.id" class="movie-card" @click="$router.push('/movie/' + movie.id)">
          <div class="poster-wrap">
            <img :src="movie.poster" class="poster" :alt="movie.name" />
            <div class="poster-overlay">
              <el-button size="small" round plain style="color:#fff;border-color:rgba(255,255,255,0.6);">查看详情</el-button>
            </div>
            <div class="upcoming-badge">即将上映</div>
          </div>
          <div class="movie-info">
            <h3 class="movie-name">{{ movie.name }}</h3>
            <p class="movie-meta">{{ movie.genre }}</p>
            <p class="movie-meta">{{ movie.releaseDate }} 上映</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return { showingMovies: [], upcomingMovies: [] }
  },
  mounted() {
    this.$http.get('/api/movie/showing').then(res => {
      if (res.data.code === 200) this.showingMovies = res.data.data
    })
    this.$http.get('/api/movie/upcoming').then(res => {
      if (res.data.code === 200) this.upcomingMovies = res.data.data
    })
  }
}
</script>

<style scoped>
.hero-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.1));
  border-radius: 20px;
  padding: 50px 60px;
  margin-bottom: 50px;
  position: relative;
  overflow: hidden;
}
.hero-banner::before {
  content: '';
  position: absolute;
  width: 300px; height: 300px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.1), transparent);
  border-radius: 50%;
  top: -100px; right: -50px;
}
.hero-title {
  font-size: 42px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.hero-subtitle {
  font-size: 16px;
  color: rgba(255,255,255,0.6);
  margin-bottom: 30px;
}
.hero-stats { display: flex; gap: 40px; }
.hero-stat { text-align: center; }
.stat-num { display: block; font-size: 32px; font-weight: 700; color: #667eea; }
.stat-desc { font-size: 13px; color: rgba(255,255,255,0.5); }
.poster-stack { position: relative; width: 250px; height: 350px; }
.stacked-poster {
  position: absolute;
  width: 180px;
  height: 260px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.4);
  cursor: pointer;
  transition: all 0.3s;
}
.stacked-poster:hover { transform: translateY(-10px) !important; }

.section { margin-bottom: 50px; }
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}
.section-header h2 {
  font-size: 22px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 8px;
}
.section-badge {
  background: rgba(245, 108, 108, 0.2);
  color: #f56c6c;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
}
.section-badge.upcoming {
  background: rgba(230, 162, 60, 0.2);
  color: #e6a23c;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.movie-card {
  border-radius: 12px;
  overflow: hidden;
  background: #1a1a2e;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.movie-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 40px rgba(102, 126, 234, 0.2);
}

.poster-wrap {
  position: relative;
  overflow: hidden;
}
.poster {
  width: 100%;
  height: 300px;
  object-fit: cover;
  transition: transform 0.4s ease;
  display: block;
}
.movie-card:hover .poster {
  transform: scale(1.08);
}
.poster-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}
.movie-card:hover .poster-overlay { opacity: 1; }

.rating-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(245, 108, 108, 0.9);
  color: #fff;
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 700;
}
.upcoming-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: rgba(230, 162, 60, 0.9);
  color: #fff;
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.movie-info { padding: 14px; }
.movie-name {
  font-size: 15px;
  color: #fff;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.movie-meta {
  font-size: 12px;
  color: rgba(255,255,255,0.45);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-top: 3px;
}
</style>
