package com.menu.controller;

import com.menu.common.Result;
import com.menu.config.AdminRequired;
import com.menu.dto.CategoryDTO;
import com.menu.entity.Category;
import com.menu.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "分类管理", description = "菜品分类的增删改查")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @Operation(summary = "获取启用的分类列表")
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }
    
    @AdminRequired
    @Operation(summary = "获取所有分类列表", description = "包含禁用的分类")
    @GetMapping("/listAll")
    public Result<List<Category>> listAll() {
        return Result.success(categoryService.listAll());
    }
    
    @AdminRequired
    @Operation(summary = "保存分类", description = "新增或更新分类信息")
    @PostMapping("/save")
    public Result<Void> save(@Valid @RequestBody CategoryDTO dto) {
        categoryService.save(dto);
        return Result.success();
    }
    
    @AdminRequired
    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "分类ID") @PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
