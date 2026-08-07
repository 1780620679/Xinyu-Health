package com.example.springbootaiproject.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springbootaiproject.DTO.response.CategoryTreeNodeDTO;
import com.example.springbootaiproject.entity.KnowledgeCategory;
import com.example.springbootaiproject.mapper.KnowledgeCategoryMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeCategoryService {

    @Resource
    private KnowledgeCategoryMapper categoryMapper;

    /**
     * 获取分类树（仅返回启用状态的分类）
     */
    public List<CategoryTreeNodeDTO> getCategoryTree() {
        LambdaQueryWrapper<KnowledgeCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeCategory::getStatus, 1)
                .orderByAsc(KnowledgeCategory::getSortOrder);

        List<KnowledgeCategory> categories = categoryMapper.selectList(wrapper);

        return categories.stream().map(cat -> {
            CategoryTreeNodeDTO dto = new CategoryTreeNodeDTO();
            dto.setId(cat.getId());
            dto.setCategoryName(cat.getCategoryName());
            return dto;
        }).collect(Collectors.toList());
    }
}
