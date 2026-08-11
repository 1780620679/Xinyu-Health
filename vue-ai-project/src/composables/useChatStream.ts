import { ref } from 'vue'
import { fetchEventSource, type EventSourceMessage } from '@microsoft/fetch-event-source'
import { useAdminStore } from '@/stores/admin'

interface StreamPayload {
  code: string | number
  message?: string
  data?: {
    content?: string
  }
}

interface StartChatStreamOptions {
  sessionId: string
  userMessage: string
  isRetry?: boolean
  onChunk: (content: string) => void //这三个方法是我们定义的，方便组件调用 是一个回调钩子,让外部组件可以自定义"AI 回复/过程中/完成后/失败后要做什么"
  onDone: () => void                    //都是外部调用 startStream 时必须传入的参数。
  onError: (error: Error) => void
}

/**
 * 管理 AI 咨询的 SSE 连接。
 * 页面只关心收到的内容和最终状态，不需要了解底层连接细节。
 */
export const useChatStream = () => {
  const adminStore = useAdminStore()
  const isStreaming = ref(false) // 是否正在流式传输 即组件内的isAiTyping
  let activeController: AbortController | null = null // 当前活动的 AbortController，用于取消流式连接

  // 停止流式连接
  const stopStream = (): void => {
    activeController?.abort()
    activeController = null
    isStreaming.value = false
  }

  // 开启流式连接
  const startStream = (options: StartChatStreamOptions): boolean => {
    if (isStreaming.value) return false

    //事前准备
    const controller = new AbortController()
    activeController = controller
    isStreaming.value = true
    let settled = false // 是否已经处理完成

    // 流式连接完成
    const finish = (): void => {
      if (settled) return
      settled = true
      isStreaming.value = false
      if (activeController === controller) {
        activeController = null
      }
      controller.abort()
      options.onDone()// ← 触发外部传入的完成回调 
    }

    // 流式连接失败
    const fail = (reason: unknown): void => {
      if (settled) return
      settled = true
      isStreaming.value = false
      if (activeController === controller) {
        activeController = null
      }
      controller.abort()
      const error = reason instanceof Error
        ? reason
        : new Error('AI 回复失败，请稍后重试')
      options.onError(error)
    }

    void fetchEventSource('/api/psychological-chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Token: adminStore.token,
        Accept: 'text/event-stream',
      },
      body: JSON.stringify({
        sessionId: options.sessionId,
        userMessage: options.userMessage,
        retry: options.isRetry ?? false,
      }),
      signal: controller.signal,//添加取消信号
      async onopen(response) {
        // 监听连接成功事件，判断类型是不是想要的流式类型
        const contentType = response.headers.get('content-type') || ''
        if (!response.ok) {
          const message = response.status === 401 || response.status === 403
            ? '登录状态已失效，请重新登录后再试'
            : response.status >= 500
              ? 'AI 服务暂时不可用，请稍后重试'
              : `AI 请求失败（HTTP ${response.status}）`
          const error = new Error(message)
          fail(error)
          throw error
        }
        if (!contentType.includes('text/event-stream')) {
          const error = new Error('AI 服务返回了异常的数据格式，请稍后重试')
          fail(error)
          throw error
        }
      },
      onmessage(event: EventSourceMessage) {
        // 监听消息事件，判断是否是 done 事件 （流式连接完成）
        if (event.event === 'done') {
          finish()
          return
        }
        // 监听消息事件，判断是否是普通消息（code 200）
        const raw = event.data.trim()
        if (!raw) return

        try {
          const payload = JSON.parse(raw) as StreamPayload
          if (String(payload.code) === '200' && payload.data?.content) {
            options.onChunk(payload.data.content)// ← 触发外部传入的分块回调 用于处理流式数据
            return
          }
          fail(new Error(payload.message || 'AI 服务处理失败，请稍后重试'))
        } catch (error) {
          fail(error instanceof Error
            ? error
            : new Error('AI 返回的数据解析失败，请稍后重试'))
        }
      },
      onclose() {
        // 正常结束一定会先收到 done；未收到 done 就断开，属于可重试的异常中断
        fail(new Error('连接意外中断，请重新生成'))
      },
      onerror(error) {
        fail(error)
        throw error
      },
    }).catch((error: unknown) => {
      // 监听连接失败事件，判断是否是取消连接
      // 为什么要做这个判断?
      // 场景	                    aborted 值	   是否调用 fail()    	原因
      // 用户主动停止 (点击停止按钮)	 true	      ❌ 不调用	        这是预期行为,不算错误
      // 连接已完成/失败后自动 abort	 true	      ❌ 不调用	        finish() 或 fail() 已处理过
      // 网络异常/服务器错误	         false	    ✅ 调用	         这是真正的意外错误,需要处理
      if (!controller.signal.aborted) {
        fail(error)
      }
    })

    return true
  }

  return {
    isStreaming,
    startStream,
    stopStream,
  }
}


