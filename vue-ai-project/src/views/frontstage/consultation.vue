<template>
  <div class="consultation-container">
    <!-- 左侧静态结构 -->
    <div class="sidebar">
      <!-- 情绪趋势图表 -->
      <div class="emotion-chart-container">
        <div class="chart-header">
          <h3 class="chart-title">情绪趋势</h3>
        </div>
        <div ref="chartRef" class="emotion-chart"></div>
      </div>
      <!-- 情绪花园 -->
      <div class="emotion-garden">
        <!-- 标题 -->
        <div class="garden-header">
          <div class="garden-title">情绪花园</div>
        </div>
        <!-- 气泡 -->
        <div class="emotion-info">
          <div class="emotion-name">{{ currentEmotion.primaryEmotion }}</div>
          <div class="emotion-score">{{ currentEmotion.emotionScore }}</div>
        </div>
        <!-- 提示 -->
        <div class="warm-tips">
          <div class="emotion-status-text">
            <span class="status-label">今日感觉</span>
            <span class="status-emotion">{{ currentEmotion.isNegative ? '需要关注' : '情绪正常' }}</span>
          </div>
          <!-- 强度 -->
          <div class="emotion-intensity">
            <span class="intensity-dots">
              <span v-for="dot in 3" :key="dot" class="dot"
                :class="{ 'active': getDotActive(currentEmotion.emotionScore) >= dot }"></span>
            </span>
            <span class="intensity-text">
              {{ getRiskLevel(currentEmotion.riskLevel) }}
            </span>
          </div>
          <!-- 温暖建议卡片 -->
          <div class="warm-suggestion" v-if="currentEmotion.suggestion">
            <div class="suggestion-icon">💝</div>
            <div class="suggestion-content">
              <div class="suggestion-title">暖心小建议</div>
              <div class="suggestion-text">{{ currentEmotion.suggestion }}</div>
            </div>
          </div>
          <!-- 治愈小行动 -->
          <div class="healing-actions" v-if="currentEmotion.improvementSuggestions.length > 0">
            <div class="actions-title">小tips</div>
            <div class="actions-list">
              <div v-for="action in currentEmotion.improvementSuggestions" :key="action" class="action-item">
                <div class="action-icon">⭐</div>
                <div class="action-text">{{ action }}</div>
              </div>
            </div>
          </div>
          <!-- 风险预警提示 消极且风险等级高 -->
          <div class="risk-notice" v-if="currentEmotion.isNegative && currentEmotion.riskLevel > 1">
            <div class="notice-icon">⚠️</div>
            <div class="notice-content">
              <div class="notice-title">风险预警</div>
              <div class="notice-text">{{ currentEmotion.riskDescription }}</div>
            </div>
          </div>
        </div>
      </div>
      <!-- 会话历史 -->
      <div class="session-history">
        <h4 class="section-title">会话历史</h4>
        <div class="session-list">
          <div class="session-item" v-for="session in sessionList" :key="session.id"
            @click="handleSessionClick(session)">
            <div class="session-info">
              <div class="session-title">
                <span>{{ session.sessionTitle }}</span>
                <div class="session-meta">
                  <span class="session-time">{{ session.startedAt }}</span>
                </div>
                <div class="session-preview">
                  {{ session.lastMessageContent }}
                </div>
                <div class="session-status">
                  <span>
                    <el-icon>
                      <ChatRound />
                    </el-icon>
                    {{ session.messageCount || 0 }}
                  </span>
                  <span>
                    <el-icon>
                      <Clock />
                    </el-icon>
                    {{ session.durationMinutes || 0 }}分钟
                  </span>
                </div>
              </div>
              <!-- 删除小按钮 -->
              <div class="session-actions">
                <!-- @click.stop 阻止事件冒泡，点击删除按钮不会触发点击会话的事件，不然会重新触发点击当前历史会话的事件（即切换到当前会话调取会话详情） -->
                <el-button text type="danger" size="small" @click.stop="handelDeleteSession(session.id)">
                  <el-icon>
                    <DeleteFilled />
                  </el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 右侧聊天区域 -->
    <div class="chat-main">
      <!-- 头部 -->
      <div class="chat-header">
        <div class="header-left">
          <div class="chat-avatar">
            <el-image :src="likeUrl" alt="AI助手" style="width: 32px; height: 32px; border-radius: 50%;" />
          </div>
          <div class="chat-info">
            <h2>心屿</h2>
            <p>您的专属心理咨询师</p>
          </div>
        </div>
        <!-- 右侧加号 (点击创建新会话) -->
        <el-button circle @click="createNewConversation" title="创建新会话">
          <!-- 新会话图标 icon插槽 -->
          <el-icon>
            <Plus />
          </el-icon>
        </el-button>
      </div>
      <!-- 聊天内容区域 -->
      <div ref="chatMessagesRef" class="chat-messages" @scroll.passive="handleChatScroll">
        <!-- 默认欢迎用语 -->
        <div class="message-item ai-message" v-if="messages.length === 0">
          <div class="message-avatar">
            <el-image :src="logoUrl" alt="AI助手" style="width: 32px; height: 32px; border-radius: 50%;" />
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <p>你好，欢迎来到心屿，我可以帮助您处理您的情绪和问题，为您提供专业的心理咨询，有什么想聊的吗？</p>
            </div>
            <div class="message-time">
              刚刚
            </div>
          </div>
        </div>
        <!-- 消息列表 -->
        <div v-for="msg in messages" :key="msg.id" class="message-item"
          :class="msg.senderType === 1 ? 'user-message' : 'ai-message'">
          <div class="message-avatar">
            <el-image v-if="msg.senderType === 1" :src="userUrl" alt="用户" style="width: 18px; height: 18px;" />
            <el-image v-if="msg.senderType === 2" :src="logoUrl" alt="AI助手"
              style="width: 32px; height: 32px; border-radius: 50%;" />
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <!-- 1为用户消息 2为AI助手消息 -->
              <!-- ai正在思考 。。。 -->
              <div v-if="msg.senderType === 2 && isAiTyping && msg.id === activeAiMessage?.id && !msg.content"
                class="typing-indicator">
                <div class="typing-dot" v-for="dot in 3" :key="dot"></div>
              </div>
              <!-- ai错误提示 可能ai出bug了-->
              <div v-else-if="msg.isError" class="error-message">
                <p>{{ msg.content }}</p>
              </div>
              <!-- ai正常回复  这里需要对ai返回的数据进行markdown渲染处理-->
              <MarkdownRenderer v-else-if="msg.senderType === 2 && !msg.isError" :content="msg.content"
                :is-ai-message="true" />
              <!-- 用户的消息 -->
              <p v-else-if="msg.content" v-html="formatUserMessage(msg.content)"></p>
              <div v-if="msg.isStopped" class="stopped-message">已停止生成</div>
            </div>
            <div class="message-time">
              {{ msg.senderType === 2 && isAiTyping && msg.id === activeAiMessage?.id ? '正在回复中...' : msg.createdAt }}
            </div>
          </div>
        </div>
      </div>
      <!-- 输入框 -->
      <div class="chat-input">
        <!-- 左侧输入框 -->
        <div class="input-container">
          <el-input class="message-input" v-model="userMessage" placeholder="请输入您要咨询的问题" type="textarea" :rows="3"
            :disabled="isAiTyping" clearable @keyup.enter="sendMessage"></el-input>
          <div class="input-footer">
            <span>按Enter发送，Shift+Enter换行</span>
            <span>{{ userMessage.length }}/500</span>
          </div>
        </div>
        <!-- 右侧发送按钮 -->
        <el-button v-if="isAiTyping" class="send-btn stop-btn" type="danger" title="停止生成"
          @click="handleStopGenerating">
          <el-icon>
            <VideoPause />
          </el-icon>
        </el-button>
        <el-button v-else :disabled="!userMessage.trim() || userMessage.length > 500" class="send-btn" type="primary"
          @click="sendMessage">
          <el-icon>
            <Promotion />
          </el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref, onUnmounted, reactive, nextTick } from 'vue';
