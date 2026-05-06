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
        <h2>登录您的账号</h2>
        <p>登录后即可开始使用</p>
      </div>
    </div>
    <div class="form-container">
      <el-form :model="formData" :rules="rules" ref="ruleFormRef" label-position="top">
        <el-form-item label="用户名或邮箱" prop="username">
          <el-input v-model="formData.username" size="large" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" size="large" placeholder="请输入密码" type="password" show-password />
        </el-form-item>
        <el-button class="btn" type="primary" size="large" @click="submitForm">登录</el-button>
      </el-form>
    </div>
    <div class="footer">
      <p>还没有账号？<router-link to="/auth/register">去注册</router-link></p>
    </div>
  </div>
</template>
<script setup>
import { LoginAPI } from "@/apis/admin"
import { useAdminStore } from "@/stores/admin"
import { ElMessage } from "element-plus"
import { ref } from "vue"
import { useRouter } from "vue-router"

const ruleFormRef = ref(null)
const formData = ref({
  username: "xiaohao",
  password: "123456",
})
const rules = ref({
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
})
const router = useRouter()
const adminStore = useAdminStore()
const submitForm = async () => {
  if (!ruleFormRef.value) return

  await ruleFormRef.value.validate()
  // 登录接口
  //之前处理过登录接口返回的数据 所以这里直接使用res.token就可以了，不需要再处理一层data
  LoginAPI(formData.value).then((res) => {
    if (!res.token) {
      return ElMessage.error("登录失败")
    }
    ElMessage.success("登录成功")
    // 登录成功后，将token和userInfo存储到store中
    adminStore.setToken(res.token)
    adminStore.setUserInfo(res.userInfo)
    // 登录成功后，跳转  后台端/用户端
    if (res.userInfo.userType === 2) {
      // 后台端
      router.push("/back/dashboard")
    } else {
      // 用户端，跳转到首页即可
      router.push("/")
    }
  })
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
