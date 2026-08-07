<template>
  <div>
    <PageHead title="知识文章">
      <template #buttons>
        <el-button type="primary" @click="handleEdit({} as KnowledgeArticle)">新增</el-button>
      </template>
    </PageHead>
    <TableSearch :formItem="formItem" @search="handleSearch"></TableSearch>
    <el-table :data="tableData" style="width: 100%; margin-top: 25px" v-loading="loading">
      <el-table-column label="文章标题" width="450" fixed="left">
        <template #default="scope">{{ scope.row.title }}</template>
      </el-table-column>
      <el-table-column label="分类" width="200">
        <template #default="scope">{{
          categoryMap[scope.row.categoryId]
        }}</template>
      </el-table-column>
      <el-table-column label="作者" width="150" prop="authorName"></el-table-column>
      <el-table-column label="阅读量" width="150" prop="readCount"></el-table-column>
      <el-table-column label="发布时间" width="150" prop="publishedAt"></el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="scope">
          <el-button text type="primary" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button text type="success" @click="handlePublish(scope.row)"
            v-if="scope.row.status === 0 || scope.row.status === 2">发布</el-button>
          <el-button text type="warning" @click="handleOffline(scope.row)" v-if="scope.row.status === 1">下线</el-button>
          <el-button text type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination background :page-size="pagination.size" layout="prev, pager, next" :total="pagination.total"
      @change="handleCurrentChange" />
    <ArticleDialog v-model:modelValue="dialogVisible" :categoryList="categoryList" :currentArticle="currentArticle"
      @success="handleSuccess"></ArticleDialog>
  </div>
</template>
<script setup lang="ts">
import {
  deleteKnowledgeArticleAPI,
  getCategoryTreeAPI,
  getKnowledgeArticleDetailAPI,
  getKnowledgeArticlePageAPI,
  publishKnowledgeArticleAPI,
} from "@/apis/backend/knowledge"
import { onMounted, ref, reactive } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import ArticleDialog from "@/components/ArticleDialog.vue"
import TableSearch from "@/components/TableSearch.vue"
import PageHead from "./components/PageHead.vue"
import { KnowledgeArticle, KnowledgeArticlePageParams, CategoryTreeItem, CategoryOption, KnowledgeSearchForm, KnowledgeArticleDetail } from "@/types/backstage/knowledge"

// 引入加载状态
const loading = ref(false)
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
const pagination = reactive<KnowledgeArticlePageParams>({
  currentPage: 1,
  size: 5,
  total: 0,
})
// 分页切换方法
const handleCurrentChange = (page: number) => {
  pagination.currentPage = page
  handleSearch({})
}
// 搜索方法(子传父回调来的数据)
const handleSearch = async (formData: KnowledgeSearchForm) => {
  loading.value = true
  // 合并分页配置和搜索表单数据
  const params: KnowledgeArticlePageParams = {
    ...pagination,
    ...formData,
  }// 获取知识文章列表
  const { records, total } = await getKnowledgeArticlePageAPI(params)
  tableData.value = records
  pagination.total = total
  loading.value = false
}
// 表格数据
const tableData = ref<KnowledgeArticle[]>([])
//分类映射
const categoryMap = reactive<Record<string, string>>({})
//分类列表
const categoryList = ref<CategoryOption[]>([])
// 引入知识分类接口(获取知识分类树下拉框数据)
onMounted(async () => {
  const data = await getCategoryTreeAPI()
  categoryList.value = data.map((item: CategoryTreeItem) => {
    //处理分类映射(将分类id映射到分类名称)  等号左边id为key,右边item.categoryName为value，可以在表格中根据id作为key找到对应显示分类名称
    categoryMap[item.id] = item.categoryName
    return {
      label: item.categoryName,
      value: item.id,
    }
  })
  // 处理分类列表(将分类id转换为数字类型，不然报错)
  formItem[1].options = categoryList.value.map((item: CategoryOption) => ({
    label: item.label,
    value: Number(item.value),
  }))
  handleSearch({})
})
const currentArticle = ref<KnowledgeArticleDetail | null>(null)
// 新增/编辑
const handleEdit = async (row: KnowledgeArticle) => {
  //判断是否是新增
  if (!row.id) {
    //新增
    currentArticle.value = null//新增时，清空当前文章详情，避免编辑时的默认值被覆盖
    dialogVisible.value = true
  } else {
    //编辑
    // 调用获取知识文章详情接口
    const data = await getKnowledgeArticleDetailAPI(row.id)
    currentArticle.value = data
    dialogVisible.value = true
  }
}
// 发布
const handlePublish = async (row: KnowledgeArticle) => {
  // @ts-ignore
  ElMessageBox.confirm("确认发布吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "info",
  }).then(async () => {
    // 调用发布接口
    await publishKnowledgeArticleAPI(row.id, 1)
    //成功提示
    ElMessage.success("发布成功")
    // 刷新表格数据
    handleSearch({})
  })
}
// 下线
const handleOffline = async (row: KnowledgeArticle) => {
  // @ts-ignore
  ElMessageBox.confirm("确认下线吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    // 调用下线接口
    await publishKnowledgeArticleAPI(row.id, 2)
    //成功提示
    ElMessage.success("下线成功")
    // 刷新表格数据
    handleSearch({})
  })
}
// 删除
const handleDelete = async (row: KnowledgeArticle) => {
  // @ts-ignore
  ElMessageBox.confirm("确认删除吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "danger",
  }).then(async () => {
    // 调用删除接口
    await deleteKnowledgeArticleAPI(row.id)
    //成功提示
    ElMessage.success("删除成功")
    // 刷新表格数据
    handleSearch({})
  })
}
// 操作成功方法(子传父回调来的数据)
const handleSuccess = () => {
  // 操作成功后，刷新表格数据
  handleSearch({})
}
</script>
