import { dayjs } from 'element-plus'

// 与情绪日志表格的“记录日期”列保持一致，只展示年月日。
export const formatDate = (value?: string | null): string => {
  if (!value) return '-'
  const date = dayjs(value)
  return date.isValid() ? date.format('YYYY-MM-DD') : '-'
}
