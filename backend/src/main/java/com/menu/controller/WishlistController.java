package com.menu.controller;

import com.menu.common.Result;
import com.menu.config.AdminRequired;
import com.menu.dto.WishlistDTO;
import com.menu.entity.Wishlist;
import com.menu.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "心愿清单", description = "心愿的增删改查")
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    
    private final WishlistService wishlistService;
    
    @Operation(summary = "获取心愿列表", description = "管理员查看全部，用户只看自己的")
    @GetMapping("/list")
    public Result<List<Wishlist>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer role = (Integer) request.getAttribute("role");
        return Result.success(role == 1 ? wishlistService.listAll() : wishlistService.list(userId));
    }
    
    @Operation(summary = "添加心愿")
    @PostMapping("/save")
    public Result<Void> save(@Valid @RequestBody WishlistDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        wishlistService.save(dto, userId);
        return Result.success();
    }
    
    @Operation(summary = "删除心愿")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "心愿ID") @PathVariable Long id,
                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer role = (Integer) request.getAttribute("role");
        wishlistService.delete(id, userId, role);
        return Result.success();
    }
    
    @AdminRequired
    @Operation(summary = "更新心愿状态")
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(
            @Parameter(description = "心愿ID") @PathVariable Long id, 
            @Parameter(description = "状态: 0-待添加 1-已添加") @RequestParam Integer status) {
        wishlistService.updateStatus(id, status);
        return Result.success();
    }
}