import { deleteSessionAPI, getSessionDetailAPI, getSessionListAPI, getSessionEmotionAPI, startSessionAPI, } from '@/apis/frontend/consultation'
import { ElMessage, ElMessageBox } from 'element-plus';
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'
import { useChatStream } from '@/composables/useChatStream';
import * as echarts from 'echarts';
import type { SessionMessage, SessionInfo, SessionEmotionResponse, StartSessionParams } from '@/types/frontstage/consultation';

// 图标
const logoUrl = new URL('@/assets/images/心屿.png', import.meta.url).href
const likeUrl = new URL('@/assets/images/like.png', import.meta.url).href
const userUrl = new URL('@/assets/images/users.png', import.meta.url).href


// 聊天内容
const messages = ref<SessionMessage[]>([])
// 用户输入的消息
const userMessage = ref<string>('')
// AI 流式连接由 composable 统一管理，页面只消费连接状态和回调结果
const { isStreaming: isAiTyping, startStream, stopStream } = useChatStream()
// 当前正在接收流式内容的 AI 消息
const activeAiMessage = ref<SessionMessage | null>(null)

// 消息列表滚动控制
const chatMessagesRef = ref<HTMLElement | null>(null)
const shouldAutoScroll = ref(true)
let scrollFrameId: number | null = null

