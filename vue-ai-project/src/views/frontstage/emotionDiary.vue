<template>
  <div class="emotionDiary-container">
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-image :src="heartUrl" style="width: 50px; height: 50px;" />
        </div>
        <div class="header-text">
          <h1>情绪日记</h1>
          <p>记录每一天的心情，感受成长的力量</p>
        </div>
      </div>
    </div>

    <div class="content">
      <div class="diary-card">
        <div class="card-header">
          <span class="card-icon">📊</span>
          <span class="card-title">为自己打分</span>
        </div>
        <div class="card-body">
          <p class="card-hint">请根据今日的情绪状态，给自己打分。</p>
          <div class="rate">
            <el-rate v-model="diaryForm.moodScore" show-text :texts="emotionStatus" :max="10" size="large" />
          </div>
        </div>
      </div>

      <div class="diary-card">
        <div class="card-header">
          <span class="card-icon">💝</span>
          <span class="card-title">主要情绪</span>
        </div>
        <div class="card-body">
          <div class="emotion-grid">
            <div class="emotion-card" v-for="emotion in emotionOptions" :key="emotion.name"
              @click="selectEmotion(emotion.name)" :class="{ 'selected': emotion.name == diaryForm.dominantEmotion }">
              <el-image lazy :src="emotion.url" style="width: 50px; height: 50px;" />
              <div class="emotion-name">{{ emotion.name }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="diary-card">
        <div class="card-header">
          <span class="card-icon">📝</span>
          <span class="card-title">详细记录</span>
        </div>
        <div class="card-body">
          <div class="detail-form">
            <div class="form-group">
              <div class="form-label">
                <span class="label-icon">💭</span>
                情绪触发因素
              </div>
              <el-input v-model="diaryForm.emotionTriggers" type="textarea" :rows="3" show-word-limit maxlength="1000"
                placeholder="请输入情绪触发因素" />
            </div>
            <div class="form-group">
              <div class="form-label">
                <span class="label-icon">✨</span>
                今日感想
              </div>
              <el-input v-model="diaryForm.diaryContent" type="textarea" :rows="5" show-word-limit maxlength="2000"
                placeholder="请输入今日感想" />
            </div>
            <div class="life-indicators">
              <div class="indicator-group">
                <div class="form-label">
                  <span class="label-icon">🌙</span>
                  睡眠质量
                </div>
                <el-select v-model="diaryForm.sleepQuality" placeholder="请选择睡眠质量">
                  <el-option label="很差" :value="1" />
                  <el-option label="较差" :value="2" />
                  <el-option label="一般" :value="3" />
                  <el-option label="良好" :value="4" />
                  <el-option label="优秀" :value="5" />
                </el-select>
              </div>
              <div class="indicator-group">
                <div class="form-label">
                  <span class="label-icon">📈</span>
                  压力水平
                </div>
                <el-select v-model="diaryForm.stressLevel" placeholder="请选择压力水平">
                  <el-option label="很低" :value="1" />
                  <el-option label="较低" :value="2" />
                  <el-option label="中等" :value="3" />
                  <el-option label="较高" :value="4" />
                  <el-option label="很高" :value="5" />
                </el-select>
              </div>
            </div>
            <div class="action-buttons">
              <el-button class="btn-reset" @click="resetForm">重置</el-button>
              <el-button class="btn-submit" type="primary" @click="submitForm">提交日记</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { dayjs, ElMessage } from 'element-plus'
import { createOrUpdateEmotionDiaryAPI } from '@/apis/frontend/emotionDiary'
import type { CreateOrUpdateEmotionDiaryParams } from '@/types/frontstage/emotionDiary'
const heartUrl = new URL('@/assets/images/like.png', import.meta.url).href
// 情绪状态
const emotionStatus: string[] = ['绝望崩溃', '消沉抑郁', '焦虑烦躁', '低落不悦', '平静淡然', '轻松惬意', '愉悦舒心', '欢欣满足', '兴奋欣喜', '极致幸福']

// 情绪选项类型
interface EmotionOption {
  name: string;
  url: string;
}

//情绪选项,从@/assets/images/里读取
const emotionOptions: EmotionOption[] = [
  { name: '开心', url: new URL('@/assets/images/开心.png', import.meta.url).href },
  { name: '悲伤', url: new URL('@/assets/images/悲伤.png', import.meta.url).href },
  { name: '焦虑', url: new URL('@/assets/images/焦虑.png', import.meta.url).href },
  { name: '兴奋', url: new URL('@/assets/images/兴奋.png', import.meta.url).href },
  { name: '平静', url: new URL('@/assets/images/平静.png', import.meta.url).href },
  { name: '惊讶', url: new URL('@/assets/images/惊讶.png', import.meta.url).href },
  { name: '困惑', url: new URL('@/assets/images/困惑.png', import.meta.url).href },
  { name: '疲惫', url: new URL('@/assets/images/疲惫.png', import.meta.url).href },
]

// 情绪日记表单数据类型
interface DiaryForm {
  diaryDate: string;
  moodScore: number | null;
  dominantEmotion: string;
  emotionTriggers: string;
  diaryContent: string;
  sleepQuality: number | null;
  stressLevel: number | null;
}

// 选择主要情绪
const selectEmotion = (emotion: string): void => {
  diaryForm.value.dominantEmotion = emotion
}

// 情绪日记表单数据
const diaryForm = ref<DiaryForm>({
  diaryDate: dayjs().format('YYYY-MM-DD'),//记录日期
  moodScore: null,//情绪评分（1-10）
  dominantEmotion: '',//重要情绪
  emotionTriggers: '',//情绪触发因素
  diaryContent: '',//今日感想
  sleepQuality: null,//睡眠质量
  stressLevel: null,//压力水平
})

// 重置表单
const resetForm = (): void => {
  Object.assign(diaryForm.value, {
    diaryDate: dayjs().format('YYYY-MM-DD'),//记录日期
    moodScore: null,//情绪评分（1-10）
    dominantEmotion: '',//重要情绪
    emotionTriggers: '',//情绪触发因素
    diaryContent: '',//今日感想
    sleepQuality: null,//睡眠质量
    stressLevel: null,//压力水平
  })
}

// 提交表单
const submitForm = async (): Promise<void> => {
  //简单校验
  if (!diaryForm.value.moodScore || !diaryForm.value.dominantEmotion || !diaryForm.value.emotionTriggers || !diaryForm.value.diaryContent || !diaryForm.value.sleepQuality || !diaryForm.value.stressLevel) {
    ElMessage.error('请填写完整信息')
    return
  }

  // 准备提交数据
  const submitData: CreateOrUpdateEmotionDiaryParams = {
    diaryDate: diaryForm.value.diaryDate,
    diaryContent: diaryForm.value.diaryContent,
    moodScore: diaryForm.value.moodScore,
    dominantEmotion: diaryForm.value.dominantEmotion,
    emotionTriggers: diaryForm.value.emotionTriggers,
    sleepQuality: diaryForm.value.sleepQuality,
    stressLevel: diaryForm.value.stressLevel,
  }

  // 提交表单
  // 调用后端接口提交数据
  await createOrUpdateEmotionDiaryAPI(submitData)
  ElMessage.success('提交成功')
  // 重置表单
  resetForm()
}
</script>
<style scoped lang="scss">
.emotionDiary-container {
  background: linear-gradient(180deg, #FFF9F5 0%, #FFF5F0 100%);
  min-height: calc(100vh - 70px);
  padding-bottom: 60px;

  html.dark & {
    background: linear-gradient(180deg, #1a1a1a 0%, #252525 100%);
  }

  .page-header {
    background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
    padding: 50px 40px;

    .header-content {
      max-width: 980px;
      margin: 0 auto;
      display: flex;
      align-items: center;
      gap: 20px;

      .header-icon {
        width: 80px;
        height: 80px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        backdrop-filter: blur(10px);
      }

      .header-text {
        h1 {
          font-size: 36px;
          font-weight: 700;
          color: white;
          margin: 0 0 8px;
          font-family: 'Microsoft YaHei', sans-serif;
        }

        p {
          font-size: 16px;
          color: rgba(255, 255, 255, 0.9);
          margin: 0;
          font-family: 'Microsoft YaHei', sans-serif;
        }
      }
    }
  }

  .content {
    max-width: 980px;
    margin: 0 auto;
    padding: 30px 20px;

    .diary-card {
      background: white;
      border-radius: 20px;
      padding: 28px;
      margin-bottom: 24px;
      box-shadow: 0 4px 20px rgba(255, 107, 107, 0.06);
      border: 1px solid rgba(255, 107, 107, 0.08);

      html.dark & {
        background: #2d2d2d;
      }

      .card-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 24px;
        padding-bottom: 16px;
        border-bottom: 2px solid #FFF5F0;

        html.dark & {
          border-bottom-color: #3d3d3d;
        }

        .card-icon {
          font-size: 24px;
        }

        .card-title {
          font-size: 20px;
          font-weight: 600;
          color: #333;
          font-family: 'Microsoft YaHei', sans-serif;

          html.dark & {
            color: #fff;
          }
        }
      }

      .card-body {
        .card-hint {
          font-size: 15px;
          color: #888;
          margin: 0 0 20px;
          font-family: 'Microsoft YaHei', sans-serif;

          html.dark & {
            color: #999;
          }
        }

        .emotion-grid {
          display: grid;
          grid-template-columns: repeat(4, 1fr);
          gap: 16px;

          .emotion-card {
            padding: 20px 16px;
            border: 2px solid #F0F0F0;
            border-radius: 16px;
            text-align: center;
            cursor: pointer;
            background: #FAFAFA;
            transition: all 0.3s ease;

            html.dark & {
              background: #333;
              border-color: #444;
            }

            &:hover {
              transform: translateY(-4px);
              box-shadow: 0 8px 25px rgba(255, 107, 107, 0.15);
              border-color: #FF9A56;
            }

            .emotion-name {
              margin-top: 10px;
              font-size: 14px;
              font-weight: 500;
              color: #666;
              font-family: 'Microsoft YaHei', sans-serif;

              html.dark & {
                color: #aaa;
              }
            }

            &.selected {
              border-color: #FF6B6B;
              background: linear-gradient(135deg, #FFF5F0 0%, #FFE4D6 100%);
              transform: translateY(-4px);
              box-shadow: 0 8px 25px rgba(255, 107, 107, 0.2);

              html.dark & {
                background: linear-gradient(135deg, #3d3d3d 0%, #2d2d2d 100%);
              }

              .emotion-name {
                color: #FF6B6B;
                font-weight: 600;
              }
            }
          }
        }

        .detail-form {
          .form-label {
            margin: 0 0 12px;
            color: #555;
            font-size: 15px;
            font-weight: 500;
            font-family: 'Microsoft YaHei', sans-serif;
            display: flex;
            align-items: center;
            gap: 8px;

            html.dark & {
              color: #aaa;
            }

            .label-icon {
              font-size: 18px;
            }
          }

          .form-group {
            margin-bottom: 24px;
          }

          .life-indicators {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 24px;
            margin-bottom: 30px;
          }

          .action-buttons {
            display: flex;
            justify-content: center;
            gap: 20px;
            padding-top: 10px;

            .btn-reset {
              padding: 14px 40px;
              border-radius: 50px;
              font-size: 16px;
              font-weight: 500;
              font-family: 'Microsoft YaHei', sans-serif;
              border: 2px solid #FF9A56;
              color: #FF9A56;
              background: transparent;
              transition: all 0.3s ease;

              &:hover {
                background: #FFF5F0;
                transform: translateY(-2px);
              }

              html.dark & {
                border-color: #FFB4B4;
                color: #FFB4B4;

                &:hover {
                  background: #3d3d3d;
                }
              }
            }

            .btn-submit {
              padding: 14px 50px;
              border-radius: 50px;
              font-size: 16px;
              font-weight: 600;
              font-family: 'Microsoft YaHei', sans-serif;
              background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
              border: none;
              transition: all 0.3s ease;

              &:hover {
                transform: translateY(-3px);
                box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35);
              }
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .emotionDiary-container {
    .content {
      .diary-card {
        .card-body {
          .emotion-grid {
            grid-template-columns: repeat(2, 1fr);
          }

          .detail-form {
            .life-indicators {
              grid-template-columns: 1fr;
            }
          }
        }
      }
    }
  }
}
</style>
