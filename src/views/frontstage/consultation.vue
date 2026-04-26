<template>
  <div class="consultation-container">
    <!-- 左侧静态结构 -->
    <div class="sidebar">
      <!-- AI助手信息 -->
      <div class="ai-assistant-info">
        <div class="breathing-circle">
          <el-image :src="logoUrl" alt="AI助手" style="width: 60px; height: 60px; border-radius: 50%;" />
        </div>
        <h3 class="assistant-name">心屿</h3>
        <div class="online-status">
          <div class="status-dot"></div>
          在线服务中
        </div>
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
                <el-button text type="danger" size="small" @click="handelDeleteSession(session.id)">
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
            <el-image :src="likeUrl" alt="AI助手" style="width: 30px; height: 30px;" />
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
      <div class="chat-messages">
        <!-- 默认欢迎用语 -->
        <div class="message-item ai-message" v-if="messages.length === 0">
          <div class="message-avatar">
            <el-image :src="logoUrl" alt="AI助手" style="width: 32px; height: 32px; border-radius: 50%;" />
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <p>你好，欢迎来到心屿，我是您的专属心理咨询师，我可以帮助您处理您的情绪和问题，以及为您提供专业的心理咨询，快开始对话吧！</p>
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
            <el-image v-if="msg.senderType === 2" :src="logoUrl" alt="AI助手" style="width: 18px; height: 18px;" />
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <!-- 1为用户消息 2为AI助手消息 -->
              <!-- ai正在思考 。。。 -->
              <div v-if="msg.senderType === 2 && isAiTyping && !msg.content" class="typing-indicator">
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
            </div>
            <div class="message-time">
              {{ msg.senderType === 2 && isAiTyping ? '正在回复中...' : msg.createdAt }}
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
        <el-button :disabled="!userMessage.trim() || userMessage.length > 500" class="send-btn" type="primary"
          @click="sendMessage">
          <el-icon>
            <Promotion />
          </el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { onMounted, ref } from 'vue';
import { deleteSessionAPI, getSessionDetailAPI, getSessionListAPI, getSessionEmotionAPI, startSessionAPI, } from '@/apis/frontend/consultation'
import { ElMessage } from 'element-plus';
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'
import { fetchEventSource } from '@microsoft/fetch-event-source';
import { useAdminStore } from '@/stores/admin';

// 图标
const logoUrl = new URL('@/assets/images/心屿.png', import.meta.url).href
const likeUrl = new URL('@/assets/images/like.png', import.meta.url).href
const userUrl = new URL('@/assets/images/users.png', import.meta.url).href


// 聊天内容
const messages = ref([])
// 用户输入的消息
const userMessage = ref('')
// 是否助手正在回复(用于禁用输入框)
const isAiTyping = ref(false)

