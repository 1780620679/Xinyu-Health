<template>
  <div class="emotionDiary-container">
    <!-- 头部 -->
    <div class="header-section">
      <div class="header-content">
        <el-image :src="likeUrl" style="width: 60px; height: 60px;" />
        <h1>情绪日记</h1>
      </div>
    </div>
    <!-- 内容 -->
    <div class="content">
      <!-- 情绪评分 -->
      <div class="diary-card">
        <div class="title">为自己打分</div>
        <div class="section">
          <p>请根据今日的情绪状态，给自己打分。</p>
          <div class="rate">
            <el-rate v-model="diaryForm.moodScore" show-text :texts="emotionStatus" :max="10" size="large" />
          </div>
        </div>
      </div>
      <!-- 主要情绪 -->
      <div class="diary-card">
        <div class="title">主要情绪</div>
        <div class="emotion-grid">
          <div class="emotion-card" v-for="emotion in emotionOptions" :key="emotion.name"
            @click="selectEmotion(emotion.name)" :class="{ 'selected': emotion.name == diaryForm.dominantEmotion }">
            <el-image :src="emotion.url" style="width: 50px; height: 50px;" />
            <div class="emotion-name">{{ emotion.name }}</div>
          </div>
        </div>
      </div>
      <!-- 详细记录 -->
      <div class="diary-card">
        <div class="title">详细记录</div>
        <div class="detail-form">
          <!-- 情绪触发因素 -->
          <div class="form-group">
            <div class="form-label">情绪触发因素</div>
            <el-input v-model="diaryForm.emotionTriggers" type="textarea" :rows="3" show-word-limit maxlength="1000"
              placeholder="请输入情绪触发因素" />
          </div>
          <!-- 今日感想 -->
          <div class="form-group">
            <div class="form-label">今日感想</div>
            <el-input v-model="diaryForm.diaryContent" type="textarea" :rows="5" show-word-limit maxlength="2000"
              placeholder="请输入今日感想" />
          </div>
          <!-- 生活指标 -->
          <div class="life-indicators">
            <div class="indicator-group">
              <div class="form-label">睡眠质量</div>
              <el-select v-model="diaryForm.sleepQuality" placeholder="请选择睡眠质量">
                <el-option label="很差" :value="1" />
                <el-option label="较差" :value="2" />
                <el-option label="一般" :value="3" />
                <el-option label="良好" :value="4" />
                <el-option label="优秀" :value="5" />
              </el-select>
            </div>
            <div class="indicator-group">
              <div class="form-label">压力水平</div>
              <el-select v-model="diaryForm.stressLevel" placeholder="请选择压力水平">
                <el-option label="很低" :value="1" />
                <el-option label="较低" :value="2" />
                <el-option label="中等" :value="3" />
                <el-option label="较高" :value="4" />
                <el-option label="很高" :value="5" />
              </el-select>
            </div>
          </div>
          <!-- 提交按钮 -->
          <div class="action-buttons">
            <el-button @click="resetForm">重置</el-button>
            <el-button type="primary" @click="submitForm">提交</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { dayjs, ElMessage } from 'element-plus'
