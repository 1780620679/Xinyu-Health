<template>
  <div>
    <PageHead title="知识文章">
      <template #buttons>
        <el-button type="primary">新增</el-button>
      </template>
    </PageHead>
    <TableSearch :formItem="formItem" @search="handleSearch"></TableSearch>
  </div>
</template>
<script setup>
import PageHead from "@/components/PageHead.vue";
import TableSearch from "@/components/TableSearch.vue";
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
    options: [
      {
        label: "心理健康基础",
        value: 1,
      },
      {
        label: "情绪管理",
        value: 2,
      },
    ],
  },
];
// 搜索方法(子传父回调来的数据)
const handleSearch = (formData) => {
  console.log(formData);
};
</script>
