<template>
  <div>
    <PageHead title="咨询记录"></PageHead>
    <el-table :data="tableData" style="width: 100%;">
      <el-table-column label="会话ID" width="100">
        <template #default="scope">
          <el-avatar>{{ scope.row.userNickname }}</el-avatar>
        </template>
      </el-table-column>
      <el-table-column label="情绪日志">
        <template #default="scope">
          <div class="session-title">{{ scope.row.sessionTitle }}</div>
          <div class="session-preview">{{ scope.row.lastMessageContent }}</div>
        </template>
      </el-table-column>
      <el-table-column label="消息数量" width="100" prop="messageCount">
      </el-table-column>
      <el-table-column label="时间" width="100" prop="lastMessageTime"></el-table-column>
      <el-table-column label="详情" width="100">
        <template #default="scope">
          <el-button text type="primary" @click="handleSessionDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background :page-size="pagination.size" layout="prev, pager, next" :total="pagination.total"
      @change="handleCurrentChange" />
    <el-dialog title="会话详情" v-model="showDetailDialog" width="70%">
      <div class="session-detail">
        <div class="detail-header">
          <div class="detail-row">
            <div class="detail-label">用户ID</div>
            <div class="detail-value">{{ sessionDetail.userId }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">消息数</div>
            <div class="detail-value">{{ sessionDetail.messageCount }}</div>
          </div>
          <div class="detail-row">
            <div class="detail-label">开始时间</div>
            <div class="detail-value">{{ sessionDetail.startdAt }}</div>
          </div>
        </div>
        <div class="messages-container">
          <div class="messages-header">
            <h4>消息详情</h4>
          </div>
          <div class="messages-list" v-loading="loading">
            <div v-for="message in sessionDetail.messages" :key="message.id" class="message-item"
              :class="message.senderType === 1 ? 'user-message' : 'ai-message'">
              <div class="message-header">
                <span class="sender">{{ message.senderType === 1 ? '用户' : 'AI' }}</span>
                <span class="time">{{ message.createdAt }}</span>
              </div>
              <div class="message-content">{{ message.content }}</div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">
          关闭
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { getConsultationDataAPI, getSessionMessagesAPI } from '@/apis/consultation';
import PageHead from '@/components/PageHead.vue';
import { onMounted, ref } from 'vue';

// 表格数据
const tableData = ref([])
//分页配置
const pagination = ref({
  total: 0,
  size: 10,
  currentPage: 1,
})
// 获取咨询记录数据
const handleSearch = async () => {
  const res = await getConsultationDataAPI(pagination.value)
  const { records, total } = res
  tableData.value = records
  pagination.value.total = total
}

// 处理会话详情点击事件
const showDetailDialog = ref(false)
const sessionDetail = ref({})
// 加载状态
const loading = ref(false)

const handleSessionDetail = async (row) => {
  loading.value = true
  sessionDetail.value = row
  showDetailDialog.value = true
  // 获取会话消息详情列表
  const messages = await getSessionMessagesAPI(row.id)
  sessionDetail.value.messages = messages
  loading.value = false
}
// 分页切换方法
const handleCurrentChange = (page) => {
  pagination.value.currentPage = page
  // 分页切换时，需要重新获取数据
  handleSearch()
}


onMounted(() => {
  handleSearch()
})
</script>
<style scoped lang="scss">
.session-title {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.session-preview {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.session-detail {
  max-height: 70vh;
  overflow-y: auto;

  .detail-header {
    margin-bottom: 20px;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e9ecef;
  }

  .detail-row {
    display: flex;
    align-items: center;
    margin-bottom: 8px;

    :last-child {
      margin-bottom: 0;
    }

    .detail-label {
      font-weight: 500;
      color: #495057;
      min-width: 80px;
      margin-right: 8px;
    }

    .detail-value {
      color: #333;
    }
  }
}

.messages-container {
  margin-top: 20px;

  .messages-header {
    margin-bottom: 16px;

    h4 {
      margin: 0;
      color: #333;
      font-size: 16px;
      font-weight: 500;
    }
  }

  .messages-list {
    max-height: 400px;
    overflow-y: auto;
    border: 1px solid #e9ecef;
    border-radius: 8px;
    padding: 16px;
    background: #fff;

    .message-item {
      margin-bottom: 12px;
      padding: 12px;
      border-radius: 8px;
      background: #f8f9fa;
      border: 1px solid #e9ecef;

      :last-child {
        margin-bottom: 0;
      }

      &.user-message {
        background: #e8f4fd;
      }

      &.ai-message {
        background: #f0f9f0;
      }
    }

    .message-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .sender {
        font-weight: 500;
        color: #333;
        display: flex;
        align-items: center;
        gap: 4px;
      }

      .time {
        font-size: 12px;
        color: #999;
      }

      .message-content {
        color: #333;
        line-height: 1.6;
        white-space: pre-wrap;
        margin-top: 8px;
        font-size: 14px;
      }
    }
  }
}
</style>
