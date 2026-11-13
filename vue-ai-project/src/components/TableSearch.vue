<template>
  <el-form ref="ruleFormRef" :model="formData">
    <el-row :gutter="24">
      <template v-for="item in formDataAttrs">
        <el-col v-bind="item.col">
          <el-form-item :label="item.label" :prop="item.prop">
            <!--
              特殊筛选扩展：配置项带 slot: true 时走具名插槽，由父组件自定义控件
              - 适用场景：日期区间、级联选择等内置类型覆盖不到的个性化筛选
              - 通过 :form-data 把响应式 formData 透传给父组件，父组件用
                <template #propName="{ formData }"> 拿到后直接 v-model="formData.xxx"
                保证数据仍统一收口在 formData 中，搜索/重置逻辑不受影响
              - 组件对未知控件保持"开闭原则"：对扩展开放、对修改关闭
            -->
            <template v-if="item.slot">
              <slot :name="item.prop" :form-data="formData"></slot>
            </template>
            <!-- 普通筛选：按 comp 类型映射内置组件，动态绑定字段值 -->
            <component
              v-else
              :is="isComp(item.comp)"
              v-model="formData[item.prop]"
              :placeholder="item.placeholder"
            >
              <template v-if="item.comp === 'select'">
                <el-option
                  v-for="opt in item.options"
                  :key="opt.value"
                  :label="opt.label"
                  :value="opt.value"
                />
              </template>
            </component>
          </el-form-item>
        </el-col>
      </template>
    </el-row>

    <el-button type="primary" @click="handleSearch">搜索</el-button>
    <el-button @click="handleReset(ruleFormRef)">重置</el-button>
  </el-form>
</template>
<script setup>
import { computed, reactive, ref } from "vue"

const props = defineProps({
  formItem: {
    type: Array,
    default: () => [],
  },
})
const emit = defineEmits(["search"])

const ruleFormRef = ref()
// 搜索表单数据
const formData = reactive({})
// 根据组件类型返回对应的组件
const isComp = (comp) => {
  return {
    input: "el-input",
    select: "el-select",
  }[comp]
}
//设置响应式布局（根据屏幕宽度自动调整）
const formDataAttrs = computed(() => {
  const { formItem } = props
  formItem.forEach((item) => {
    item.col = { xs: 24, sm: 12, md: 8, lg: 6, xl: 4 } //设置响应式布局（根据屏幕宽度自动调整）默认是24列
  })
  return formItem
})
// 搜索方法
const handleSearch = () => {
  emit("search", formData)
}
// 重置方法 不能简单的formData = {};因为有的表单的默认值并非是空字符串 要查询官网文档进行重置操作
const handleReset = (formEl) => {
  //先重置
  if (!formEl) return
  formEl.resetFields()
  //再提交调用一下
  handleSearch()
}
</script>
