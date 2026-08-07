// 知识文章列表请求参数
export interface KnowledgeArticleListParams {
  currentPage?: number;
  size?: number;
  title?: string;
  categoryId?: string;
  status?: number;
  sortField?: string;
  sortDirection?: string;
}

// 知识文章信息
export interface KnowledgeArticle {
  id: string;
  title: string;
  summary: string;
  content: string;
  categoryId: string;
  categoryName: string;
  coverImage: string;
  tags: string[];
  readCount: number;
  authorName: string;
  publishAt: string;
  createdAt: string;
  updatedAt: string;
  tagArray?: string[];
}

// 知识文章列表响应
export interface KnowledgeArticleListResponse {
  records: KnowledgeArticle[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

// 知识文章详情响应
export type KnowledgeArticleDetailResponse = KnowledgeArticle;