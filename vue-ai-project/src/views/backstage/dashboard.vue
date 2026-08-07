<template>
  <div class="dashboard-container" v-loading="loading">
    <!-- 栅格布局  -->
    <!-- 四个卡片 gutter表示之间间距为20px -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card v-if="overviewData.systemOverview">
          <div class="card-content">
            <div class="avatar users">
              <el-image :src="iconUrl1" style="width: 40px; height: 40px;"></el-image>
            </div>
            <div class="info">
              <p class="title">用户总数</p>
              <p class="number">{{ overviewData.systemOverview.totalUsers }}</p>
              <p class="subtitle-title">活跃数：{{ overviewData.systemOverview.activeUsers }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card v-if="overviewData.systemOverview">
          <div class="card-content">
            <div class="avatar like">
              <el-image :src="iconUrl2" style="width: 40px; height: 40px;"></el-image>
            </div>
            <div class="info">
              <p class="title">情绪日志</p>
              <p class="number">{{ overviewData.systemOverview.totalDiaries }}</p>
              <p class="subtitle-title">今日新增：{{ overviewData.systemOverview.todayNewDiaries }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card v-if="overviewData.systemOverview">
          <div class="card-content">
            <div class="avatar comments">
              <el-image :src="iconUrl3" style="width: 40px; height: 40px;"></el-image>
            </div>
            <div class="info">
              <p class="title">咨询会话</p>
              <p class="number">{{ overviewData.systemOverview.totalSessions }}</p>
              <p class="subtitle-title">今日新增：{{ overviewData.systemOverview.todayNewSessions }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card v-if="overviewData.systemOverview">
          <div class="card-content">
            <div class="avatar smile">
              <el-image :src="iconUrl4" style="width: 40px; height: 40px;"></el-image>
            </div>
            <div class="info">
              <p class="title">平均情绪</p>
              <p class="number">{{ overviewData.systemOverview.avgMoodScore }}/10</p>
              <p class="subtitle-title">情绪健康指数</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- 两个图表 -->
    <el-row style="margin-top: 20px;" :gutter="20">
      <el-col :span="12">
        <el-card style="width: 100%;">
          <!-- 图表标题(卡片要用具名插槽#header) -->
          <template #header>
            <div class="card-header">情绪趋势分析</div>
          </template>
          <!-- 图表内容 -->
          <div class="chart-content">
            <div ref="emotionChartRef" style="height: 300px; width: 100%;"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card style="width: 100%;">
          <!-- 图表标题(卡片要用具名插槽#header) -->
          <template #header>
            <div class="card-header">咨询会话统计</div>
          </template>
          <!-- 图表内容 -->
          <div class="chart-content">
            <!-- 先有三个div盒子展示一些内容 -->
            <div class="consultation-stats" v-if="overviewData.consultationStats">
              <div class="stat-item">
                <div class="stat-label">总会话数</div>
                <div class="stat-value">{{ overviewData.consultationStats.totalSessions }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">平均时长</div>
                <div class="stat-value">{{ overviewData.consultationStats.avgDurationMinutes }}分钟</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">活跃用户</div>
                <div class="stat-value">{{ overviewData.systemOverview.activeUsers }}</div>
              </div>
            </div>
            <!-- 图表 -->
            <div ref="consultationChartRef" style="height: 260px; width: 100%;"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- 下方单个图表 -->
    <el-row style="margin-top: 20px;">
      <el-card style="width: 100%;">
        <!-- 图表标题(卡片要用具名插槽#header) -->
        <template #header>
          <div class="card-header">用户活跃度趋势</div>
        </template>
        <!-- 图表内容 -->
        <div class="chart-content">
          <div ref="userActiveChartRef" style="height: 300px; width: 100%;"></div>
        </div>
      </el-card>
    </el-row>
  </div>
</template>
<script setup lang="ts">
import { getOverviewAPI } from "@/apis/backend/dashboard"
import { onMounted, ref } from "vue"

// echarts----------------------------------------------------------------------------------
import * as echarts from "echarts"
import { DailyTrend, EmotionTrend, OverviewResponse, UserActivity } from "@/types/backstage/dashboard"
// 图表实例(情绪趋势/咨询会话/用户活跃度)    用于接收实例
let emotionChart: any = null
let consultationChart: any = null
let userActiveChart: any = null
//用于接收DOM
const emotionChartRef = ref(null)
const consultationChartRef = ref(null)
const userActiveChartRef = ref(null)

// 初始化情绪趋势图表
const initEmotionChart = () => {
  // DOM不存在直接返回
  if (!emotionChartRef.value) return
  // 图表实例存在，先销毁旧实例（后端数据变化前端更新实现图表刷新功能）
  if (emotionChart) {
    emotionChart.dispose()
  }
  // 初始化创建echarts实例
  emotionChart = echarts.init(emotionChartRef.value)
  //获取情绪趋势数据
  const TrendData: EmotionTrend[] = overviewData.value.emotionTrend
  //配置项
  const option = {
    // 图表标题
    title: {
      text: '情绪趋势分析',
      textStyle: {
        fontSize: 16,
        fontWeight: 600,
        color: '#2d3436'
      },
      left: 'center',
      top: 10
    },
    // 图表提示框(鼠标悬停时显示)
    tooltip: {
      trigger: 'axis',// 触发类型：坐标轴触发
      borderColor: '#f093fb',
      borderWidth: 1,
      textStyle: {
        color: '#2d3436'
      }
    },
    // 图表图例(点击隐藏显示)(就是标题下面可以点的两个东西)
    legend: {
      data: ['平均情绪评分', '记录数量'],
      top: 40
    },
    // 图表x轴(时间)
    xAxis: {
      type: 'category',
      data: TrendData.map(item => item.date),
      axisLine: {
        lineStyle: {
          color: 'rgba(244, 162, 97, 0.3)'
        }
      }
    },
    // 图表y轴(数值),因为左右两边都有，所以用用一个数组
    yAxis: [
      {
        type: 'value',
        name: '情绪评分',
        position: 'left',
        axisLabel: {
          color: '#636e72'
        },
        axisLine: {
          lineStyle: {
            color: 'rgba(244, 162, 97, 0.3)'
          }
        },
        splitLine: {
          lineStyle: {
            color: 'rgba(244, 162, 97, 0.1)'
          }
        }
      },
      {
        type: 'value',
        name: '记录数量',
        position: 'right',
        axisLabel: {
          color: '#636e72'
        },
        axisLine: {
          lineStyle: {
            color: 'rgba(244, 162, 97, 0.3)'
          }
        },
        splitLine: {
          show: false
        },
      }
    ],
    // 图表系列(数据)
    series: [{
      name: '平均情绪评分',
      type: 'line',//折线图还有bar柱状图，scatter散点图，area面积图等
      smooth: true,//平滑曲线
      lineStyle: {//折线样式(颜色/宽度)
        color: '#ffeaa7',
        width: 3
      },
      itemStyle: {//散点图样式(颜色/大小)
        color: '#ffeaa7',
      },
      data: TrendData.map(item => item.avgMoodScore)
    },
    {
      name: '记录数量',
      type: 'line',
      smooth: true,//平滑曲线
      lineStyle: {//折线样式(颜色/宽度)
        color: '#fab1a0',
        width: 3
      },
      itemStyle: {//散点图样式(颜色/大小)
        color: '#fab1a0',
      },
      data: TrendData.map(item => item.recordCount)
    }],
    //图标位置和大小 控制容器样式
    grid: {
      top: 80,
      bottom: '3%',//下边距3%
      left: '3%',//左边距3%
      right: '4%'//右边距4%
    }
  }
  // 图表设置配置项
  emotionChart.setOption(option)
}
// 初始化咨询会话图表
const initConsultationChart = () => {
  // Dom不存在直接返回
  if (!consultationChartRef.value) return
  // 图表实例存在，先销毁旧实例（后端数据变化前端更新实现图表刷新功能）
  if (consultationChart) {
    consultationChart.dispose()
  }
  // 初始化创建echarts实例
  consultationChart = echarts.init(consultationChartRef.value)
  //获取咨询会话数据
  const dailyTrend: DailyTrend[] = overviewData.value.consultationStats.dailyTrend
  //配置项
  const option = {
    title: {
      text: '咨询活动统计',
      textStyle: {
        fontSize: 16,
        fontWeight: 600,
        color: '#2d3436'
      },
      left: 'center',
      top: 10
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#fab1a0',
      borderWidth: 1,
      textStyle: {
        color: '#2d3436'
      }
    },
    legend: {
      data: ['会话数量', '参与用户数'],
      top: 40,
      textStyle: {
        color: '#636e72'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: 80,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dailyTrend.map(item => item.date),
      axisLine: {
        lineStyle: {
          color: 'rgba(244, 162, 97, 0.3)'
        }
      },
      axisLabel: {
        color: '#636e72'
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#636e72'
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(244, 162, 97, 0.3)'
        }
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(244, 162, 97, 0.1)'
        }
      }
    },
    series: [
      {
        name: '会话数量',
        type: 'bar',
        data: dailyTrend.map(item => item.sessionCount),
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#74b9ff' },
              { offset: 1, color: '#0984e3' }
            ]
          }
        },
        barWidth: '40%'
      },
      {
        name: '参与用户数',
        type: 'bar',
        data: dailyTrend.map(item => item.userCount),
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#fdcb6e' },
              { offset: 1, color: '#f39c12' }
            ]
          }
        },
        barWidth: '40%'
      }
    ]
  }
  // 图表设置配置项
  consultationChart.setOption(option)
}
// 初始化用户活跃度图表
const initUserActiveChart = () => {
  // DOM不存在直接返回
  if (!userActiveChartRef.value) return
  // 图表实例存在，先销毁旧实例（后端数据变化前端更新实现图表刷新功能）
  if (userActiveChart) {
    userActiveChart.dispose()
  }
  // 初始化创建echarts实例
  userActiveChart = echarts.init(userActiveChartRef.value)
  //获取用户活跃度数据
  const activityData: UserActivity[] = overviewData.value.userActivity
  //配置项
  const option = {
    title: {
      text: '用户活跃度趋势',
      textStyle: {
        fontSize: 16,
        fontWeight: 600,
        color: '#2d3436'
      },
      left: 'center',
      top: 10
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#fab1a0',
      borderWidth: 1,
      textStyle: {
        color: '#2d3436'
      }
    },
    legend: {
      data: ['活跃用户', '新增用户', '日记用户', '咨询用户'],
      top: 40,
      textStyle: {
        color: '#636e72'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: 80,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: activityData.map(item => item.date),
      axisLine: {
        lineStyle: {
          color: 'rgba(244, 162, 97, 0.3)'
        }
      },
      axisLabel: {
        color: '#636e72'
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#636e72'
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(244, 162, 97, 0.3)'
        }
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(244, 162, 97, 0.1)'
        }
      }
    },
    series: [
      {
        name: '活跃用户',
        type: 'line',
        data: activityData.map(item => item.activeUsers),
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#a29bfe'
        },
        itemStyle: {
          color: '#a29bfe'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(162, 155, 254, 0.4)' },
              { offset: 1, color: 'rgba(162, 155, 254, 0.1)' }
            ]
          }
        }
      },
      {
        name: '新增用户',
        type: 'line',
        data: activityData.map(item => item.newUsers),
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#fdcb6e'
        },
        itemStyle: {
          color: '#fdcb6e'
        }
      },
      {
        name: '日记用户',
        type: 'line',
        data: activityData.map(item => item.diaryUsers),
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#00b894'
        },
        itemStyle: {
          color: '#00b894'
        }
      },
      {
        name: '咨询用户',
        type: 'line',
        data: activityData.map(item => item.consultationUsers),
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#fab1a0'
        },
        itemStyle: {
          color: '#fab1a0'
        }
      }
    ]
  }
  // 图表设置配置项
  userActiveChart.setOption(option)
}

