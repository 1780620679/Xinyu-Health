import request from "@/utils/request"

// 获取分类树(知识文章分类下拉框可选的值)
export const getCategoryTreeAPI = () => {
  return request({
    url: "/knowledge/category/tree",
    method: "get",
  })
}
// 获取知识文章列表
//get请求有参数时，需要在params中传递参数
export const getKnowledgeArticlePageAPI = (params) => {
  return request({
    url: "/knowledge/article/page",
    method: "get",
    params,
  })
}
// 知识文章文件上传
export const uploadFileAPI = (file, businessInfo) => {
  //创建FormData对象，用于存储文件和业务信息
  const formData = new FormData()
  formData.append("file", file)
  formData.append("businessType", "ARTICLE")
  formData.append("businessId", businessInfo.businessId)
  formData.append("businessFiled", "cover")
  return request({
    url: "/file/upload",
    method: "post",
    data: formData,
    // 上传文件时，不需要手动设置Content-Type，axios会自动处理
  })
}
//获取知识文章详情/knowledge/article/{id}
export const getKnowledgeArticleDetailAPI = (id) => {
  return request({
    url: `/knowledge/article/${id}`,
    method: "get",
  })
}
//文章新增
//data为文章数据，包含标题、内容、分类、摘要、标签等
export const knowledgeArticleAPI = (data) => {
  return request({
    url: "/knowledge/article",
    method: "post",
    data,
  })
}
//文章编辑/knowledge/article/{id}
//data为文章数据，包含标题、内容、分类、摘要、标签等
export const knowledgeArticleUpdateAPI = (id, data) => {
  return request({
    url: `/knowledge/article/${id}`,
    method: "put",
    data,
  })
}
// 发布/下线知识文章
export const publishKnowledgeArticleAPI = (id, status) => {
  return request({
    url: `/knowledge/article/${id}/status`,
    method: "put",
    data: {
      status,
    }
  })
}
// 删除知识文章
export const deleteKnowledgeArticleAPI = (id) => {
  return request({
    url: `/knowledge/article/${id}`,
    method: "delete",
  })
}
