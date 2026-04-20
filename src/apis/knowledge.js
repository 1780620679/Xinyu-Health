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
    // 上传文件时，需要设置Content-Type为multipart/form-data
    headers: {
      "Content-Type": "multipart/form-data",
    },
  })
}
