<template>
  <div class="login-container">
    <div class="login-bg">
      <div class="bg-circles">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
        <div class="circle circle-3"></div>
      </div>
    </div>
    <div class="login-box">
      <div class="login-left">
        <div class="brand">
          <i class="el-icon-film" style="font-size: 48px; margin-bottom: 16px;"></i>
          <h1>电影院订票管理系统</h1>
          <p>STAR CINEMA</p>
        </div>
        <div class="features">
          <div class="feature-item">
            <i class="el-icon-video-camera"></i>
            <span>海量影片资源</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-location"></i>
            <span>在线选座购票</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-medal"></i>
            <span>会员积分权益</span>
          </div>
        </div>
      </div>
      <div class="login-right">
        <h2>欢迎登录</h2>
        <p class="subtitle">请输入账号密码登录系统</p>
        <el-form :model="form" :rules="rules" ref="loginForm" class="login-form">
          <el-form-item prop="username">
            <el-input v-model="form.username" prefix-icon="el-icon-user" placeholder="请输入用户名" size="large" clearable></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" prefix-icon="el-icon-lock" placeholder="请输入密码" type="password" size="large" clearable show-password @keyup.enter.native="login"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-btn" @click="login" :loading="loading">登 录</el-button>
          </el-form-item>
          <div class="login-footer">
            <span>还没有账号？</span>
            <el-button type="text" @click="showRegister = true">立即注册</el-button>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 注册对话框 -->
    <el-dialog title="用户注册" :visible.sync="showRegister" width="420px" center>
      <el-form :model="regForm" :rules="regRules" ref="regForm" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="regForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="regForm.password" type="password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="regForm.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="regForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="regForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="showRegister = false">取 消</el-button>
        <el-button type="primary" @click="register">注 册</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      form: { username: '', password: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false,
      showRegister: false,
      regForm: { username: '', password: '', nickname: '', phone: '', email: '' },
      regRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
      }
    }
  },
  methods: {
    login() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return
        this.loading = true
        this.$http.post('/api/login', this.form).then(res => {
          this.loading = false
          if (res.data.code === 200) {
            const { user, role } = res.data.data
            this.$store.dispatch('login', user)
            this.$message.success('登录成功！')
            if (role === 'ADMIN') {
              this.$router.push('/admin')
            } else {
              this.$router.push('/home')
            }
          } else {
            this.$message.error(res.data.msg)
          }
        }).catch(() => {
          this.loading = false
          this.$message.error('网络错误，请稍后重试')
        })
      })
    },
    register() {
      this.$refs.regForm.validate(valid => {
        if (!valid) return
        this.$http.post('/api/register', this.regForm).then(res => {
          if (res.data.code === 200) {
            this.$message.success('注册成功，请登录')
            this.showRegister = false
            this.form.username = this.regForm.username
            this.form.password = this.regForm.password
          } else {
            this.$message.error(res.data.msg)
          }
        })
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  position: relative;
  overflow: hidden;
}

.login-bg { position: absolute; inset: 0; }
.bg-circles .circle {
  position: absolute;
  border-radius: 50%;
  animation: float 6s ease-in-out infinite;
}
.circle-1 {
  width: 300px; height: 300px;
  background: rgba(102, 126, 234, 0.15);
  top: -100px; right: -50px;
  animation-delay: 0s;
}
.circle-2 {
  width: 200px; height: 200px;
  background: rgba(118, 75, 162, 0.15);
  bottom: -50px; left: -30px;
  animation-delay: 2s;
}
.circle-3 {
  width: 150px; height: 150px;
  background: rgba(234, 102, 186, 0.1);
  top: 50%; left: 30%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.05); }
}

.login-box {
  display: flex;
  width: 820px;
  min-height: 480px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.5);
  position: relative;
  z-index: 10;
}

.login-left {
  width: 360px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #fff;
}

.brand { text-align: center; margin-bottom: 40px; }
.brand h1 { font-size: 32px; font-weight: 700; letter-spacing: 4px; }
.brand p { font-size: 14px; opacity: 0.8; margin-top: 4px; letter-spacing: 6px; }

.features { display: flex; flex-direction: column; gap: 16px; }
.feature-item {
  display: flex; align-items: center; gap: 12px;
  font-size: 15px; opacity: 0.9;
  padding: 10px 20px;
  background: rgba(255,255,255,0.1);
  border-radius: 10px;
  transition: all 0.3s;
}
.feature-item:hover { background: rgba(255,255,255,0.2); transform: translateX(5px); }
.feature-item i { font-size: 20px; }

.login-right {
  flex: 1;
  background: #fff;
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-right h2 { font-size: 26px; color: #1a1a2e; margin-bottom: 8px; }
.subtitle { color: #909399; margin-bottom: 32px; font-size: 14px; }

.login-form .el-input__inner {
  height: 46px;
  border-radius: 10px;
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  border-radius: 10px !important;
  letter-spacing: 8px;
}

.login-footer {
  text-align: center;
  color: #909399;
  font-size: 13px;
}
</style>
