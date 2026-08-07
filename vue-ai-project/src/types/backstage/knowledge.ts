// 知识文章分类树结构
export interface CategoryTreeItem {
  id: string;
  categoryName: string;
}

// 知识文章分类选项（用于下拉框）
export interface CategoryOption {
  label: string;
  value: string;
}

// 知识文章列表请求参数
export interface KnowledgeArticlePageParams {
  currentPage: number;
  size: number;
  total: number;
}

// 知识文章列表响应
export interface KnowledgeArticlePageResponse {
  records: KnowledgeArticle[];
  total: number;
}

// 知识文章信息
export interface KnowledgeArticle {
  id: string;
  title: string;
  categoryId: string;
  authorName: string;
  readCount: number;
  publishedAt: string;
  status: number;
  summary?: string;
  content?: string;
  categoryName?: string;
  cover?: string;
  tags?: string[];
  viewCount?: number;
  createdAt?: string;
  updatedAt?: string;
}

// 知识文章详情
export interface KnowledgeArticleDetail extends KnowledgeArticle {
  summary: string;
  content: string;
  categoryName: string;
  cover: string;
  tags: string[];
  createdAt: string;
  updatedAt: string;
}

// 知识文章创建/更新参数
export interface KnowledgeArticleParams {
  title: string;
  summary: string;
  content: string;
  categoryId: string;
  cover?: string;
  tags?: string[];
  status: number;
}

// 文件上传响应
export interface UploadFileResponse {
  url: string;
  fileName: string;
  fileSize: number;
}

// 搜索表单数据
export interface KnowledgeSearchForm {
  title?: string;
  categoryId?: string;
  status?: number;
}