const handleChatScroll = (): void => {
  const container = chatMessagesRef.value
  if (!container) return
  const distanceToBottom = container.scrollHeight - container.scrollTop - container.clientHeight
// scrollTop：当前已经向下滚动了多少距离。
// scrollHeight：消息列表全部内容的总高度。
// clientHeight：当前肉眼能看到的区域高度。
// distanceToBottom：距离底部的距离。
// 当距离底部小于80px时，自动滚动到底部 大于80px时，说明用户在往上滑动，不自动滚动到底部
  shouldAutoScroll.value = distanceToBottom < 80
}
//当用户在上面滑动时，shouldAutoScroll为false，然后此时发送消息，调用scrollToBottom(true)强制滚动到底部
const scrollToBottom = async (force = false): Promise<void> => {
  if (force) shouldAutoScroll.value = true
  if (!force && !shouldAutoScroll.value) return //用户在往上滑动，不自动滚动到底部

  // 等待 Vue 把新消息渲染到 DOM 后再读取最新的 scrollHeight

  // 因为当执行messages.value.push(userMessage)
  // 之后，Vue 不一定立刻修改真实 DOM。
  // 大致过程是：
  // 修改 messages 数组
  // → Vue 发现数据发生变化
  // → Vue 把更新任务放入队列
  // → 当前 JavaScript 执行结束
  // → Vue 更新真实 DOM
  // 如果我们刚刚 push 完消息，就马上获取：
  // container.scrollHeight
  // 这时新消息可能还没有渲染出来，获得的还是旧高度。
  await nextTick()
  if (scrollFrameId !== null) return
//window.requestAnimationFrame 是一个异步函数，用于在浏览器重绘前执行指定的回调函数
  // 使用它来进行记录一个scrollFrameId，不然每次ai输出一个字符，滚动条就会大量一点点滚动，影响性能
  // 如果每一个片段都立刻滚动，可能短时间调用几十次，造成：
  // 页面频繁重新计算布局；
  // 滚动卡顿；
  // 性能浪费。
  // requestAnimationFrame 可以理解成：
  // 等浏览器准备绘制下一帧画面时，再执行滚动。
// 一般屏幕一秒刷新大约 60 次，因此每帧约 16.7 毫秒。
  scrollFrameId = window.requestAnimationFrame(() => {
    scrollFrameId = null
    const container = chatMessagesRef.value
    if (!container) return
    container.scrollTo({
      top: container.scrollHeight,  // top 表示滚动到哪个纵向位置。
      behavior: force ? 'smooth' : 'auto',//behavior 控制滚动方式：smooth平滑滚动 auto自动滚动 force判断用户发送消息：使用平滑滚动。AI 持续输出：立即跟随。
    })
  })
}

// 情绪趋势图表
const chartRef = ref<HTMLElement | null>(null)
let chart: echarts.ECharts | null = null

// 情绪历史数据类型
interface EmotionHistoryItem {
  sessionId: string;
  sessionTitle: string;
  time: string;
  score: number;
  emotion: string;
}

// 情绪分数历史记录
const emotionHistory = ref<EmotionHistoryItem[]>([])

//情绪花园
const currentEmotion = ref<SessionEmotionResponse>({
  primaryEmotion: '困惑',
  emotionScore: 50,
  isNegative: false,
  riskLevel: 0,
  suggestion: '保持正常的情绪状态，避免情绪过载。',
  improvementSuggestions: [],
})
//三个小点映射
const getDotActive = (score: number): number => {
  if (score >= 61) {
    return 3
  }
  if (score >= 31) {
    return 2
  }
  return 1
}
// 风险等级映射
const getRiskLevel = (level: number): string => {
  switch (level) {
    case 0:
      return '正常'
    case 1:
      return '关注'
    case 2:
      return '预警'
    case 3:
      return '危机'
    default:
      return '正常'
  }
}
//获取情绪分析结果
const getEmotionAnalysis = async (sessionId: string | number): Promise<void> => {
  //确保sessionId格式正确 session_123
  const id = sessionId.toString().startsWith('session_') ? sessionId.toString() : 'session_' + sessionId
  //获取情绪分析结果
  const res = await getSessionEmotionAPI(id)

  // 请求返回前用户可能已经切换会话，旧结果不能覆盖当前情绪花园
  if (currentSession.value?.sessionId === id) {
    currentEmotion.value = res
  }
}

const formatEmotionTime = (value?: string): string => {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hour = date.getHours().toString().padStart(2, '0')
  const minute = date.getMinutes().toString().padStart(2, '0')
  return `${month}-${day} ${hour}:${minute}`
}

// 会话列表是唯一数据源：每条会话最多生成一个趋势点
const rebuildEmotionHistory = (): void => {
  emotionHistory.value = sessionList.value
    .filter(session => session.emotionScore !== undefined && session.primaryEmotion)
    .slice()
    .reverse()
    .map(session => ({
      sessionId: String(session.id),
      sessionTitle: session.sessionTitle,
      time: formatEmotionTime(session.emotionUpdatedAt || session.startedAt),
      score: session.emotionScore as number,
      emotion: session.primaryEmotion as string,
    }))
  updateChart()
}


// 初始化图表
const initChart = (): void => {
  if (chartRef.value) {
    // 销毁旧图表实例
    if (chart) {
      chart.dispose()
    }

    // 创建新图表实例
    chart = echarts.init(chartRef.value)

    // 更新图表
    updateChart()
  }
}

