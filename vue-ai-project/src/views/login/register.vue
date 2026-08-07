<template>
  <div class="container">
    <div class="title">
      <div class="back-home" @click="router.push('/')">
        <el-icon>
          <Back />
        </el-icon>
        <span>返回首页</span>
      </div>
      <div class="title-text">
        <h2>创建您的账户</h2>
        <p>请填写注册信息</p>
      </div>
    </div>
    <div class="form-container">
      <el-form :model="formData" :rules="rules" ref="ruleFormRef" label-position="top">
        <el-form-item label="用户名或邮箱" prop="username">
          <el-input v-model="formData.username" size="large" placeholder="请输入用户名或邮箱" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" size="large" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" size="large" placeholder="请输入昵称（可选）" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" size="large" placeholder="请输入手机号（可选）" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" size="large" placeholder="请输入密码" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="formData.confirmPassword" size="large" placeholder="请再次输入密码" type="password"
            show-password />
        </el-form-item>
        <el-button class="btn" type="primary" size="large" @click="submitForm">注册</el-button>
      </el-form>
    </div>
    <div class="footer">
      <p>已有账户？<router-link to="/auth/login">立即登录</router-link></p>
    </div>
  </div>
</template>
<script setup>
import { ref } from "vue"
import { useRouter } from "vue-router"
import { ElMessage } from "element-plus"
import { RegisterAPI } from "@/apis/admin"

const ruleFormRef = ref(null)
const formData = ref({
  username: "",
  email: "",
  nickname: "",
  phone: "",
  password: "",
  confirmPassword: "",
  gender: 0,//性别 0：男 1：女
  userType: 1,//用户类型 1：前台用户 2：后台管理员
})
const rules = ref({
  username: [
    { required: true, message: "请输入用户名或邮箱", trigger: "blur" },
    { min: 3, max: 50, message: "用户名或邮箱长度在3-50之间", trigger: "blur" }
  ],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱格式", trigger: "blur" }
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度至少为6位", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== formData.value.password) {
          callback(new Error("两次输入的密码不一致"))
        } else {
          callback()
        }
      },
      trigger: "blur"
    }
  ]
})
const router = useRouter()

// 提交注册
const submitForm = async () => {
  if (!ruleFormRef.value) return

  await ruleFormRef.value.validate()

  // 注册接口
  await RegisterAPI(formData.value)

  ElMessage.success('注册成功')

  // 注册成功后跳转到登录页
  setTimeout(() => {
    router.push("/auth/login")
  }, 1000)
}
</script>
<style scoped lang="scss">
.container {
  width: 384px;

  .title {
    .back-home {
      margin-bottom: 60px;
    }

    .title-text {
      text-align: center;

      h2 {
        font-size: 36px;
        margin-bottom: 10px;
      }

      p {
        font-size: 20px;
        color: #6b7280;
      }
    }
  }

  .form-container {
    margin-top: 30px;

    .btn {
      margin-top: 40px;
      width: 100%;
    }
  }

  .footer {
    padding: 20px;
    text-align: center;
  }
}
</style>