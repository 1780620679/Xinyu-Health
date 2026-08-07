package com.example.springbootaiproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootaiproject.entity.KnowledgeArticle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeArticleMapper extends BaseMapper<KnowledgeArticle> {
}