import { createOrUpdateEmotionDiaryAPI } from '@/apis/frontend/emotionDiary'
const likeUrl = new URL('@/assets/images/like.png', import.meta.url).href
// 情绪状态
const emotionStatus = ['绝望崩溃', '消沉抑郁', '焦虑烦躁', '低落不悦', '平静淡然', '轻松惬意', '愉悦舒心', '欢欣满足', '兴奋欣喜', '极致幸福']
//情绪选项,从@/assets/images/里读取
const emotionOptions = [
  { name: '开心', url: new URL('@/assets/images/开心.png', import.meta.url).href },
  { name: '悲伤', url: new URL('@/assets/images/悲伤.png', import.meta.url).href },
  { name: '焦虑', url: new URL('@/assets/images/焦虑.png', import.meta.url).href },
  { name: '兴奋', url: new URL('@/assets/images/兴奋.png', import.meta.url).href },
  { name: '平静', url: new URL('@/assets/images/平静.png', import.meta.url).href },
  { name: '惊讶', url: new URL('@/assets/images/惊讶.png', import.meta.url).href },
  { name: '困惑', url: new URL('@/assets/images/困惑.png', import.meta.url).href },
  { name: '疲惫', url: new URL('@/assets/images/疲惫.png', import.meta.url).href },
]
// 选择主要情绪
const selectEmotion = (emotion) => {
  diaryForm.value.dominantEmotion = emotion
}
// 情绪日记表单数据
const diaryForm = ref({
  diaryDate: dayjs().format('YYYY-MM-DD'),//记录日期
  moodScore: null,//情绪评分（1-1null）
  dominantEmotion: '',//重要情绪
  emotionTriggers: '',//情绪触发因素
  diaryContent: '',//今日感想
  sleepQuality: null,//睡眠质量
  stressLevel: null,//压力水平
})
// 重置表单
const resetForm = () => {
  Object.assign(diaryForm.value, {
    diaryDate: dayjs().format('YYYY-MM-DD'),//记录日期
    moodScore: null,//情绪评分（1-1null）
    dominantEmotion: '',//重要情绪
    emotionTriggers: '',//情绪触发因素
    diaryContent: '',//今日感想
    sleepQuality: null,//睡眠质量
    stressLevel: null,//压力水平
  })
}
// 提交表单
const submitForm = async () => {
  //简单校验
  if (!diaryForm.value.moodScore || !diaryForm.value.dominantEmotion || !diaryForm.value.emotionTriggers || !diaryForm.value.diaryContent || !diaryForm.value.sleepQuality || !diaryForm.value.stressLevel) {
    ElMessage.error('请填写完整信息')
    return
  }
  // 提交表单
  // 调用后端接口提交数据
  await createOrUpdateEmotionDiaryAPI(diaryForm.value)
  ElMessage.success('提交成功')
  // 重置表单
  resetForm()
}

</script>
<style scoped lang="scss">
.emotionDiary-container {
  background: linear-gradient(135deg, #fafbfc 0%, #f7f9fc 50%, #f2f6fa 100%);

  .header-section {
    margin: 0 auto;
    width: 881px;
    background: linear-gradient(135deg, #d32158a3 0%, #f54323bf 100%);
    // background: linear-gradient(135deg, #7ED321 0%, #F5A623 100%);
    border-radius: 10px;
    color: white;
    padding: 48px;

    .header-content {
      display: flex;
      align-items: center;
      gap: 12px;
    }
  }

  .content {
    margin: 0 auto;
    width: 980px;
    padding: 20px;

    .diary-card {
      margin-bottom: 20px;
      background: white;
      border-radius: 10px;
      padding: 20px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);

      .title {
        margin-bottom: 20px;
        font-size: 25px;
        font-weight: 600;
        color: #374151;
      }

      .section {
        margin-bottom: 20px;

        p {
          font-size: 15px;
          color: #6B7280;
          margin-bottom: 15px;
        }
      }

      .emotion-grid {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;

        .emotion-card {
          padding: 15px;
          border: 2px solid #E5E7EB;
          border-radius: 15px;
          text-align: center;
          cursor: pointer;
          background: #F9FAFB;

          .emotion-name {
            margin-top: 10px;
            padding: 0 75px;
            color: #374151;
          }

          &.selected {
            border-color: #7ED321;
            background: #F0FDF4;
            transform: translateY(-3px);
          }
        }
      }

      .detail-form {
        .form-label {
          margin: 10px 0;
          color: #374151;
        }

        .life-indicators {
          display: flex;
          gap: 20px;

          .indicator-group {
            flex: 1;
          }
        }

        .action-buttons {
          margin-top: 40px
        }
      }
    }
  }
}
</style>