// 更新图表
const updateChart = (): void => {
  if (!chart) return

  // 准备图表数据
  const times = emotionHistory.value.map(item => item.time)
  const scores = emotionHistory.value.map(item => item.score)

  // 配置图表选项
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function (params: any) {
        const data = params[0]
        const index = data.dataIndex
        const item = emotionHistory.value[index]
        return `${item.sessionTitle}<br/>${data.name}<br/>情绪: ${item.emotion}<br/>指数: ${data.value}`
      }
    },
    xAxis: {
      type: 'category',
      data: times,
      axisLabel: {
        rotate: 45,
        fontSize: 10,
        interval: 0,
        margin: 15
      },
      axisTick: {
        interval: 0
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      name: '情绪指数'
    },
    series: [
      {
        data: scores,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 2,
          color: '#FF9A56'
        },
        itemStyle: {
          color: '#FF6B6B'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 154, 86, 0.3)' },
            { offset: 1, color: 'rgba(255, 154, 86, 0.1)' }
          ])
        }
      }
    ]
  }

  // 应用配置
  // 每次都按会话列表完整重建，避免 ECharts 合并旧数据造成残留点
  chart.setOption(option, { notMerge: true })
}

// 会话历史列表
const sessionList = ref<SessionInfo[]>([])

// 会话对象类型
interface Session {
  sessionId: string;
  status: 'TEMP' | 'ACTIVE' | string;
  sessionTitle: string;
}

// 会话历史列表记录
const getSessionList = async (): Promise<void> => {
  const res = await getSessionListAPI({
    pageNum: 1,
    pageSize: 10,
  })
  // console.log(res, '会话历史列表');
  sessionList.value = res.records
  rebuildEmotionHistory()
}

// 点击会话历史项 ,切换到该会话
const handleSessionClick = async (session: SessionInfo): Promise<void> => {
  //获取会话记录详情
  const res = await getSessionDetailAPI(session.id)
  // console.log(res, '会话记录详情');
  messages.value = res
  void scrollToBottom(true)


  //获取情绪分析结果
  getEmotionAnalysis(session.id)

  //更新当前会话对象currentSession
  const sessionData: Session = {
    sessionId: "session_" + session.id,//会话ID
    status: 'ACTIVE',//会话状态
    sessionTitle: session.sessionTitle,//会话标题
  }
  currentSession.value = sessionData

}
// 删除会话
const handelDeleteSession = async (sessionId: string): Promise<void> => {
  ElMessageBox.confirm('确定删除该会话吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteSessionAPI(sessionId)
    ElMessage.success('删除成功')
     // 如果删除的是当前正在看的会话，重置到新会话  // 先重置当前会话（防止旧 SSE 流关闭时触发情绪分析）
    if (currentSession.value && currentSession.value.sessionId === 'session_' + sessionId) {
        createNewConversation()   // ← 清空消息 + 新建临时会话
    }
     //删除成功后，刷新会话列表
    getSessionList()
  })
}
// 格式化用户消息(将换行符替换为<br>标签)
const formatUserMessage = (message: string): string => {
  return message.replace(/\n/g, '<br>')
}


//定义当前会话对象（可能是临时会话，也可能是历史会话）
const currentSession = ref<Session | null>(null)
//创建临时会话消息(只是在先创建一个临时会话对象，不调用后端接口创建会话)
const createNewConversation = (): void => {
  // 切换到新会话时终止旧连接，避免旧回复污染新页面
  stopStream()
  activeAiMessage.value = null
  //创建一个新的会话对象
  const newSession: Session = {
    sessionId: `temp_${Date.now()}`,//会话ID
    status: 'TEMP',//会话状态
    sessionTitle: '新会话',//会话标题
  }
  currentSession.value = newSession
  //点击创建新会话后，清空界面上的数据
  messages.value = []
  //点击创建新会话后，清空情绪花园数据（默认值）
  currentEmotion.value = {
    primaryEmotion: '困惑',
    emotionScore: 50,
    isNegative: false,
    riskLevel: 0,
    suggestion: '保持正常的情绪状态，避免情绪过载。',
    improvementSuggestions: [],
  }

}

