<template>
  <div class="front-body">
    <!-- 导航栏 -->
    <header class="front-header">
      <div class="nav-container">
        <div class="nav-brand" @click="$router.push('/home')">
          <i class="el-icon-film" style="font-size:24px;color:#667eea;"></i>
          <span class="brand-name">电影院订票管理系统</span>
        </div>
        <nav class="nav-links">
          <router-link to="/home" class="nav-link">首页</router-link>
          <router-link to="/my-orders" class="nav-link" v-if="isLogin">我的订单</router-link>
          <router-link to="/user-center" class="nav-link" v-if="isLogin">个人中心</router-link>
        </nav>
        <div class="nav-right">
          <template v-if="isLogin">
            <el-dropdown @command="handleCommand">
              <span class="user-drop">
                <el-avatar :size="30" :src="user.avatar" icon="el-icon-user-solid"></el-avatar>
                <span>{{ user.nickname || user.username }}</span>
                <i class="el-icon-arrow-down"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="center" icon="el-icon-user">个人中心</el-dropdown-item>
                <el-dropdown-item command="orders" icon="el-icon-tickets">我的订单</el-dropdown-item>
                <el-dropdown-item command="logout" icon="el-icon-switch-button" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="text" class="login-btn-link" @click="$router.push('/login')">
              <i class="el-icon-user"></i> 登录 / 注册
            </el-button>
          </template>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="front-main">
      <transition name="fade">
        <router-view :key="$route.fullPath" />
      </transition>
    </main>

    <!-- 底部 -->
    <footer class="front-footer">
      <div class="footer-content">
        <div class="footer-brand">
          <i class="el-icon-film" style="font-size:20px;color:#667eea;"></i>
          <span>电影院订票管理系统 Star Cinema</span>
        </div>
        <div class="footer-links">
          <a href="#">关于我们</a>
          <a href="#">联系客服</a>
          <a href="#">隐私政策</a>
          <a href="#">用户协议</a>
        </div>
        <div class="footer-copy">© 2026 电影院订票管理系统 All Rights Reserved</div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'FrontLayout',
  computed: {
    isLogin() { return this.$store.getters.isLogin },
    user() { return this.$store.state.user || {} }
  },
  methods: {
    handleCommand(cmd) {
      if (cmd === 'logout') {
        this.$store.dispatch('logout')
        this.$message.success('已退出登录')
        this.$router.push('/home')
      } else if (cmd === 'center') {
        this.$router.push('/user-center')
      } else if (cmd === 'orders') {
        this.$router.push('/my-orders')
      }
    }
  }
}
</script>

<style scoped>
.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 64px;
}
.nav-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.brand-name {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2px;
}
.nav-links {
  display: flex;
  gap: 32px;
}
.nav-link {
  color: rgba(255,255,255,0.75);
  text-decoration: none;
  font-size: 15px;
  transition: all 0.3s;
  padding: 6px 0;
  border-bottom: 2px solid transparent;
}
.nav-link:hover, .nav-link.router-link-active {
  color: #fff;
  border-bottom-color: #667eea;
}
.nav-right { display: flex; align-items: center; }
.user-drop {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255,255,255,0.85);
  cursor: pointer;
  font-size: 14px;
}
.login-btn-link {
  color: rgba(255,255,255,0.85) !important;
  font-size: 14px;
}
.front-main {
  max-width: 1200px;
  margin: 0 auto;
  min-height: calc(100vh - 200px);
  padding: 30px 20px;
}
.front-footer {
  background: #111126;
  padding: 40px 20px;
  margin-top: 60px;
}
.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}
.footer-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  margin-bottom: 16px;
}
.footer-links {
  margin-bottom: 16px;
}
.footer-links a {
  color: rgba(255,255,255,0.5);
  text-decoration: none;
  margin: 0 16px;
  font-size: 13px;
  transition: color 0.3s;
}
.footer-links a:hover { color: #667eea; }
.footer-copy { color: rgba(255,255,255,0.3); font-size: 12px; }
</style>
