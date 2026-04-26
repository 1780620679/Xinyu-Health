<template>
  <div class="knowledge-container">
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">
          <el-image :src="bookUrl" style="width: 50px; height: 50px;" />
        </div>
        <div class="header-text">
          <h1>知识库</h1>
          <p>专业的心理健康知识，陪伴您成长</p>
        </div>
      </div>
    </div>

    <div class="content">
      <div class="recommend-section">
        <div class="section-header">
          <span class="section-icon">🔥</span>
          <span class="section-title">精品推荐</span>
        </div>
        <div class="recommend-list">
          <div v-for="item in recommendList" :key="item.id" class="recommend-item"
            @click="ClickToArticleDetail(item.id)">
            <h4>{{ item.title }}</h4>
            <p class="read-count">
              <el-icon>
                <Histogram />
              </el-icon>
              {{ item.readCount }} 阅读
            </p>
          </div>
        </div>
      </div>

      <div class="article-list">
        <Skeleton v-if="loading" type="article" :count="3" />
        <template v-else>
          <div v-for="item in articleList" :key="item.id" class="article-item" @click="ClickToArticleDetail(item.id)">
            <div class="article-cover">
              <img v-img-lazy="getImage(item.coverImage)" />
            </div>
            <div class="article-info">
              <div class="article-meta">
                <el-tag type="primary" plain size="small">{{ item.categoryName }}</el-tag>
                <span class="article-date">
                  <el-icon><List /></el-icon>
                  {{ dayjs(item.updatedAt).format('YYYY-MM-DD') }}
                </span>
              </div>
              <h3 class="article-title">{{ item.title }}</h3>
              <div class="article-footer">
                <div class="author">
                  <el-icon><Avatar /></el-icon>
                  {{ item.authorName }}
                </div>
                <div class="read-count">
                  <el-icon><Platform /></el-icon>
                  {{ item.readCount }} 阅读
                </div>
              </div>
            </div>
          </div>
          <div v-if="articleList.length === 0" class="no-data">
            <span class="no-data-icon">📚</span>
            <p>暂无文章数据</p>
          </div>
        </template>
      </div>
    </div>

    <div class="pagination-wrapper">
      <el-pagination background layout="prev, pager, next" :total="pagination.total" :page-size="pagination.size"
        @change="handleCurrentChange" />
    </div>
  </div>
</template>
<script setup>
import { dayjs } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getKnowledgeArticleListAPI } from '@/apis/frontend/konwledge'
import Skeleton from '@/components/Skeleton.vue'

const bookUrl = new URL('@/assets/images/book.png', import.meta.url).href

// 加载状态
const loading = ref(true)
const dogUrl = new URL('@/assets/dog.jpg', import.meta.url).href


//左侧精品推荐
const recommendList = ref([])
const getRecommendList = async () => {
  const params = {
    sortField: 'readCount',
    sortDirection: 'desc',
    currentPage: 1,
    size: 5
  }
  const res = await getKnowledgeArticleListAPI(params)
  recommendList.value = res.records
}

//右侧文章列表
const articleList = ref([])
//分页
const pagination = ref({
  currentPage: 1,
  size: 5,
  total: 0
})
//获取文章图片
const getImage = (url) => {
  return url ? 'http://159.75.169.224:1235' + url : dogUrl
}

// 获取文章列表
const getArticleList = async () => {
  loading.value = true
  try {
    const params = {
      sortField: 'publishAt',
      sortDirection: 'desc',
      ...pagination.value
    }
    const res = await getKnowledgeArticleListAPI(params)
    articleList.value = res.records
    pagination.value.total = res.total
  } finally {
    loading.value = false
  }
}

// 分页切换
const handleCurrentChange = (page) => {
  pagination.value.currentPage = page
  getArticleList()
}
// 点击文章跳转详情页
const router = useRouter()
const ClickToArticleDetail = (id) => {
  router.push(`/knowledge/article/${id}`)
}