// 点击发送消息
const sendMessage = (): void => {
  if (!userMessage.value.trim()) return

  if (isAiTyping.value) {
    ElMessage.error('正在回复中，请稍后再发送')
    return
  }
  //存储用户消息，并清空输入框
  const usermessage = userMessage.value.trim()
  userMessage.value = ''

  //如果没有会话或者是临时会话，创建新会话（这里才指调用后端接口创建会话）
  if (currentSession.value && currentSession.value.status === 'TEMP') {
    startNewSession(usermessage)
  } else if (currentSession.value) {
    //历史会话直接继续对话
    messages.value.push({
      id: Date.now().toString(),
      sessionId: currentSession.value.sessionId,
      content: usermessage,
      senderType: 1,
      createdAt: new Date().toISOString(),//创建时间
    })
    void scrollToBottom(true)
    startAIResponse(currentSession.value.sessionId, usermessage)
  }

}
// 发送消息到后端创建一个新会话
const startNewSession = async (usermessage: string): Promise<void> => {
  //构建需要发送的参数
  const params: StartSessionParams = {
    initialMessage: usermessage,//消息
  }
  //标题(判断是不是临时会话，即新会话直接设置，历史会话则需要获取到标题)
  if (currentSession.value && currentSession.value.sessionTitle === '新会话') {
    params.sessionTitle = usermessage.length > 10 ? usermessage.substring(0, 10) : usermessage
  } else if (currentSession.value) {
    //历史会话直接使用当前会话标题
    params.sessionTitle = currentSession.value.sessionTitle
  }
  //调用后端接口创建会话
  const res = await startSessionAPI(params)
  
  //把后端返回的数据转为前端会话的格式
  const sessionData: Session = {
    sessionId: res.sessionId,//会话ID
    status: res.status,//会话状态
    sessionTitle: params.sessionTitle || '新会话',
  }
  //如果是临时会话，更新数据
  if (currentSession.value && currentSession.value.status === 'TEMP') {
    //更新为正式会话
    Object.assign(currentSession.value, sessionData)
  } else {
    //否则即，历史会话，直接赋值可以确保获取到最新的会话数据
    currentSession.value = sessionData
  }
  //创建会话后，刷新会话历史列表
  getSessionList()
  //添加初始用户消息
  messages.value.push({
    id: Date.now().toString(),
    sessionId: currentSession.value.sessionId,
    content: usermessage,
    senderType: 1,
    createdAt: new Date().toISOString(),//创建时间
  })
  void scrollToBottom(true)
  //开启流式对话，监听后端返回的消息
  if (currentSession.value) {
    startAIResponse(currentSession.value.sessionId, usermessage)
  }
}
//开启流式对话
const startAIResponse = (sessionId: string, userMessage: string): void => {
  //防止重复发送
  if (isAiTyping.value) {
    ElMessage.error('正在回复中，请稍后再发送')
    return
  }
  //创建一个ai默认消息,也作为占位符，等ai回复完成后，再替换为实际的回复内容
  // 必须使用 reactive：SSE 回调持有并逐片修改这个对象，普通对象不会触发 Vue 增量渲染
  const aiMessage = reactive<SessionMessage>({
    id: `ai_${Date.now()}_${Math.random().toString(36).substring(2)}`,//定义一个唯一的id
    senderType: 2,//AI助手消息
    sessionId: currentSession.value!.sessionId,//会话ID
    content: '',//消息内容
    createdAt: new Date().toISOString(),//创建时间
  })
  //开启流式对话，监听后端返回的消息
  const started = startStream({
    sessionId,
    userMessage,
    // 处理流式数据。追加到aiMessage.content中
    onChunk(content) {
      aiMessage.content += content
      void scrollToBottom()  //注意这里没传值 ，由force判断，保证用户发送消息：使用平滑滚动。AI 持续输出：立即跟随。 force为true时，滚动到最底部 但是用户在ai输出时向上划，就不打扰
    },
    onDone() {
      activeAiMessage.value = null
      // 后端在 SSE 完成前已更新本会话情绪；刷新当前卡片和趋势列表
      void Promise.all([
        getEmotionAnalysis(sessionId),
        getSessionList(),
      ])
    },
    onError(error) {
      aiMessage.content = 'AI 回复失败，请稍后再重试'
      aiMessage.isError = true
      activeAiMessage.value = null
      ElMessage.error(error.message)
    },
  })

  if (started) {
    activeAiMessage.value = aiMessage
    //将AI占位消息添加到列表，后续收到的片段会持续更新同一个对象
    messages.value.push(aiMessage)
    void scrollToBottom(true)
  }
}

const handleStopGenerating = (): void => {
  if (!isAiTyping.value) return

  stopStream()
  if (activeAiMessage.value) {
    activeAiMessage.value.isStopped = true
  }
  activeAiMessage.value = null
  void scrollToBottom(true)
}

onMounted(() => {
  //创建一个临时会话
  createNewConversation()
  //获取会话历史列表
  getSessionList()
  // 初始化图表
  initChart()
})

onUnmounted(() => {
  // 离开页面时终止仍在进行的 SSE 请求，避免内存和网络资源泄漏
  stopStream()
  activeAiMessage.value = null
  if (scrollFrameId !== null) {
    window.cancelAnimationFrame(scrollFrameId)
    scrollFrameId = null
  }
  // 销毁图表实例
  if (chart) {
    chart.dispose()
    chart = null
  }
})

