<template>
  <div>
    <PageHead title="知识文章">
      <template #buttons>
        <el-button type="primary" @click="dialogVisible = true">新增</el-button>
      </template>
    </PageHead>
    <TableSearch :formItem="formItem" @search="handleSearch"></TableSearch>
    <el-table :data="tableData" style="width: 100%; margin-top: 25px">
      <el-table-column label="文章标题" width="350" fixed="left">
        <template #default="scope">{{ scope.row.title }}</template>
      </el-table-column>
      <el-table-column label="分类" width="200">
        <template #default="scope">{{
          categoryMap[scope.row.categoryId]
        }}</template>
      </el-table-column>
      <el-table-column
        label="作者"
        width="150"
        prop="authorName"
      ></el-table-column>
      <el-table-column
        label="阅读量"
        width="150"
        prop="readCount"
      ></el-table-column>
      <el-table-column
        label="发布时间"
        width="150"
        prop="publishedAt"
      ></el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="scope">
          <el-button text type="primary">编辑</el-button>
          <el-button
            text
            type="success"
            v-if="scope.row.status === 0 || scope.row.status === 2"
            >发布</el-button
          >
          <el-button text type="warning" v-if="scope.row.status === 1"
            >下线</el-button
          >
          <el-button text type="danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      :page-size="pagination.size"
      layout="prev, pager, next"
      :total="pagination.total"
      @change="handleCurrentChange"
    />
    <ArticleDialog
      v-model:modelValue="dialogVisible"
      :categoryList="categoryList"
    ></ArticleDialog>
  </div>
</template>
<script setup>
import PageHead from "@/components/PageHead.vue"
import TableSearch from "@/components/TableSearch.vue"
import ArticleDialog from "@/components/ArticleDialog.vue"
import {
  getCategoryTreeAPI,
  getKnowledgeArticlePageAPI,
} from "@/apis/knowledge"
import { onMounted, ref, reactive } from "vue"
// 引入弹窗状态
const dialogVisible = ref(false)
// 搜索表单配置
const formItem = [
  {
    comp: "input",
    label: "文章标题",
    prop: "title",
    placeholder: "请输入文章标题",
  },
  {
    comp: "select",
    label: "文章内容",
    prop: "categoryId",
    placeholder: "请选择分类",
  },
  {
    comp: "select",
    label: "状态",
    prop: "status",
    placeholder: "请选择状态",
    options: [
      {
        label: "草稿",
        value: 0,
      },
      {
        label: "已发布",
        value: 1,
      },
      {
        label: "已下线",
        value: 2,
      },
    ],
  },
]
// 分页配置
const pagination = reactive({
  currentPage: 1,
  size: 5,
  total: 0,
})
// 分页切换方法
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  handleSearch({})
}
// 搜索方法(子传父回调来的数据)
const handleSearch = async (formData) => {
  // 合并分页配置和搜索表单数据
  const params = {
    ...pagination,
    ...formData,
  }
  const { records, total } = await getKnowledgeArticlePageAPI(params)
  tableData.value = records
  pagination.total = total
}
// 表格数据
const tableData = ref([])
//分类映射
const categoryMap = reactive({})
//分类列表
const categoryList = ref([])
// 引入知识分类接口(获取知识分类树下拉框数据)
onMounted(async () => {
  const data = await getCategoryTreeAPI()
  categoryList.value = data.map((item) => {
    //处理分类映射(将分类id映射到分类名称)  等号左边id为key,右边item.categoryName为value，可以在表格中根据id作为key找到对应显示分类名称
    categoryMap[item.id] = item.categoryName
    return {
      label: item.categoryName,
      value: item.id,
    }
  })
  formItem[1].options = categoryList.value
  handleSearch({})
})
</script>
