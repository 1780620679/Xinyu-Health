<template>
  <el-dialog
    title="文章详情"
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
    </el-form>
  </el-dialog>
</template>
<script setup>
import { uploadFileAPI } from "@/apis/knowledge"
import { fileBaseURL } from "@/config"
import { ref, computed, onMounted, reactive } from "vue"
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  categoryList: {
    type: Array,
    default: () => [],
  },
})
// 定义标签数组
const commonTags = [
  "情绪管理",
  "焦虑",
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
const emit = defineEmits(["update:modelValue"])
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
  title: "",
  content: "",
  coverImage: "",
  categoryId: 1,
  summary: "",
  tags: "",
  id: "",
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
})
// 上传图片
const imgUrl = ref("")
// 上传图片前的校验
const beforeUpload = (file) => {
  // 校验文件类型
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
// 上传图片请求(可以从中结构获取到file对象)
const handleUploadRequest = async ({ file }) => {
  //UUID生成，作为图片的唯一标识businessId
  const businessId = crypto.randomUUID()
  // 调用上传文件接口
  const fileRes = await uploadFileAPI(file, { businessId: businessId })

  // 上传成功后，将图片URL赋值给imgUrl(并拼接文件名)
  imgUrl.value = fileBaseURL + fileRes.filePath
  formData.coverImage = fileRes.filePath
}
// 删除图片
const handleDelete = () => {
  imgUrl.value = ""
  formData.coverImage = ""
}

// 关闭弹窗方法
const handleClose = () => {}
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