</script>
<style scoped lang="scss">
.consultation-container {
  margin: 0 auto;
  width: 1200px;
  display: flex;
  gap: 20px;
  padding: 20px;

  .sidebar {
    width: 320px;

    .emotion-chart-container {
      margin-bottom: 20px;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 252, 248, 0.95) 100%);
      border-radius: 16px;
      padding: 16px;
      box-shadow: 0 8px 32px rgba(251, 146, 60, 0.06), 0 2px 8px rgba(0, 0, 0, 0.04);
      border: 1px solid rgba(251, 146, 60, 0.08);
      backdrop-filter: blur(10px);
      transition: all 0.3s ease;

      .chart-header {
        margin-bottom: 16px;

        .chart-title {
          font-size: 16px;
          font-weight: 700;
          background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          text-align: center;
          background-clip: text;
          margin: 0;
        }
      }

      .emotion-chart {
        width: 100%;
        height: 250px;
      }
    }

    .session-history {
      background: white;
      border-radius: 16px;
      padding: 16px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      margin-bottom: 20px;
      min-height: 250px;
      display: flex;
      flex-direction: column;

      .section-title {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin: 0 0 16px;
        display: flex;
        align-items: center;
        justify-content: space-between;

      }

      .session-list {
        overflow-y: auto;
        max-height: 200px;
        scrollbar-width: thin;
        scrollbar-color: rgba(64, 150, 255, 0.3) transparent;

        .session-item {
          position: relative;
          display: flex;
          align-items: flex-start;
          gap: 12px;
          padding: 12px;
          margin-bottom: 8px;
          border-radius: 12px;
          cursor: pointer;
          transition: all 0.3s ease;
          border: 2px solid transparent;

          &:hover {
            background: #f8f9ff;
            border-color: #e6f0ff;
          }

          &.active {
            background: #e6f0ff;
            border-color: #4096ff;
          }

          .session-info {
            flex: 1;

            .session-title {
              font-weight: 500;
              font-size: 14px;
              color: #333;
              margin-bottom: 4px;
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;

              .session-meta {
                display: flex;
                align-items: center;
                gap: 8px;
                margin-bottom: 6px;

                .session-time {
                  font-size: 12px;
                  color: #999;
                }
              }

              .session-preview {
                width: 200px;
                font-size: 12px;
                color: #666;
                margin-bottom: 6px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
              }

              .session-status {
                display: flex;
                align-items: center;
                gap: 12px;

                span {
                  font-size: 12px;
                  color: #999;
                  display: flex;
                  align-items: center;
                  gap: 4px;
                }
              }
            }

            .session-actions {
              position: absolute;
              top: 10px;
              right: 12px;
            }
          }
        }

        .no-sessions-text {
          text-align: center;
          font-size: 14px;
          color: #999;
        }
      }
    }

    .emotion-garden {
      background: linear-gradient(135deg, #fef9e7 0%, #fcf4e6 50%, #f6f0e8 100%);
      border-radius: 20px;
      padding: 16px;
      margin-bottom: 20px;
      box-shadow: 0 8px 32px rgba(252, 244, 230, 0.8);
      border: 1px solid rgba(255, 255, 255, 0.2);
      position: relative;
      overflow: hidden;
      min-height: 300px;

      .garden-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 20px;
        position: relative;
        z-index: 2;

        .garden-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 16px;
          font-weight: 600;
          color: #8b4513;
        }
      }

      .emotion-info {
        margin: 0 auto;
        width: 80px;
        height: 80px;
        border-radius: 50%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        z-index: 10;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
        border: 2px solid rgba(255, 255, 255, 0.8);
        background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%);
        color: #fff;

        .emotion-name {
          font-size: 15px;
          font-weight: 600;
          line-height: 1;
          margin-bottom: 2px;
        }

        .emotion-score {
          font-size: 14px;
          font-weight: 700;
          opacity: 0.9;
        }
      }

      .warm-tips {
        text-align: center;
        margin-bottom: 16px;

        .emotion-status-text {
          margin-bottom: 12px;

          .status-label {
            font-size: 14px;
            color: #8b7355;
            margin-right: 8px;
          }

          .status-emotion {
            font-size: 16px;
            font-weight: 600;
            padding: 4px 12px;
            border-radius: 16px;
            display: inline-block;
          }
        }

        .emotion-intensity {
          margin-bottom: 16px;
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 8px;

          .intensity-dots {
            display: flex;
            gap: 4px;

            .dot {
              width: 8px;
              height: 8px;
              border-radius: 50%;
              background: #e0e0e0;
              transition: all 0.3s ease;

              &.active {
                background: linear-gradient(135deg, #ff9a9e, #fecfef);
                transform: scale(1.2);
                box-shadow: 0 2px 8px rgba(255, 154, 158, 0.4);
              }
            }
          }

          .intensity-text {
            font-size: 12px;
            color: #8b7355;
            font-weight: 500;
          }
        }

        .warm-suggestion {
          background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.8));
          border-radius: 16px;
          padding: 12px;
          margin-bottom: 16px;
          display: flex;
          align-items: flex-start;
          gap: 10px;
          border: 1px solid rgba(255, 255, 255, 0.6);
          box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);

          .suggestion-icon {
            font-size: 20px;
            flex-shrink: 0;
            margin-top: 2px;
          }

          .suggestion-content {
            text-align: left;
            flex: 1;

            .suggestion-title {
              font-size: 14px;
              font-weight: 600;
              color: #8b7355;
              margin-bottom: 6px;
            }

            .suggestion-text {
              font-size: 13px;
              color: #6b5b47;
              line-height: 1.5;
            }
          }
        }

        .healing-actions {
          margin-bottom: 16px;

          .actions-title {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            font-size: 14px;
            font-weight: 600;
            color: #8b7355;
            margin-bottom: 16px;
          }

          .actions-list {
            display: flex;
            flex-direction: column;
            gap: 10px;

            .action-item {
              background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.7));
              border-radius: 12px;
              padding: 12px;
              display: flex;
              align-items: center;
              gap: 10px;
              border: 1px solid rgba(255, 255, 255, 0.5);
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
              text-align: left;

              .action-icon {
                font-size: 14px;
                color: #ffd700;
                flex-shrink: 0;
              }

              .action-text {
                font-size: 12px;
                color: #6b5b47;
                line-height: 1.4;
                flex: 1;
              }
            }
          }
        }

        .risk-notice {
          background: linear-gradient(135deg, #fff9e6, #ffeaa7);
          border-radius: 16px;
          padding: 16px;
          display: flex;
          align-items: flex-start;
          gap: 12px;
          border: 1px solid rgba(255, 234, 167, 0.6);
          box-shadow: 0 6px 20px rgba(255, 234, 167, 0.3);

          .notice-icon {
            font-size: 20px;
            flex-shrink: 0;
            margin-top: 2px;
          }

          .notice-content {
            flex: 1;

            .notice-title {
              font-size: 14px;
              font-weight: 600;
              color: #d4840f;
              margin-bottom: 6px;
            }

            .notice-text {
              font-size: 13px;
              color: #b8740c;
              line-height: 1.5;
            }
          }
        }
      }
    }
  }

  .chat-main {
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 252, 250, 0.98) 100%);
    border-radius: 20px;
    box-shadow: 0 12px 40px rgba(251, 146, 60, 0.08), 0 4px 16px rgba(0, 0, 0, 0.04);
    border: 1px solid rgba(251, 146, 60, 0.1);
    backdrop-filter: blur(10px);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    flex: 1;

    .chat-header {
      background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
      color: white;
      padding: 20px 24px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      position: relative;
      flex-shrink: 0;

      .header-left {
        display: flex;
        align-items: center;

        .chat-avatar {
          width: 48px;
          height: 48px;
          background: rgba(255, 255, 255, 0.25);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          position: relative;
          z-index: 1;
        }

        .chat-info {
          h2 {
            font-size: 20px;
            font-weight: 700;
            margin-bottom: 4px;
          }

          p {
            font-size: 14px;
          }
        }
      }
    }

    .chat-messages {
      flex: 1;
      overflow-y: auto;
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 16px;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.02) 0%, rgba(255, 252, 248, 0.05) 100%);
      min-height: 0;
      max-height: calc(100vh - 200px);
      scrollbar-width: thin;
      scrollbar-color: rgba(251, 146, 60, 0.3) transparent;

      .message-item {
        display: flex;
        align-items: flex-start;
        gap: 12px;

        .message-avatar {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 14px;
          color: white;
          flex-shrink: 0;
        }

        &.ai-message {
          .message-avatar {
            background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
            box-shadow: 0 4px 12px rgba(255, 154, 86, 0.3);
          }
        }

        &.user-message {
          .message-avatar {
            background: linear-gradient(135deg, #6b7280, #4b5563);
            box-shadow: 0 4px 12px rgba(107, 114, 128, 0.3);
          }
        }

        .message-content {
          max-width: 70%;

          .message-bubble {
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 252, 248, 0.95) 100%);
            border-radius: 16px;
            padding: 12px 16px;
            position: relative;
            animation: fadeInUp 0.4s ease-out;
            border: 1px solid rgba(251, 146, 60, 0.1);
            box-shadow: 0 4px 16px rgba(251, 146, 60, 0.05);

            .typing-indicator {
              display: flex;
              gap: 4px;
              padding: 8px 0;

              .typing-dot {
                width: 8px;
                height: 8px;
                background: #ccc;
                border-radius: 50%;
                animation: typing 1.5s ease-in-out infinite;

                &:nth-child(2) {
                  animation-delay: 0.2s;
                }

                &:nth-child(3) {
                  animation-delay: 0.4s;
                }
              }
            }

            /* 错误消息样式 */
            .error-message {
              background: linear-gradient(135deg, #FEF2F2 0%, #FECACA 100%);
              border: 1px solid #F87171;
              border-radius: 12px;
              padding: 12px 16px;
              color: #991B1B;
              font-weight: 500;
              display: flex;
              align-items: center;
              gap: 8px;
            }

            .stopped-message {
              margin-top: 8px;
              padding-top: 8px;
              border-top: 1px dashed rgba(120, 113, 108, 0.3);
              color: #78716c;
              font-size: 12px;
            }
          }

          .message-time {
            font-size: 12px;
            color: #999;
            margin-top: 4px;
          }
        }
      }
    }

    .chat-input {
      border-top: 1px solid rgba(251, 146, 60, 0.1);
      padding: 20px 24px;
      display: flex;
      gap: 12px;
      align-items: flex-end;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.5) 0%, rgba(255, 252, 248, 0.7) 100%);
      backdrop-filter: blur(10px);
      flex-shrink: 0;

      .input-container {
        flex: 1;
      }

      .input-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 12px;
        color: #78716c;
        font-weight: 500;
      }

      .send-btn {
        height: 60px;
        width: 60px;
        border-radius: 16px;
        background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%) !important;
        border: none !important;
        box-shadow: 0 6px 20px rgba(255, 154, 86, 0.25);
        transition: all 0.3s ease;

        &.stop-btn {
          background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%) !important;
          box-shadow: 0 6px 20px rgba(220, 38, 38, 0.25);
        }
      }

    }

  }
}
</style>