// //将ai回复消息添加到消息列表中（用户消息+ai默认消息）
//   messages.value.push(aiMessage)
//   //调用流式接口
//   const ctrl = new AbortController()//js里自带的专门用于终止fetch请求的控制器
//   fetchEventSource('/api/psychological-chat/stream', {
//     method: 'POST',
//     headers: {
//       'Content-Type': 'application/json',
//       'Token': useAdminStore().token,
//       'Accept': 'text/event-stream',
//     },
//     body: JSON.stringify({
//       sessionId,
//       userMessage,
//     }),
//     //添加取消信号
//     signal: ctrl.signal,
//     //监听连接成功事件，判断类型是不是想要的流式类型
//     
//     onopen: (res: any) => {
//       if (res.headers.get('Content-Type') !== 'text/event-stream') {
//         ElMessage.error('连接失败，返回的不是流式数据类型')
//       }
//     },
//     //正常返回的消息就会触发onmessage事件
//     // 多次触发 onmessage 回调（每次返回一小段 AI 回复）
//     // ↓
//     // ├─ 收到普通消息 → 解析 payload → 追加到 aiMessage.content
//     // └─ 收到 'done' 事件 → 设置 isAiTyping = false
//     //                       → ctrl.abort() 断开连接
//     //                       → 调用情绪分析
//     //                       → return 退出回调 /                            
//     onmessage: (event: any) => {
//       //获取返回的消息
//       const raw = event.data.trim()
//       if (!raw) return
//       //当前的对象
//       const eventName = event.event
//       //当前会话的AI消息，就是messages数组的最后一个元素
//       const aiMessage = messages.value[messages.value.length - 1]

//       if (eventName === 'done') {
//         //ai回复完成
//         isAiTyping.value = false
//         ctrl.abort()    //ctrl.abort() 不阻断当前函数执行，它只是通知底层 fetch 请求"可以停了"，当前回调该跑完的代码照跑。所以顺序是：
//                         // ctrl.abort()     → 发信号：连接可以断了
//                         // getEmotionAnalysis() → 正常执行 ✅
//                         // return           → 退出回调
//         //获取情绪分析结果
//         // console.log('ai 在 onmessage 事件中');
//         if (currentSession.value) {
//           getEmotionAnalysis(currentSession.value.sessionId)
//         }
//         return
//       }
//       //将返回的消息解析为json格式
//       const payload = JSON.parse(raw)
//       //判断是否成功
//       const ok = String(payload.code) === '200'
//       if (ok && payload.data && payload.data.content) {
//         aiMessage.content += payload.data.content
//       } else if (!ok) {
//         handleError(payload.message || 'ai回复失败')
//       }
//     },
//     //监听错误事件
//     onerror: (error: any) => {
//       handleError(error || 'ai回复失败')
//     },
//     //监听关闭事件 onclose 在 ctrl.abort() 手动断开时其实不会触发。它只在服务器主动关闭或网络异常断连时才走。
//     onclose: () => { 
//       //ai回复完成，开始情绪分析结果
//       if (currentSession.value) {
//         getEmotionAnalysis(currentSession.value.sessionId)
//       }
//       console.log('ai 在 onclose 事件中');
//     },

//   })
// }
// //错误处理
// const handleError = (error: string | Error): void => {
//   //当前会话的AI消息
//   const aiMessage = messages.value[messages.value.length - 1]
//   if (aiMessage) {
//     aiMessage.content = 'ai回复失败，请稍后再重试'
//   }
//   isAiTyping.value = false
//   ElMessage.error(error instanceof Error ? error.message : error)
// }