//情绪花园
const currentEmotion = ref({
  primaryEmotion: '困惑',
  emotionScore: 50,
  isNegative: false,
  riskLevel: 0,
  suggestion: '保持正常的情绪状态，避免情绪过载。',
  improvementSuggestions: [],
})
//三个小点映射
const getDotActive = (score) => {
  if (score >= 61) {
    return 3
  }
  if (score >= 31) {
    return 2
  }
  return 1
}
// 风险等级映射
const getRiskLevel = (level) => {
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
const getEmotionAnalysis = async (sessionId) => {
  //确保sessionId格式正确 sessionId_123
  const id = sessionId.toString().startsWith('session_') ? sessionId : 'session_' + sessionId
  //获取情绪分析结果
  const res = await getSessionEmotionAPI(id)
  // console.log(res, '情绪分析结果');
  //更新情绪花园数据
  currentEmotion.value = res
  // 加载完成后，隐藏骨架屏
  skeletonLoading.value = false
}


// 会话历史列表
const sessionList = ref([])
// 会话历史列表记录
const getSessionList = async () => {
  const res = await getSessionListAPI({
    pageNum: 1,
    pageSize: 10,
  })
  // console.log(res, '会话历史列表');
  sessionList.value = res.records
}

// 点击会话历史项 ,切换到该会话
const handleSessionClick = async (session) => {
  //获取会话记录详情
  const res = await getSessionDetailAPI(session.id)
  // console.log(res, '会话记录详情');
  messages.value = res


  //获取情绪分析结果
  getEmotionAnalysis(session.id)

  //更新当前会话对象currentSession
  const sessionData = {
    sessionId: "session_" + session.id,//会话ID
    status: 'ACTIVE',//会话状态
    sessionTitle: session.sessionTitle,//会话标题
  }
  currentSession.value = sessionData

}
// 删除会话
const handelDeleteSession = async (sessionId) => {
  await deleteSessionAPI(sessionId)
  ElMessage.success('删除成功')
  //删除成功后，刷新会话列表
  getSessionList()
}
// 格式化用户消息(将换行符替换为<br>标签)
const formatUserMessage = (message) => {
  return message.replace(/\n/g, '<br>')
}


//定义当前会话对象（可能是临时会话，也可能是历史会话）
const currentSession = ref(null)
//创建临时会话消息(只是在先创建一个临时会话对象，不调用后端接口创建会话)
const createNewConversation = () => {
  //创建一个新的会话对象
  const newSession = {
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
const sendMessage = () => {
  if (!userMessage.value.trim()) return

  if (isAiTyping.value) {
    ElMessage.error('正在回复中，请稍后再发送')
    return
  }
  //存储用户消息，并清空输入框
  const usermessage = userMessage.value.trim()
  userMessage.value = ''

  //如果没有会话或者是临时会话，创建新会话（这里才指调用后端接口创建会话）
  if (currentSession.value.status === 'TEMP') {
    startNewSession(usermessage)
  } else {
    //历史会话直接继续对话
    messages.value.push({
      id: Date.now(),
      content: usermessage,
      senderType: 1,
      createdAt: new Date().toISOString(),//创建时间
    })
    startAIResponse(currentSession.value.sessionId, usermessage)
  }

}
// 发送消息到后端创建一个新会话
const startNewSession = async (usermessage) => {
  //构建需要发送的参数
  const params = {
    initialMessage: usermessage,//消息
  }
  //标题(判断是不是临时会话，即新会话直接设置，历史会话则需要获取到标题)
  if (currentSession.value.sessionTitle === '新会话') {
    params.sessionTitle = `AI助手-${new Date().toLocaleString()}`
  } else {
    //历史会话直接使用当前会话标题
    params.sessionTitle = currentSession.value.sessionTitle
  }
  //调用后端接口创建会话
  const res = await startSessionAPI(params)
  // console.log(res);
  //把后端返回的数据转为前端会话的格式
  const sessionData = {
    sessionId: res.sessionId,//会话ID
    status: res.status,//会话状态
    sessionTitle: params.sessionTitle,//会话标题
  }
  //如果是临时会话，更新数据
  if (currentSession.value && currentSession.value.status === 'TEMP') {
    //更新为正式会话
    Object.assign(currentSession.value, sessionData)
  } else {
    //否则即，创建一个新的会话
    currentSession.value = sessionData
  }
  //创建会话后，刷新会话历史列表
  getSessionList()
  //添加初始用户消息
  messages.value.push({
    id: Date.now(),
    content: usermessage,
    senderType: 1,
    createdAt: new Date().toISOString(),//创建时间
  })
  //开启流式对话，监听后端返回的消息
  startAIResponse(currentSession.value.sessionId, usermessage)
}
//开启流式对话
const startAIResponse = (sessionId, userMessage) => {
  //防止重复发送
  if (isAiTyping.value) {
    ElMessage.error('正在回复中，请稍后再发送')
    return
  }
  //设置正在回复中
  isAiTyping.value = true
  //创建一个ai默认消息,也作为占位符，等ai回复完成后，再替换为实际的回复内容
  const aiMessage = {
    id: `ai_${Date.now()}_${Math.random().toString(36).substring(2)}`,//定义一个唯一的id
    senderType: 2,//AI助手消息
    content: '',//消息内容
    createdAt: new Date().toISOString(),//创建时间
  }
  //将ai回复消息添加到消息列表中（用户消息+ai默认消息）
  messages.value.push(aiMessage)
  //调用流式接口
  const ctrl = new AbortController()//js里自带的专门用于终止fetch请求的控制器
  fetchEventSource('/api/psychological-chat/stream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Token': useAdminStore().token,
      'Accept': 'text/event-stream',
    },
    body: JSON.stringify({
      sessionId,
      userMessage,
    }),
    //添加取消信号
    signal: ctrl.signal,
    //监听连接成功事件，判断类型是不是想要的流式类型
    onopen: (res) => {
      if (res.headers.get('Content-Type') !== 'text/event-stream') {
        ElMessage.error('连接失败，返回的不是流式数据类型')
      }
    },
    //正常返回的消息就会触发onmessage事件
    onmessage: (event) => {
      //获取返回的消息
      const raw = event.data.trim()
      if (!raw) return
      //当前的对象
      const eventName = event.event
      //当前会话的AI消息，就是messages数组的最后一个元素
      const aiMessage = messages.value[messages.value.length - 1]

      if (eventName === 'done') {
        //ai回复完成
        isAiTyping.value = false
        ctrl.abort()
        //获取情绪分析结果
        getEmotionAnalysis(currentSession.value.sessionId)
        return
      }
      //将返回的消息解析为json格式
      const payload = JSON.parse(raw)
      //判断是否成功
      const ok = String(payload.code) === '200'
      if (ok && payload.data && payload.data.content) {
        aiMessage.content += payload.data.content
      } else if (!ok) {
        handleError(payload.message || 'ai回复失败')
      }
    },
    //监听错误事件
    onerror: (error) => {
      handleError(error || 'ai回复失败')
    },
    //监听关闭事件
    onclose: () => {
      //ai回复完成，开始情绪分析结果
      getEmotionAnalysis(currentSession.value.sessionId)
    },

  })
}
//错误处理
const handleError = (error) => {
  //当前会话的AI消息
  const aiMessage = messages.value[messages.value.length - 1]
  if (aiMessage) {
    aiMessage.content = 'ai回复失败，请稍后再重试'
  }
  isAiTyping.value = false
  ElMessage.error(error)
}

onMounted(() => {
  //创建一个临时会话
  createNewConversation()
  //获取会话历史列表
  getSessionList()
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

    .ai-assistant-info {
      margin-bottom: 20px;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 252, 248, 0.95) 100%);
      border-radius: 16px;
      padding: 16px;
      box-shadow: 0 8px 32px rgba(251, 146, 60, 0.06), 0 2px 8px rgba(0, 0, 0, 0.04);
      border: 1px solid rgba(251, 146, 60, 0.08);
      backdrop-filter: blur(10px);
      transition: all 0.3s ease;

      .breathing-circle {
        width: 60px;
        height: 60px;
        background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 12px;
        animation: breathing 4s ease-in-out infinite;
        box-shadow: 0 6px 24px rgba(255, 154, 86, 0.25);
        position: relative;
      }

      .assistant-name {
        font-size: 16px;
        font-weight: 700;
        background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        text-align: center;
        background-clip: text;
        margin: 0 0 12px;
      }

      .online-status {
        display: flex;
        align-items: center;
        justify-content: center;
        color: #059669;
        font-size: 12px;
        font-weight: 600;

        .status-dot {
          width: 8px;
          height: 8px;
          background: #059669;
          border-radius: 50%;
          margin-right: 8px;
          animation: pulse 2s infinite;
          box-shadow: 0 0 8px rgba(5, 150, 105, 0.4);
        }
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
3. 调用流式API fetchEventSource
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