<!-- //   ### 逻辑说明
    // 1. 临时会话的处理 ：
    //    - 当 currentSession.value.status === 'TEMP' 时，使用 Object.assign(currentSession.value, sessionData)
    //    - Object.assign() 方法会将 sessionData 的所有可枚举属性复制到 currentSession.value 对象中
    //    - 这样做的目的是：保留原临时会话对象的引用，只更新其属性值
    // 2. 历史会话的处理 ：

    //    - 当不是临时会话时，直接使用 currentSession.value = sessionData
    //    - 这会创建一个新的会话对象引用
    // ### 为什么这样处理？
    // 1. 临时会话的特殊性 ：

    //    - 临时会话是在前端创建的，还没有对应的后端会话
    //    - 使用 Object.assign() 可以保持对象引用不变，同时更新其属性
    //    - 这样可以确保页面上绑定到这个会话对象的所有地方都能自动更新
    // 2. 历史会话的处理 ：

    //    - 历史会话是从后端获取的，已经有完整的会话数据
    //    - 直接赋值可以确保获取到最新的会话数据
    // ### 技术优势
    // 1. 保持响应式 ：

    //    - 使用 Object.assign() 可以保持对象的响应式特性，因为它修改的是原对象的属性
    //    - 而直接赋值会创建新对象，可能会影响响应式绑定
    // 2. 节省内存 ：

    //    - 对于临时会话，只需要更新属性，不需要创建新对象
    //    - 对于历史会话，直接赋值可以确保获取到最新的数据
    // 3. 逻辑清晰 ：

    //    - 明确区分了临时会话和历史会话的处理方式
    //    - 代码结构清晰，易于理解和维护 -->

