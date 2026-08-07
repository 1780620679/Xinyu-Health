package com.example.springbootaiproject.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootaiproject.DTO.command.KnowledgeArticlePageDTO;
import com.example.springbootaiproject.DTO.command.KnowledgeArticleSaveDTO;
import com.example.springbootaiproject.DTO.response.CategoryTreeNodeDTO;
import com.example.springbootaiproject.DTO.response.KnowledgeArticleVO;
import com.example.springbootaiproject.common.Result;
import com.example.springbootaiproject.service.KnowledgeArticleService;
import com.example.springbootaiproject.service.KnowledgeCategoryService;
import com.example.springbootaiproject.utils.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识文章 & 分类控制器
 */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeArticleController {

    @Resource
    private KnowledgeArticleService articleService;

    @Resource
    private KnowledgeCategoryService categoryService;

    // ==================== 分类 ====================

    @GetMapping("/category/tree")
    public Result<List<CategoryTreeNodeDTO>> categoryTree() {
        return Result.ok(categoryService.getCategoryTree());
    }

    // ==================== 文章列表（前台+后台共用） ====================

    /**
     * 分页查询文章列表
     * 未传 status → 前台模式：仅返回已发布（status=1）
     * 传入 status → 后台模式：按指定状态筛选
     */
    @GetMapping("/article/page")
    public Result<Page<KnowledgeArticleVO>> page(KnowledgeArticlePageDTO dto,
                                                  @RequestParam(name = "status", required = false) Integer statusParam) {
        // 前台一定会传 sortField，不传 sortField 的视为后台请求
        boolean isFront = (dto.getSortField() != null);
        if (statusParam != null) {
            dto.setStatus(statusParam);
        }
        return Result.ok(articleService.page(dto, isFront));
    }

    // ==================== 文章详情 ====================

    @GetMapping("/article/{id}")
    public Result<KnowledgeArticleVO> detail(@PathVariable String id) {
        KnowledgeArticleVO vo = articleService.getById(id);
        if (vo == null) {
            return Result.error("文章不存在");
        }
        return Result.ok(vo);
    }

    // ==================== 后台管理 ====================

    @PostMapping("/article")
    public Result<KnowledgeArticleVO> create(@Valid @RequestBody KnowledgeArticleSaveDTO dto) {
        Long userId = getCurrentUserId();
        return Result.ok(articleService.create(userId, dto));
    }

    @PutMapping("/article/{id}")
    public Result<KnowledgeArticleVO> update(@Valid @RequestBody KnowledgeArticleSaveDTO dto) {
        KnowledgeArticleVO vo = articleService.update(dto.getId(), dto);
        if (vo == null) {
            return Result.error("文章不存在");
        }
        return Result.ok(vo);
    }

    @PutMapping("/article/{id}/status")
    public Result<Void> updateStatus(@PathVariable String id,
                                      @RequestBody java.util.Map<String, Integer> body) {
        articleService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    @DeleteMapping("/article/{id}")
    public Result<Void> delete(@PathVariable String id) {
        articleService.delete(id);
        return Result.ok();
    }

    private Long getCurrentUserId() {
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        return jwt.getClaim("userId").asLong();
    }
}
