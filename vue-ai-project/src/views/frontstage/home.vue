<template>
  <div class="home-container">
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">
            <span class="title-main">心屿</span>
            <span class="title-sub">您的专属心理健康伙伴</span>
          </h1>
          <p class="hero-description">在这里，您可以敞开心扉，记录情绪，与AI助手进行温暖的对话。我们陪伴您走过每一个心情起伏，一起寻找内心的平静与力量。</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" class="btn-primary" @click="$router.push('/consultation')">
              <el-icon>
                <ChatDotRound />
              </el-icon>
              开始对话
            </el-button>
            <el-button size="large" class="btn-secondary" @click="$router.push('/emotion-diary')">
              <el-icon>
                <Edit />
              </el-icon>
              记录心情
            </el-button>
          </div>
        </div>
        <div class="hero-visual">
          <div class="visual-circle">
            <el-image :src="iconUrl" alt="心屿助手" class="logo-image" />
          </div>
          <div class="floating-element element-1">🌸</div>
          <div class="floating-element element-2">💭</div>
          <div class="floating-element element-3">✨</div>
        </div>
      </div>
    </div>

    <div class="features-section">
      <div class="features-header">
        <h2 class="section-title">我们的服务</h2>
        <p class="section-subtitle">用心陪伴，每一天</p>
      </div>
      <div class="features-grid">
        <div class="feature-card" @click="$router.push('/consultation')">
          <div class="feature-icon">
            <el-icon>
              <ChatDotRound />
            </el-icon>
          </div>
          <h3 class="feature-title">AI智能对话</h3>
          <p class="feature-desc">随时随地与AI助手对话，倾诉心声，获得专业的情绪支持和建议</p>
        </div>
        <div class="feature-card" @click="$router.push('/emotion-diary')">
          <div class="feature-icon">
            <el-icon>
              <Edit />
            </el-icon>
          </div>
          <h3 class="feature-title">情绪日记</h3>
          <p class="feature-desc">记录每日情绪变化，跟踪心情趋势，更好地了解自己的内心世界</p>
        </div>
        <div class="feature-card" @click="$router.push('/knowledge')">
          <div class="feature-icon">
            <el-icon>
              <Reading />
            </el-icon>
          </div>
          <h3 class="feature-title">心理知识库</h3>
          <p class="feature-desc">专业的心理健康知识，帮助您更好地认识情绪，掌握自我调节方法</p>
        </div>
      </div>
    </div>

    <div class="mood-tips-section">
      <div class="tips-card">
        <div class="tips-icon">💡</div>
        <div class="tips-content">
          <h3>每日心语</h3>
          <p>{{ dailyQuote }}</p>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
const iconUrl = new URL('@/assets/images/心屿.png', import.meta.url).href

const quotes = [
  "每一个情绪都值得被倾听，每一次倾诉都是自我疗愈的开始。",
  "生活中不缺少美，缺少的是发现美的眼睛和感受美的心灵。",
  "允许自己有不完美的时刻，这才是真实的成长。",
  "今天的你，已经比昨天更勇敢了。",
  "照顾好自己的情绪，是对自己最好的关爱。",
  "每一个小小的进步，都值得被庆祝。",
]

