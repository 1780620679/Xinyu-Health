<template>
  <div>
    <PageHead title="情绪日志"></PageHead>
    <TableSearch :formItem="formItem" @search="handleSearch"></TableSearch>
    <!-- 表格 -->
    <el-table :data="tableData" style="width: 100%;">
      <el-table-column label="用户ID" width="80" prop="userId" fixed="left"></el-table-column>
      <el-table-column label="会话ID" width="80">
        <template #default="scope">
          <el-avatar>{{ scope.row.nickname }}</el-avatar>
        </template>
      </el-table-column>
      <el-table-column label="记录日期" width="120" prop="diaryDate"></el-table-column>
      <el-table-column label="情绪评分" width="200">
        <template #default="scope">
          <el-rate :model-value="scope.row.moodScore" :max="10" disabled></el-rate>
        </template>
      </el-table-column>
      <el-table-column label="生活指标" width="200">
        <template #default="scope">
          <div>
            <p>睡眠质量：{{ scope.row.sleepQuality }}/5</p>
            <p>压力等级：{{ scope.row.stressLevel }}/5</p>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="情绪产生原因" width="200" prop="emotionTriggers"></el-table-column>
      <el-table-column label="日记内容" width="200" prop="diaryContent"></el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="scope">
          <el-button text type="primary" @click="handleEdit(scope.row)">详情</el-button>
          <el-button text type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination background :page-size="pagination.size" layout="prev, pager, next" :total="pagination.total"
      @change="handleCurrentChange" />
    <!-- 详情弹窗 -->
    <el-dialog width="800px" v-model="showDetailDialog" title="情绪日志详情">
      <div class="detail-content" v-if="sessionDetail">
        <!-- 用户信息 -->
        <div class="detail-section">
          <h4>用户信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户名">{{ sessionDetail.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ sessionDetail.nickname }}</el-descriptions-item>
            <el-descriptions-item label="用户ID">{{ sessionDetail.userId }}</el-descriptions-item>
            <el-descriptions-item label="记录日期">{{ sessionDetail.diaryDate }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <!-- 情绪状态 -->
        <div class="detail-section">
          <h4>情绪状态</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="情绪评分">
              <el-rate :model-value="sessionDetail.moodScore" :max="10" disabled show-score></el-rate>
            </el-descriptions-item>
            <el-descriptions-item label="主要情绪">
              <el-tag :type="getEmotionTagType(sessionDetail.dominantEmotion)">{{ sessionDetail.dominantEmotion || '无'
              }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="睡眠质量">{{ sessionDetail.sleepQuality || '无' }}/5</el-descriptions-item>
            <el-descriptions-item label="压力等级">{{ sessionDetail.stressLevel || '无' }}/5</el-descriptions-item>
          </el-descriptions>
        </div>
        <!-- 日记内容 -->
        <div class="detail-section">
          <h4>日记内容</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="情绪触发因素">{{ sessionDetail.emotionTriggers || '无' }}</el-descriptions-item>
            <el-descriptions-item label="日记内容">{{ sessionDetail.diaryContent || '无' }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <!-- AI情绪分析结果 -->
        <div class="detail-section">
          <h4>AI情绪分析结果</h4>
          <div class="ai-analysis-result">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="主要情绪">
                <el-tag :type="getAiEmotionTagType(aiData.primaryEmotion)">{{ aiData.primaryEmotion || '无' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="情绪强度">
                <el-progress :percentage="aiData.emotionScore"
                  :color="getEmotionScoreColor(aiData.emotionScore)"></el-progress>
              </el-descriptions-item>
              <el-descriptions-item label="风险等级">
                <el-tag :type="getRiskLevelTagType(aiData.riskLevel)">{{ getRiskLevelText(aiData.riskLevel) || '无'
                }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="情绪性质">
                <el-tag :type="aiData.isNegative ? 'danger' : 'success'">{{ aiData.isNegative ? '负向情绪' : '正向情绪'
                }}</el-tag>
              </el-descriptions-item>
            </el-descriptions>
            <!-- ai建议 -->
            <div class="ai-suggestion-section">
              <h5>专业建议</h5>
              <div class="suggestion-conntent">{{ aiData.suggestion || '无' }}</div>
            </div>
            <div class="ai-risk-section">
              <h5>风险描述</h5>
              <div class="risk-conntent">{{ aiData.riskDescription || '无' }}</div>
            </div>
            <div class="ai-improvements-section">
              <h5>改善建议</h5>
              <div class="improvements-conntent">
                <ul class="improvements-list">
                  <li v-for="item in aiData.improvementsSuggestionss" :key="item">{{ item }}</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
        <!-- 时间信息 -->
        <div class="detail-section">
          <h4>时间信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="更新时间">{{ sessionDetail.updatedAt }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ sessionDetail.createdAt }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <!-- 底部按钮 -->
      <template #footer>
        <el-button @click="showDetailDialog = false">
          关闭
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import PageHead from '@/components/PageHead.vue';
import TableSearch from '@/components/TableSearch.vue';
import { getEmotionalPageAPI, deleteEmotionalAPI } from '@/apis/emotional';
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

// 状态映射
const getEmotionTagType = (emotion) => {
  const emotionTypes = {
    '快乐': 'success',
    '平静': 'info',
    '兴奋': 'warning',
    '愤怒': 'danger',
    '悲伤': 'info',
    '焦虑': 'warning'
  }
  return emotionTypes[emotion] || 'info'
}
const getAiEmotionTagType = (emotion) => {
  const emotionTagMap = {
    '快乐': 'success',
    '平静': 'success',
    '兴奋': 'warning',
    '满足': 'success',
    '愤怒': 'danger',
    '悲伤': 'info',
    '焦虑': 'warning',
    '恐惧': 'danger',
    '沮丧': 'info',
    '压力': 'warning'
  }
  return emotionTagMap[emotion] || 'info'
}
const getEmotionScoreColor = (score) => {
  if (score >= 80) return '#f56c6c'
  if (score >= 60) return '#e6a23c'
  if (score >= 40) return '#909399'
  return '#67c23a'
}
const getRiskLevelTagType = (riskLevel) => {
  const riskTagMap = {
    0: 'success',
    1: 'info',
    2: 'warning',
    3: 'danger'
  }
  return riskTagMap[riskLevel] || 'info'
}
const getRiskLevelText = (riskLevel) => {
  const riskTextMap = {
    0: '正常',
    1: '关注',
    2: '预警',
    3: '危机'
  }
  return riskTextMap[riskLevel] || '未知风险等级'
}

// 搜索表单配置
const formItem = [
  {
    comp: "input",
    label: "用户ID",
    prop: "userId",
    placeholder: "请输入用户ID",
  },
  {
    comp: "select",
    label: "情绪评分",
    prop: "emotionScoreRange",
    placeholder: "请选择情绪评分",
    options: [
      {
        label: "差(1-3分)",
        value: '1-3',
      },
      {
        label: "中(4-6分)",
        value: '4-6',
      },
      {
        label: "好(7-10分)",
        value: '7-10',
      },
    ],
  },
]
//分页配置
const pagination = reactive({
  currentPage: 1,
  size: 10,
  total: 0,
})
// 表格数据
const tableData = ref([])


// 分页切换方法
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  handleSearch({})
}
// 搜索方法(子传父回调来的数据)
const handleSearch = async (formData) => {
  // 合并分页配置和搜索表单数据
  const params = {
    ...pagination,
    ...formData,
  }// 获取情绪日志列表
  const { records, total } = await getEmotionalPageAPI(params)
  tableData.value = records
  pagination.total = total
}
// 处理详情点击事件
const showDetailDialog = ref(false)
const aiData = ref(null)
const sessionDetail = ref(null)
const handleEdit = (row) => {
  sessionDetail.value = row
  /// 处理AI情绪分析结果决定是否显示
  if (row.aiEmotionAnalysis) {
    aiData.value = JSON.parse(row.aiEmotionAnalysis)
  }
  else {
    aiData.value = {}
  }
  showDetailDialog.value = true
}
// 处理删除点击事件
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该情绪日志吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'danger'
  }).then(async () => {
    // 调用删除接口
    await deleteEmotionalAPI(row.id)
    ElMessage.success('删除成功');
    // 刷新表格数据
    handleSearch({})
  }).catch(() => {
    // 取消删除
  })
}

onMounted(() => {
  handleSearch({})
})
</script>

<style scoped lang="scss">
.detail-content {
  .detail-section {
    margin-bottom: 24px;

    h4 {
      margin: 0 0 16px 0;
      color: #303133;
      font-size: 16px;

      i {
        margin-right: 8px;
        color: #409eff;
      }
    }
  }
}

// AI分析相关样式
.ai-analysis-status {
  .ai-status-tag {
    margin-bottom: 4px;

    i {
      margin-right: 4px;
    }
  }

  .ai-analysis-preview {
    font-size: 11px;
    color: #909399;
    margin-top: 2px;
  }
}

.ai-analysis-result {

  .ai-keywords-section,
  .ai-suggestion-section,
  .ai-risk-section,
  .ai-improvements-section {
    margin-top: 16px;
    padding: 12px;
    background-color: #f8f9fa;
    border-radius: 4px;

    h5 {
      margin: 0 0 8px 0;
      color: #606266;
      font-size: 14px;
      font-weight: 600;

      i {
        margin-right: 6px;
        color: #909399;
      }
    }
  }

  .keywords-container {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;

    .keyword-tag {
      background-color: #e1f3d8;
      color: #67c23a;
      border-color: #b3d8a4;
    }
  }

  .suggestion-content,
  .risk-content {
    line-height: 1.6;
    color: #606266;
    background-color: white;
    padding: 8px;
    border-radius: 4px;
    border: 1px solid #ebeef5;
  }

  .improvement-list {
    margin: 0;
    padding-left: 20px;

    li {
      margin-bottom: 4px;
      color: #606266;
      line-height: 1.5;
    }
  }

  .ai-analysis-meta {
    margin-top: 16px;
    padding-top: 12px;
    border-top: 1px solid #ebeef5;

    .analysis-time {
      margin: 0;
      font-size: 12px;
      color: #909399;

      i {
        margin-right: 4px;
      }
    }
  }

  .el-progress {
    .el-progress__text {
      font-size: 12px !important;
    }
  }
}
</style>
