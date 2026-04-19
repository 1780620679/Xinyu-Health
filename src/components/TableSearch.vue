<template>
  <el-form ref="ruleFormRef" :model="formData">
    <el-row :gutter="24">
      <template v-for="item in formDataAttrs">
        <el-col v-bind="item.col">
          <el-form-item :label="item.label" :prop="item.prop">
            <component
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
import { computed, reactive, ref } from "vue";

const props = defineProps({
  formItem: {
    type: Array,
    default: () => [],
  },
});
const emit = defineEmits(["search"]);

const ruleFormRef = ref();
// 搜索表单数据
const formData = reactive({});
// 根据组件类型返回对应的组件
const isComp = (comp) => {
  return {
    input: "el-input",
    select: "el-select",
  }[comp];
};
//设置响应式布局（根据屏幕宽度自动调整）
const formDataAttrs = computed(() => {
  const { formItem } = props;
  formItem.forEach((item) => {
    item.col = { xs: 24, sm: 12, md: 8, lg: 6, xl: 4 };
  });
  return formItem;
});
// 搜索方法
const handleSearch = () => {
  emit("search", formData);
};
// 重置方法 不能简单的formData = {};因为有的表单的默认值并非是空字符串 要查询官网文档进行重置
const handleReset = (formEl) => {
  //先重置
  if (!formEl) return;
  formEl.resetFields();
  //再提交调用一下
  handleSearch();
};
</script>
