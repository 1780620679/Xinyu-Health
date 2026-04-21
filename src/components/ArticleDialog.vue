<template>
  <el-dialog
    :title="isEdit ? '编辑文章' : '新增文章'"
    v-model="dialogVisible"
    width="50%"
    @close="handleClose"
  >
    <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
      <!-- 文章标题 -->
      <el-form-item label="文章标题" prop="title">
        <el-input
          v-model="formData.title"
          placeholder="请输入文章标题"
          clearable
          show-word-limit
          maxlength="20"
        />
      </el-form-item>
      <!-- 文章分类 -->
      <el-form-item label="文章分类" prop="categoryId">
        <el-select v-model="formData.categoryId" placeholder="请选择分类">
          <el-option
            v-for="item in categoryList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <!-- 文章摘要 -->
      <el-form-item label="文章摘要" prop="summary">
        <el-input
          type="textarea"
          :rows="4"
          v-model="formData.summary"
          placeholder="请输入文章摘要（可选）"
          clearable
          show-word-limit
          maxlength="1000"
        />
      </el-form-item>
      <!-- 文章标签 -->
      <el-form-item label="标签" prop="tags">
        <el-select
          v-model="formData.tagArray"
          placeholder="请输入文章标签（多个标签用逗号隔开）"
          multiple
          filterable
          allow-create
          clearable
          style="width: 100%"
        >
          <el-option
            v-for="tag in commonTags"
            :key="tag"
            :label="tag"
            :value="tag"
          />
        </el-select>
      </el-form-item>
      <!-- 封面图片 -->
      <el-form-item label="封面图片">
        <div class="cover-upload">
          <el-upload
            class="avatar-uploader"
            action="#"
            :before-upload="beforeUpload"
            :http-request="handleUploadRequest"
            :show-file-list="false"
            accept="image/*"
          >
            <div v-if="!imgUrl" class="cover-placeholder">
              <p>点击上传封面图片</p>
            </div>
            <img v-else :src="imgUrl" alt="封面图片" class="cover-image" />
          </el-upload>
          <div v-if="imgUrl">
            <el-button type="danger" size="small" @click="handleDelete"
              >删除图片</el-button
            >
          </div>
        </div>
      </el-form-item>
      <!-- 富文本编辑器 -->
      <el-form-item label="文章内容" prop="content">
        <RichTextEditor
          v-model="formData.content"
          placeholder="请输入文章内容，支持富文本格式，可以使用加粗，斜体等丰富文章内容"
          :maxChartCount="5000"
          min-height="400px"
          @change="handleCountChange"
          @created="handleEditorCreated"
        />
      </el-form-item>
    </el-form>
    <div v-if="btnPreview">
      <h3>内容预览</h3>
      <div v-html="formData.content"></div>
    </div>
    <template #footer>
      <el-button @click="btnPreview=!btnPreview">{{ btnPreview?'取消预览':'显示预览' }}</el-button>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">{{ isEdit ? '更新文章' : '新增文章' }}</el-button>
    </template>
  </el-dialog>
