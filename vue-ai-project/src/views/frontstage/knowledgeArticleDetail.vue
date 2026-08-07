<template>
  <div class="articleDetail-container">
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-image :src="bookUrl" style="width: 50px; height: 50px;" />
        </div>
        <div class="header-text">
          <h1>文章详情</h1>
          <p>阅读专业的心理健康知识</p>
        </div>
      </div>
    </div>

    <div class="content">
      <div class="diary-card article-info-card">
        <div class="article-header">
          <el-tag class="category-tag" type="primary" plain size="large">{{ articleDetail.categoryName }}</el-tag>
          <div class="article-date">
            <el-icon><List /></el-icon>
            {{ dayjs(articleDetail.updatedAt).format('YYYY-MM-DD') }}
          </div>
        </div>
        <h1 class="article-title">{{ articleDetail.title }}</h1>
        <div class="summary-content" v-if="articleDetail.summary">
          <div class="summary-icon">💡</div>
          <p>{{ articleDetail.summary }}</p>
        </div>
        <div class="article-meta">
          <div class="meta-item">
            <el-icon><Avatar /></el-icon>
            {{ articleDetail.authorName }}
          </div>
          <div class="meta-item">
            <el-icon><Platform /></el-icon>
            {{ articleDetail.readCount }}次阅读
          </div>
        </div>
      </div>

      <div class="diary-card article-content-card">
        <div class="content-header">
          <span class="content-icon">📖</span>
          <span class="content-title">文章内容</span>
        </div>
        <div class="content-wrapper" v-html="formatContent(articleDetail.content)"></div>
        <div class="tags-content" v-if="articleDetail.tagArray && articleDetail.tagArray.length > 0">
          <div class="tags-header">
            <span class="tags-icon">🏷️</span>
            <span class="tags-title">相关标签</span>
          </div>
          <div class="tags-list">
            <el-tag v-for="tag in articleDetail.tagArray" :key="tag" type="info" class="tag-item">{{ tag }}</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { dayjs } from 'element-plus'
import { onMounted, ref } from 'vue'
import { getKnowledgeArticleDetailAPI } from '@/apis/frontend/knowledge'
import { useRoute } from 'vue-router'
import type { KnowledgeArticleDetailResponse } from '@/types/frontstage/knowledge'

const bookUrl = new URL('@/assets/images/book.png', import.meta.url).href

