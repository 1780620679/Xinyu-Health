package com.example.springbootaiproject.service;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootaiproject.DTO.command.KnowledgeArticlePageDTO;
import com.example.springbootaiproject.DTO.command.KnowledgeArticleSaveDTO;
import com.example.springbootaiproject.DTO.response.KnowledgeArticleVO;
import com.example.springbootaiproject.entity.KnowledgeArticle;
import com.example.springbootaiproject.entity.KnowledgeCategory;
import com.example.springbootaiproject.entity.User;
import com.example.springbootaiproject.mapper.KnowledgeArticleMapper;
import com.example.springbootaiproject.mapper.KnowledgeCategoryMapper;
import com.example.springbootaiproject.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KnowledgeArticleService {

    @Resource
    private KnowledgeArticleMapper articleMapper;

    @Resource
    private KnowledgeCategoryMapper categoryMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 前台/后台：分页查询文章列表
     * 前台（isFront = true）：只返回已发布文章
     * 后台：返回所有文章
     */
    public Page<KnowledgeArticleVO> page(KnowledgeArticlePageDTO dto, boolean isFront) {
        LambdaQueryWrapper<KnowledgeArticle> wrapper = new LambdaQueryWrapper<>();

        // 前台只返回已发布
        if (isFront) {
            wrapper.eq(KnowledgeArticle::getStatus, 1);
        } else if (dto.getStatus() != null) {
            wrapper.eq(KnowledgeArticle::getStatus, dto.getStatus());
        }

        // 标题模糊搜索
        if (CharSequenceUtil.isNotBlank(dto.getTitle())) {
            wrapper.like(KnowledgeArticle::getTitle, dto.getTitle());
        }

        // 分类筛选
        if (dto.getCategoryId() != null) {
            wrapper.eq(KnowledgeArticle::getCategoryId, dto.getCategoryId());
        }

        // 排序
        String sortField = CharSequenceUtil.isNotBlank(dto.getSortField()) ? dto.getSortField() : "createdAt";
        String sortDirection = "DESC".equalsIgnoreCase(dto.getSortDirection()) ? "DESC" : "ASC";

        switch (sortField) {
            case "readCount":
                wrapper.orderBy(true, "DESC".equals(sortDirection), KnowledgeArticle::getReadCount);
                break;
            case "publishedAt":
                wrapper.orderBy(true, "DESC".equals(sortDirection), KnowledgeArticle::getPublishedAt);
                break;
            default:
                wrapper.orderByDesc(KnowledgeArticle::getCreatedAt);
                break;
        }

        long current = dto.getCurrentPage() != null ? dto.getCurrentPage() : 1;
        long size = dto.getSize() != null ? dto.getSize() : 10;
        Page<KnowledgeArticle> page = new Page<>(current, size);
        Page<KnowledgeArticle> articlePage = articleMapper.selectPage(page, wrapper);

        return toVOPage(articlePage);
    }

    /**
     * 获取文章详情（阅读量+1）
     */
    public KnowledgeArticleVO getById(String id) {
        KnowledgeArticle article = articleMapper.selectById(id);
        if (article == null) {
            return null;
        }
        // 阅读量+1
        article.setReadCount(article.getReadCount() == null ? 1 : article.getReadCount() + 1);
        articleMapper.updateById(article);
        return toVO(article);
    }

    /**
     * 创建文章
     */
    public KnowledgeArticleVO create(Long userId, KnowledgeArticleSaveDTO dto) {
        KnowledgeArticle article = KnowledgeArticle.builder()
                .id(IdUtil.fastSimpleUUID())
                .title(dto.getTitle())
                .summary(dto.getSummary())
                .content(dto.getContent())
                .categoryId(dto.getCategoryId())
                .coverImage(dto.getCoverImage())
                .tags(dto.getTags())
                .authorId(userId)
                .readCount(0)
                .status(dto.getStatus() != null ? dto.getStatus() : 0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if (article.getStatus() == 1) {
            article.setPublishedAt(LocalDateTime.now());
        }

        articleMapper.insert(article);
        return toVO(article);
    }

    /**
     * 编辑文章
     */
    public KnowledgeArticleVO update(String id, KnowledgeArticleSaveDTO dto) {
        KnowledgeArticle article = articleMapper.selectById(id);
        if (article == null) {
            return null;
        }

        boolean wasPublished = (article.getStatus() != null && article.getStatus() == 1);

        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setContent(dto.getContent());
        article.setCategoryId(dto.getCategoryId());
        article.setCoverImage(dto.getCoverImage());
        article.setTags(dto.getTags());
        article.setStatus(dto.getStatus() != null ? dto.getStatus() : article.getStatus());
        article.setUpdatedAt(LocalDateTime.now());

        // 如果之前未发布、现在发布了，设置发布时间
        if (!wasPublished && article.getStatus() == 1) {
            article.setPublishedAt(LocalDateTime.now());
        }

        articleMapper.updateById(article);
        return toVO(article);
    }

    /**
     * 发布/下线文章
     */
    public void updateStatus(String id, Integer status) {
        KnowledgeArticle article = articleMapper.selectById(id);
        if (article == null) {
            return;
        }
        article.setStatus(status);
        if (status == 1) {
            article.setPublishedAt(LocalDateTime.now());
        }
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(article);
    }

    /**
     * 删除文章
     */
    public void delete(String id) {
        articleMapper.deleteById(id);
    }

    // ==================== 私有方法 ====================

    private KnowledgeArticleVO toVO(KnowledgeArticle article) {
        KnowledgeArticleVO vo = new KnowledgeArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setContent(article.getContent());
        vo.setCategoryId(article.getCategoryId());
        vo.setCoverImage(article.getCoverImage());
        vo.setTags(article.getTags());
        vo.setAuthorId(article.getAuthorId());
        vo.setReadCount(article.getReadCount());
        vo.setStatus(article.getStatus());
        vo.setPublishedAt(article.getPublishedAt());
        vo.setCreatedAt(article.getCreatedAt());
        vo.setUpdatedAt(article.getUpdatedAt());

        // 填充分类名
        if (article.getCategoryId() != null) {
            KnowledgeCategory cat = categoryMapper.selectById(article.getCategoryId());
            if (cat != null) {
                vo.setCategoryName(cat.getCategoryName());
            }
        }

        return vo;
    }

    private Page<KnowledgeArticleVO> toVOPage(Page<KnowledgeArticle> articlePage) {
        List<KnowledgeArticleVO> voList = articlePage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        // 批量查询作者信息
        List<Long> authorIds = voList.stream()
                .map(KnowledgeArticleVO::getAuthorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> authorMap = Collections.emptyMap();
        if (!authorIds.isEmpty()) {
            authorMap = userMapper.selectBatchIds(authorIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u.getNickname() != null ? u.getNickname() : u.getUsername()));
        }

        for (KnowledgeArticleVO vo : voList) {
            if (vo.getAuthorId() != null) {
                vo.setAuthorName(authorMap.getOrDefault(vo.getAuthorId(), ""));
            }
        }

        Page<KnowledgeArticleVO> result = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        result.setRecords(voList);
        return result;
    }
}