onMounted(() => {
  // 初始化时获取左侧精品推荐
  getRecommendList()
  // 初始化时获取右侧文章列表
  getArticleList()
})
</script>
<style scoped lang="scss">
.knowledge-container {
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
      max-width: 1200px;
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
    display: flex;
    gap: 30px;
    max-width: 1200px;
    margin: 30px auto 0;
    padding: 0 20px;

    .recommend-section {
      width: 280px;
      flex-shrink: 0;
      background: white;
      border-radius: 20px;
      padding: 24px;
      box-shadow: 0 4px 20px rgba(255, 107, 107, 0.06);
      border: 1px solid rgba(255, 107, 107, 0.08);
      height: fit-content;
      position: sticky;
      top: 100px;

      html.dark & {
        background: #2d2d2d;
      }

      .section-header {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 20px;
        padding-bottom: 16px;
        border-bottom: 2px solid #FFF5F0;

        html.dark & {
          border-bottom-color: #3d3d3d;
        }

        .section-icon {
          font-size: 20px;
        }

        .section-title {
          font-size: 18px;
          font-weight: 600;
          color: #333;
          font-family: 'Microsoft YaHei', sans-serif;

          html.dark & {
            color: #fff;
          }
        }
      }

      .recommend-list {
        display: flex;
        flex-direction: column;
        gap: 16px;

        .recommend-item {
          border-left: 3px solid #FF9A56;
          padding-left: 14px;
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            transform: translateX(4px);

            h4 {
              color: #FF6B6B;
            }
          }

          h4 {
            font-size: 14px;
            font-weight: 500;
            color: #555;
            margin: 0 0 8px;
            line-height: 1.5;
            font-family: 'Microsoft YaHei', sans-serif;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            transition: color 0.3s ease;

            html.dark & {
              color: #aaa;
            }
          }

          .read-count {
            font-size: 12px;
            color: #999;
            display: flex;
            align-items: center;
            gap: 6px;
            margin: 0;

            html.dark & {
              color: #777;
            }
          }
        }
      }
    }

    .article-list {
      flex: 1;
      min-width: 0;

      .article-item {
        background: white;
        border-radius: 20px;
        overflow: hidden;
        margin-bottom: 24px;
        display: flex;
        cursor: pointer;
        transition: all 0.3s ease;
        box-shadow: 0 4px 20px rgba(255, 107, 107, 0.06);
        border: 1px solid rgba(255, 107, 107, 0.08);

        html.dark & {
          background: #2d2d2d;
        }

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 12px 40px rgba(255, 107, 107, 0.15);
        }

        .article-cover {
          width: 280px;
          height: 180px;
          flex-shrink: 0;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }

        .article-info {
          flex: 1;
          padding: 24px;
          display: flex;
          flex-direction: column;
          justify-content: space-between;

          .article-meta {
            display: flex;
            align-items: center;
            gap: 16px;

            .article-date {
              font-size: 13px;
              color: #999;
              display: flex;
              align-items: center;
              gap: 6px;

              html.dark & {
                color: #777;
              }
            }
          }

          .article-title {
            font-size: 20px;
            font-weight: 600;
            color: #333;
            margin: 12px 0;
            line-height: 1.4;
            font-family: 'Microsoft YaHei', sans-serif;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;

            html.dark & {
              color: #fff;
            }
          }

          .article-footer {
            display: flex;
            align-items: center;
            justify-content: space-between;

            .author, .read-count {
              font-size: 13px;
              color: #888;
              display: flex;
              align-items: center;
              gap: 6px;

              html.dark & {
                color: #777;
              }
            }
          }
        }
      }

      .no-data {
        text-align: center;
        padding: 60px 20px;
        background: white;
        border-radius: 20px;

        html.dark & {
          background: #2d2d2d;
        }

        .no-data-icon {
          font-size: 48px;
          margin-bottom: 16px;
        }

        p {
          color: #999;
          font-size: 15px;
          margin: 0;
          font-family: 'Microsoft YaHei', sans-serif;

          html.dark & {
            color: #777;
          }
        }
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    padding: 40px 0 20px;
  }
}

@media (max-width: 900px) {
  .knowledge-container .content {
    flex-direction: column;

    .recommend-section {
      width: 100%;
      position: static;
    }

    .article-list .article-item {
      flex-direction: column;

      .article-cover {
        width: 100%;
        height: 200px;
      }
    }
  }
}
</style>