//获取路由参数id(可以直接使用route.params.id/也可以defineProps来接收)
const route = useRoute()
// 文章详情
const articleDetail = ref<KnowledgeArticleDetailResponse>({
  id: '',
  title: '',
  summary: '',
  content: '',
  categoryId: '',
  categoryName: '',
  coverImage: '',
  tags: [],
  readCount: 0,
  authorName: '',
  publishAt: '',
  createdAt: '',
  updatedAt: '',
  tagArray: []
})
// 获取文章详情
const getArticleDetail = async (): Promise<void> => {
  const res = await getKnowledgeArticleDetailAPI(route.params.id as string)
  articleDetail.value = res
}
// 基本的HTML清理和格式化方法
const formatContent = (content: string | undefined): string => {
  if (!content) return ''

  // 基本的HTML清理和格式化
  let formatted = content
    .replace(/\n/g, '<br>')//换行符替换为<br>
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')//加粗
    .replace(/\*(.*?)\*/g, '<em>$1</em>')//斜体

  return formatted
}
// 初始化时获取文章详情
onMounted(() => {
  console.log(route.params.id);

  getArticleDetail()
})
</script>
<style scoped lang="scss">
.articleDetail-container {
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
      padding: 32px;
      margin-bottom: 24px;
      box-shadow: 0 4px 20px rgba(255, 107, 107, 0.06);
      border: 1px solid rgba(255, 107, 107, 0.08);

      html.dark & {
        background: #2d2d2d;
      }
    }

    .article-info-card {
      .article-header {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 20px;

        .category-tag {
          border-radius: 20px;
        }

        .article-date {
          font-size: 14px;
          color: #888;
          display: flex;
          align-items: center;
          gap: 6px;

          html.dark & {
            color: #999;
          }
        }
      }

      .article-title {
        font-size: 32px;
        font-weight: 700;
        color: #333;
        margin: 0 0 24px;
        line-height: 1.3;
        font-family: 'Microsoft YaHei', sans-serif;

        html.dark & {
          color: #fff;
        }
      }

      .summary-content {
        background: linear-gradient(135deg, #FFF5F0 0%, #FFF0EA 100%);
        border-radius: 16px;
        padding: 20px 24px;
        display: flex;
        align-items: flex-start;
        gap: 16px;
        margin-bottom: 24px;

        html.dark & {
          background: linear-gradient(135deg, #3d3d3d 0%, #333 100%);
        }

        .summary-icon {
          font-size: 24px;
          flex-shrink: 0;
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

      .article-meta {
        display: flex;
        align-items: center;
        gap: 24px;
        padding-top: 20px;
        border-top: 1px solid #F0F0F0;

        html.dark & {
          border-top-color: #444;
        }

        .meta-item {
          font-size: 14px;
          color: #888;
          display: flex;
          align-items: center;
          gap: 8px;

          html.dark & {
            color: #999;
          }
        }
      }
    }

    .article-content-card {
      .content-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 24px;
        padding-bottom: 16px;
        border-bottom: 2px solid #FFF5F0;

        html.dark & {
          border-bottom-color: #3d3d3d;
        }

        .content-icon {
          font-size: 20px;
        }

        .content-title {
          font-size: 18px;
          font-weight: 600;
          color: #333;
          font-family: 'Microsoft YaHei', sans-serif;

          html.dark & {
            color: #fff;
          }
        }
      }

      .content-wrapper {
        font-size: 15px;
        line-height: 1.8;
        color: #555;

        html.dark & {
          color: #bbb;
        }

        :deep(p) {
          margin-bottom: 16px;
        }

        :deep(h1),
        :deep(h2),
        :deep(h3),
        :deep(h4),
        :deep(h5),
        :deep(h6) {
          margin: 24px 0 12px;
          color: #333;
          font-weight: 600;
          font-family: 'Microsoft YaHei', sans-serif;

          html.dark & {
            color: #fff;
          }
        }

        :deep(h2) {
          font-size: 20px;
          border-bottom: 2px solid #FFF5F0;
          padding-bottom: 8px;

          html.dark & {
            border-bottom-color: #3d3d3d;
          }
        }

        :deep(h3) {
          font-size: 18px;
        }

        :deep(ul),
        :deep(ol) {
          padding-left: 24px;
          margin-bottom: 16px;
        }

        :deep(li) {
          margin-bottom: 8px;
        }

        :deep(strong) {
          color: #FF6B6B;
          font-weight: 600;
        }

        :deep(em) {
          color: #FF9A56;
          font-style: italic;
        }
      }

      .tags-content {
        margin-top: 32px;
        padding-top: 24px;
        border-top: 1px solid #F0F0F0;

        html.dark & {
          border-top-color: #444;
        }

        .tags-header {
          display: flex;
          align-items: center;
          gap: 10px;
          margin-bottom: 16px;

          .tags-icon {
            font-size: 18px;
          }

          .tags-title {
            font-size: 16px;
            font-weight: 600;
            color: #333;
            font-family: 'Microsoft YaHei', sans-serif;

            html.dark & {
              color: #fff;
            }
          }
        }

        .tags-list {
          display: flex;
          flex-wrap: wrap;
          gap: 10px;

          .tag-item {
            border-radius: 16px;
            padding: 6px 16px;
            font-size: 13px;

            html.dark & {
              background: #3d3d3d;
              border-color: #555;
              color: #bbb;
            }
          }
        }
      }
    }
  }
}
</style>
