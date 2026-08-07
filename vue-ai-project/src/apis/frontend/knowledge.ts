import request from '@/utils/request'
import type { KnowledgeArticleListParams, KnowledgeArticleListResponse, KnowledgeArticleDetailResponse } from '@/types/frontstage/knowledge'

// 查询知识文章列表
export const getKnowledgeArticleListAPI = (params: KnowledgeArticleListParams): Promise<KnowledgeArticleListResponse> => {
  return request({
    url: '/knowledge/article/page',
    method: 'GET',
    params
  })
}

// 获取知识文章详情
export const getKnowledgeArticleDetailAPI = (id: string): Promise<KnowledgeArticleDetailResponse> => {
  return request({
    url: `/knowledge/article/${id}`,
    method: 'GET'
  })
}