</template>
<script setup>
import { uploadFileAPI, knowledgeArticleAPI, knowledgeArticleUpdateAPI,} from "@/apis/knowledge"
import { fileBaseURL } from "@/config"
import { ref, computed, onMounted, reactive, nextTick, watch } from "vue"
import RichTextEditor from "@/components/RichTextEditor.vue"
import { ElMessage } from "element-plus"
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  categoryList: {
    type: Array,
    default: () => [],
  },
  // 当前文章详情(新增/编辑)有id值是编辑，没有id值是新增
  currentArticle: {
    type: Object,
    default: null,
   },
})
//判断是否为编辑状态
const isEdit = computed(() => !!props.currentArticle?.id)
//监听编辑数据变化，更新表单数据
watch(() => props.currentArticle, (newVal) => {
  if (newVal) {
    // 等待表单数据更新完成后再赋值
    nextTick(() => {
    // 编辑状态，将当前文章详情赋值给表单数据（不能直接给reactive对象赋值，会丢失响应式数据，否则会导致表单数据不更新）
    Object.assign(formData, newVal)
    //使用现有的id即可
    businessId.value = newVal.id
    imgUrl.value = fileBaseURL + newVal.coverImage
    })
   
  } 
  //新增不需要处理，默认值就可以使用
})
// 定义标签数组
const commonTags = [
  "情绪管理","焦虑",
  "抑郁",
  "压力",
  "睡眠",
  "冥想",
  "正念",
  "放松",
  "心理健康",
  "自我成长",
  "人际关系",
  "工作压力",
  "学习方法",
  "生活技巧",
]
// 定义事件
const emit = defineEmits(["update:modelValue", "success"])
//通过一个计算属性来监听然后触发事件提交进行弹窗的显示和隐藏
const dialogVisible = computed({
  //获取弹窗的显示状态
  get() {
    return props.modelValue
  },
  //设置弹窗的显示状态
  set(val) {
    // 触发事件提交新的显示状态，这个val就是弹窗的显示状态=》true或false
    emit("update:modelValue", val)
  },
})
// 表单数据
const formData = reactive({
  title: '',
  content: '',
  coverImage: '',
  categoryId: 1,
  summary: '',
  tags: '',
  id: '',
})
// 表单校验规则
const rules = reactive({
  title: [
    { required: true, message: "请输入文章标题", trigger: "blur" },
    { max: 200, message: "文章标题最多200个字符", trigger: "blur" },
  ],
  categoryId: [
    { required: true, message: "请选择文章分类", trigger: "change" },
  ],
  summary: [{ max: 1000, message: "文章摘要最多1000个字符", trigger: "blur" }],
  content: [
    { required: true, message: "请输入文章内容", trigger: "blur" },
    { max: 5000, message: "文章内容最多5000个字符", trigger: "blur" }
  ],
})
// 上传图片
const imgUrl = ref("")
// 上传图片前的校验（参数：file对象，返回值：true或false）
const beforeUpload = (file) => {
  // 校验文件类型是否为图片类型
  const isImg = file.type.startsWith("image/")
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImg) {
    ElMessage.error("请上传图片文件")
    return false
  }
  if (!isLt5M) {
    ElMessage.error("图片大小不能超过5MB")
    return false
  }
  return true
}
// 上传图片的唯一标识businessId
const businessId = ref("")
// 上传图片请求(可以从中结构获取到file对象)
const handleUploadRequest = async ({ file }) => {
  //UUID生成，作为图片的唯一标识businessId
   businessId.value = crypto.randomUUID()
  // 调用上传文件接口
  const fileRes = await uploadFileAPI(file, { businessId: businessId.value }) 

  // 上传成功后，将图片URL赋值给imgUrl(并拼接文件名)
  imgUrl.value = fileBaseURL + fileRes.filePath
  formData.coverImage = fileRes.filePath
}
// 删除图片
const handleDelete = () => {
  imgUrl.value = ""
  formData.coverImage = ""
}
// 富文本编辑器
// 富文本编辑器实例
const editorInstance = ref(null)
const handleEditorCreated = (editor) => {
  // console.log(editor, "editor")这个方法得到富文本编辑器实例对象
  editorInstance.value = editor
  //如果是编辑
  if(formData.content&&editor){
    // 等待富文本编辑器实例创建完成后再赋值
    nextTick(()=>{
      editor.setHtml(formData.content)
    })
  }
  //如果是新增
}
const handleCountChange = (data) => {
  // console.log(data) 有html标签和text属性值
  formData.content = data.html
}
// 预览按钮
const btnPreview = ref(false)
// 加载状态
const loading = ref(false)
const formRef = ref()
// 提交表单
const handleSubmit = async () => {
  await formRef.value.validate()
  // 校验通过后，提交表单
  loading.value = true
  const submitData = {
    ...formData,
    tags: formData.tagArray? formData.tagArray.join(",") : "",
  }
  //删除tagArray
  delete submitData.tagArray
  //如果是编辑
  if(isEdit.value){
    // 调用编辑接口
    await knowledgeArticleUpdateAPI(businessId.value, submitData)
  }else{
    // 调用新增接口
    submitData.id=businessId.value
    console.log(submitData);
    
    await knowledgeArticleAPI(submitData)
  }
  loading.value = false
  ElMessage.success("操作成功")
  emit("success")
  dialogVisible.value = false
}
// 关闭弹窗方法
const handleClose = () => {
  // 关闭弹窗时，清空表单数据
  formRef.value.resetFields()
   //重置标签
  formData.tagArray = []
  // 清空上传图片的唯一标识businessId
  businessId.value = ""
  // 清空上传图片的URL
  handleDelete()
  dialogVisible.value = false
}
</script>
<style scoped lang="scss">
.cover-placeholder {
  width: 200px;
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8b949e;
  background: #f6f8fa;
}
.cover-image {
  width: 200px;
  height: 120px;
  display: block;
}
</style>
