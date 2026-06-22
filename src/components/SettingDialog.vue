<template>
  <el-dialog v-model="visible" title="系统设置" width="500px" :close-on-click-modal="false">
    <div class="setting-content">
      <!-- 主题设置 -->
      <div class="setting-section">
        <h4 class="section-title">主题设置</h4>
        <div class="setting-item">
          <div class="item-label">
            <span>暗黑模式</span>
            <span class="item-desc">切换浅色/深色主题</span>
          </div>
          <el-switch v-model="localIsDark" @change="handleThemeChange" />
        </div>
      </div>

      <!-- 侧边栏设置 -->
      <div class="setting-section">
        <h4 class="section-title">布局设置</h4>
        <div class="setting-item">
          <div class="item-label">
            <span>侧边栏折叠</span>
            <span class="item-desc">收起左侧导航菜单</span>
          </div>
          <el-switch v-model="localIsCollapse" @change="handleCollapseChange" />
        </div>
      </div>

      <!-- 数据设置 -->
      <div class="setting-section">
        <h4 class="section-title">数据管理</h4>
        <div class="setting-item">
          <div class="item-label">
            <span>清除本地缓存</span>
            <span class="item-desc">清除浏览器存储的用户偏好数据</span>
          </div>
          <el-button type="danger" size="small" @click="handleClearCache">清除</el-button>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const AdminStore = useAdminStore()

// 本地状态副本（避免直接修改 store）
const localIsDark = ref(AdminStore.isDark)
const localIsCollapse = ref(AdminStore.isCollapse)

// v-model 双向绑定
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 弹窗打开时同步最新状态
watch(() => props.modelValue, (val) => {
  if (val) {
    localIsDark.value = AdminStore.isDark
    localIsCollapse.value = AdminStore.isCollapse
  }
})

// 主题切换
const handleThemeChange = (val) => {
  AdminStore.isDark = val
  if (val) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}

// 侧边栏折叠
const handleCollapseChange = (val) => {
  AdminStore.isCollapse = val
}

// 清除缓存
const handleClearCache = () => {
  ElMessageBox.confirm('确定要清除本地缓存吗？这将重置你的偏好设置。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('ai-health-admin')
    ElMessage.success('缓存已清除，请刷新页面')
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.setting-content {
  .setting-section {
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
      margin-bottom: 0;
    }

    .section-title {
      margin: 0 0 16px 0;
      font-size: 15px;
      font-weight: 600;
      color: #1f2937;
    }

    .setting-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 8px 0;

      .item-label {
        display: flex;
        flex-direction: column;

        span:first-child {
          font-size: 14px;
          color: #374151;
          margin-bottom: 4px;
        }

        .item-desc {
          font-size: 12px;
          color: #9ca3af;
        }
      }
    }
  }
}
</style>