<!-- ## 页面加载时发生了什么
1. 页面加载时，调用 createNewConversation() 创建一个 临时会话对象

- 这个临时会话只有 sessionId （时间戳）、 status: 'TEMP' 、 sessionTitle: '新会话'
- 这时候还没有调用后端API创建真正的会话
2. 同时调用 getSessionList() 获取会话历史列表
## 用户发送消息时发生了什么
### 第一种情况：新会话（临时会话）
当用户发送第一条消息时：

1. 调用 startNewSession(用户消息)
2. 把用户消息和标题发送给后端API
3. 后端返回正式的 sessionId
4. 用 Object.assign() 把临时会话更新为正式会话（保持对象引用不变）
5. 添加用户消息到消息列表
6. 刷新会话历史列表
7. 开始流式对话，调用 startAIResponse()
### 第二种情况：历史会话
当用户在一个已有的历史会话中继续发送消息时：

1. 用户消息直接添加到消息列表
2. 直接调用 startAIResponse() 开始流式对话
## 切换历史会话时发生了什么
1. 点击某个历史会话
2. 调用 getSessionDetailAPI() 获取该会话的详情消息
3. 更新 currentSession 为该历史会话
4. 消息列表显示该会话的历史消息
## 流式对话的核心流程
1. 创建一个临时的AI消息占位符（content为空）
2. 添加到消息列表显示
3. 调用 useChatStream，由 composable 通过 fetchEventSource 连接流式 API
4. 后端每返回一段数据，就追加到 aiMessage.content
5. 当收到 done 事件时，表示AI回复完成
## 为什么要用 Object.assign
因为临时会话对象 currentSession 已经在页面的某个地方绑定了（比如响应式引用）。如果直接赋值 currentSession.value = sessionData ，会创建新对象，可能影响绑定。

用 Object.assign() 可以只更新属性，保持对象引用不变，页面上所有绑定到这个对象的地方都能自动更新。 -->

<!-- ## 技术亮点总结
### 1. 流式对话实现（SSE）
- 使用 @microsoft/fetch-event-source 实现 Server-Sent Events 流式响应
- 实时显示AI逐字回复，打造流畅对话体验
- 支持终端请求（AbortController）
### 2. 临时会话惰性创建模式
- 页面加载时先创建临时会话对象，用户可立即输入
- 等用户发送第一条消息时才调用后端创建正式会话
- 减少不必要的API调用，优化性能
### 3. 响应式状态管理
- 使用 Object.assign() 更新临时会话为正式会话
- 保持Vue响应式引用不变，避免视图更新问题
- 清晰区分临时会话(TEMP)和历史会话(ACTIVE)状态
### 4. Markdown内容渲染
- 集成MarkdownRenderer组件
- 支持AI回复的代码高亮、列表、链接等格式
### 5. 会话列表管理
- 支持会话历史查看、切换、删除
- 左侧边栏实时更新会话列表
- 断点续聊：点击历史会话恢复完整对话上下文
### 6. 错误处理机制
- AI回复异常时显示友好错误提示
- 请求超时自动处理
- 用户体验降级方案完善
简历描述示例 ：
 实现了AI流式对话功能，使用SSE技术实现实时打字效果，优化首屏体验。设计临时会话机制，采用惰性创建模式减少API调用。集成Markdown渲染，支持代码高亮和格式化文本展示。 -->