// 初始化图表
const initChart = () => {
  initEmotionChart()
  initConsultationChart()
  initUserActiveChart()
}


// echarts----------------------------------------------------------------------------------




//card图片引入
const iconUrl1 = new URL("@/assets/images/users.png", import.meta.url).href
const iconUrl2 = new URL("@/assets/images/like.png", import.meta.url).href
const iconUrl3 = new URL("@/assets/images/comments.png", import.meta.url).href
const iconUrl4 = new URL("@/assets/images/smile.png", import.meta.url).href


//loading状态
const loading = ref(false)
//界面数据
const overviewData = ref<OverviewResponse>({} as OverviewResponse)
onMounted(async () => {
  loading.value = true
  const res: OverviewResponse = await getOverviewAPI()
  overviewData.value = res
  initChart()
  loading.value = false
})
</script>
<style lang="scss" scoped>
.dashboard-container {
  .card-content {
    display: flex;
    align-items: center;

    .avatar {
      margin-right: 12px;
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;

      &.users {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &.like {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }

      &.comments {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }

      &.smile {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
      }
    }

    .info {
      .title {
        font-size: 14px;
        color: #7f8c8d;
        margin-bottom: 4px;
      }

      .value {
        font-size: 24px;
        font-weight: 700;
        color: #2c3e50;
        margin-bottom: 4px
      }

      .subtitle-title {
        font-size: 12px;
        color: #95a5a6;
      }
    }
  }

  .chart-content {
    padding: 20px;
    height: 300px;
    position: relative;

    canvas {
      width: 100% !important;
      height: 100% !important;
    }

    .consultation-stats {
      display: flex;
      justify-content: space-around;
      margin-bottom: 20px;

      .stat-item {
        text-align: center;

        .stat-label {
          font-size: 12px;
          color: #7f8c8d;
          margin-bottom: 4px;
        }

        .stat-value {
          font-size: 18px;
          font-weight: 600;
          color: #2c3e50;
        }
      }
    }
  }
}
</style>