const dailyQuote = quotes[Math.floor(Math.random() * quotes.length)]
</script>
<style scoped lang="scss">
.home-container {
  background: linear-gradient(180deg, #FFF9F5 0%, #FFF5F0 100%);
  min-height: calc(100vh - 70px);

  html.dark & {
    background: linear-gradient(180deg, #1a1a1a 0%, #252525 100%);
  }

  .hero-section {
    max-width: 1200px;
    margin: 0 auto;
    padding: 80px 40px 60px;

    .hero-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 60px;

      .hero-text {
        flex: 1;
        max-width: 560px;

        .hero-title {
          margin: 0 0 24px;
          display: flex;
          flex-direction: column;
          gap: 8px;

          .title-main {
            font-size: 72px;
            font-weight: 800;
            background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            font-family: 'Microsoft YaHei', sans-serif;
            letter-spacing: 6px;
            line-height: 1.1;
          }

          .title-sub {
            font-size: 20px;
            color: #999;
            font-weight: 500;
            font-family: 'Microsoft YaHei', sans-serif;
            letter-spacing: 2px;
          }
        }

        .hero-description {
          font-size: 17px;
          line-height: 1.8;
          color: #666;
          margin: 0 0 36px;
          font-family: 'Microsoft YaHei', sans-serif;

          html.dark & {
            color: #aaa;
          }
        }

        .hero-actions {
          display: flex;
          gap: 20px;

          .btn-primary {
            background: linear-gradient(135deg, #FF9A56 0%, #FF6B6B 100%);
            border: none;
            border-radius: 50px;
            padding: 14px 36px;
            font-size: 16px;
            font-weight: 600;
            font-family: 'Microsoft YaHei', sans-serif;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;

            &:hover {
              transform: translateY(-3px);
              box-shadow: 0 8px 25px rgba(255, 107, 107, 0.35);
            }
          }

          .btn-secondary {
            background: white;
            border: 2px solid #FF9A56;
            border-radius: 50px;
            padding: 14px 36px;
            font-size: 16px;
            font-weight: 600;
            color: #FF6B6B;
            font-family: 'Microsoft YaHei', sans-serif;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;

            &:hover {
              background: #FFF5F0;
              transform: translateY(-3px);
            }

            html.dark & {
              background: #2d2d2d;
              border-color: #FFB4B4;
              color: #FFB4B4;

              &:hover {
                background: #333;
              }
            }
          }
        }
      }

      .hero-visual {
        position: relative;
        flex-shrink: 0;

        .visual-circle {
          width: 320px;
          height: 320px;
          border-radius: 50%;
          background: linear-gradient(135deg, #FFE4D6 0%, #FFD4C4 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 20px 60px rgba(255, 107, 107, 0.2);
          position: relative;
          z-index: 1;

          html.dark & {
            background: linear-gradient(135deg, #3d3d3d 0%, #2d2d2d 100%);
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
          }

          .logo-image {
            width: 320px;
            height: 320px;
            border-radius: 50%;
          }
        }

        .floating-element {
          position: absolute;
          font-size: 32px;
          animation: float 3s ease-in-out infinite;

          &.element-1 {
            top: 20px;
            right: -20px;
            animation-delay: 0s;
          }

          &.element-2 {
            bottom: 60px;
            left: -30px;
            animation-delay: 1s;
          }

          &.element-3 {
            bottom: -10px;
            right: 20px;
            animation-delay: 2s;
          }
        }
      }
    }
  }

  .features-section {
    max-width: 1200px;
    margin: 0 auto;
    padding: 60px 40px;

    .features-header {
      text-align: center;
      margin-bottom: 50px;

      .section-title {
        font-size: 36px;
        font-weight: 700;
        color: #333;
        margin: 0 0 12px;
        font-family: 'Microsoft YaHei', sans-serif;

        html.dark & {
          color: #fff;
        }
      }

      .section-subtitle {
        font-size: 16px;
        color: #999;
        margin: 0;
        font-family: 'Microsoft YaHei', sans-serif;
      }
    }

    .features-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 30px;

      .feature-card {
        background: white;
        border-radius: 20px;
        padding: 36px 28px;
        text-align: center;
        cursor: pointer;
        transition: all 0.3s ease;
        border: 1px solid transparent;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);

        html.dark & {
          background: #2d2d2d;
        }

        &:hover {
          transform: translateY(-8px);
          box-shadow: 0 12px 40px rgba(255, 107, 107, 0.15);
          border-color: rgba(255, 154, 86, 0.3);
        }

        .feature-icon {
          width: 70px;
          height: 70px;
          border-radius: 50%;
          background: linear-gradient(135deg, #FFF0EA 0%, #FFE4D6 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          margin: 0 auto 20px;
          font-size: 32px;
          color: #FF6B6B;

          html.dark & {
            background: linear-gradient(135deg, #3d3d3d 0%, #2d2d2d 100%);
          }
        }

        .feature-title {
          font-size: 20px;
          font-weight: 600;
          color: #333;
          margin: 0 0 12px;
          font-family: 'Microsoft YaHei', sans-serif;

          html.dark & {
            color: #fff;
          }
        }

        .feature-desc {
          font-size: 14px;
          line-height: 1.7;
          color: #888;
          margin: 0;
          font-family: 'Microsoft YaHei', sans-serif;

          html.dark & {
            color: #999;
          }
        }
      }
    }
  }

  .mood-tips-section {
    max-width: 800px;
    margin: 0 auto;
    padding: 40px 40px 80px;

    .tips-card {
      background: linear-gradient(135deg, #FFF5F0 0%, #FFF0EA 100%);
      border-radius: 24px;
      padding: 32px 40px;
      display: flex;
      align-items: center;
      gap: 24px;
      box-shadow: 0 4px 20px rgba(255, 107, 107, 0.08);

      html.dark & {
        background: linear-gradient(135deg, #2d2d2d 0%, #252525 100%);
      }

      .tips-icon {
        font-size: 48px;
        flex-shrink: 0;
      }

      .tips-content {
        h3 {
          font-size: 18px;
          font-weight: 600;
          color: #FF6B6B;
          margin: 0 0 8px;
          font-family: 'Microsoft YaHei', sans-serif;
        }

        p {
          font-size: 15px;
          line-height: 1.7;
          color: #666;
          margin: 0;
          font-family: 'Microsoft YaHei', sans-serif;

          html.dark & {
            color: #aaa;
          }
        }
      }
    }
  }
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0);
  }

  50% {
    transform: translateY(-15px);
  }
}

@media (max-width: 1024px) {
  .hero-section {
    .hero-content {
      flex-direction: column;
      text-align: center;

      .hero-text {
        max-width: 100%;

        .hero-title {
          align-items: center;
        }

        .hero-actions {
          justify-content: center;
        }
      }
    }
  }

  .features-section .features-grid {
    grid-template-columns: 1;
  }
}
</style>
