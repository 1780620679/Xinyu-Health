<template>
  <el-dialog v-model="visible" title="用户中心" width="500px" :close-on-click-modal="false">
    <div class="user-center">
      <!-- 头像区域 -->
      <div class="avatar-section">
        <el-avatar :size="80" :src="avatarUrl" />
        <h3 class="username">{{ userInfo.username || '未登录用户' }}</h3>
        <el-tag type="primary" size="small">{{ userTypeText }}</el-tag>
      </div>

      <!-- 信息列表 -->
      <el-descriptions :column="1" border class="info-list">
        <el-descriptions-item label="用户名">
          {{ userInfo.username || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="用户类型">
          {{ userTypeText }}
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ userInfo.createdAt || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="最后更新">
          {{ userInfo.updatedAt || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { useAdminStore } from '@/stores/admin'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const AdminStore = useAdminStore()
const userInfo = computed(() => AdminStore.userInfo)

// 头像
const avatarUrl = computed(() => {
  return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
})

// 用户类型映射
const userTypeText = computed(() => {
  const type = userInfo.value.userType
  if (type === 1) return '前台用户'
  if (type === 2) return '后台管理员'
  return '未知类型'
})

// v-model 双向绑定
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})
</script>

<style lang="scss" scoped>
.user-center {
  .avatar-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 24px;
    padding: 16px 0;
    border-bottom: 1px solid #f0f0f0;

    .username {
      margin: 12px 0 8px 0;
      font-size: 18px;
      font-weight: 600;
      color: #1f2937;
    }
  }

  .info-list {
    margin-top: 16px;
  }
}
</style>
