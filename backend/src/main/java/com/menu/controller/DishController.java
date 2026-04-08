package com.menu.controller;

import com.menu.common.PageResult;
import com.menu.common.Result;
import com.menu.config.AdminRequired;
import com.menu.dto.DishDTO;
import com.menu.entity.Dish;
import com.menu.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "菜品管理", description = "菜品的增删改查、随机推荐")
@RestController
@RequestMapping("/api/dish")
@RequiredArgsConstructor
public class DishController {
    
    private final DishService dishService;
    
    @Operation(summary = "分页查询菜品列表")
    @GetMapping("/list")
    public Result<PageResult<Dish>> list(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        return Result.success(dishService.page(current, size, categoryId, keyword));
    }
    
    @Operation(summary = "按分类获取菜品列表")
    @GetMapping("/listByCategory")
    public Result<List<Dish>> listByCategory(
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId) {
        return Result.success(dishService.listByCategory(categoryId));
    }
    
    @Operation(summary = "随机推荐菜品", description = "随机获取指定数量的菜品")
    @GetMapping("/random")
    public Result<List<Dish>> random(
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "5") int count) {
        return Result.success(dishService.random(count));
    }
    
    @Operation(summary = "获取菜品详情")
    @GetMapping("/{id}")
    public Result<Dish> getById(@Parameter(description = "菜品ID") @PathVariable Long id) {
        return Result.success(dishService.getById(id));
    }
    
    @AdminRequired
    @Operation(summary = "保存菜品", description = "新增或更新菜品信息")
    @PostMapping("/save")
    public Result<Void> save(@Valid @RequestBody DishDTO dto) {
        dishService.save(dto);
        return Result.success();
    }
    
    @AdminRequired
    @Operation(summary = "删除菜品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "菜品ID") @PathVariable Long id) {
        dishService.delete(id);
        return Result.success();
    }
}
