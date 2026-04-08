package com.menu.controller;

import com.menu.common.PageResult;
import com.menu.common.Result;
import com.menu.config.AdminRequired;
import com.menu.dto.OrderDTO;
import com.menu.entity.OrderRecord;
import com.menu.service.OrderService;
import com.menu.vo.StatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "订单管理", description = "点餐记录的增删改查、统计")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @AdminRequired
    @Operation(summary = "分页查询订单列表", description = "管理员可查看所有订单")
    @GetMapping("/list")
    public Result<PageResult<OrderRecord>> list(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status) {
        return Result.success(orderService.page(current, size, userId, status));
    }
    
    @Operation(summary = "获取我的订单列表")
    @GetMapping("/my")
    public Result<PageResult<OrderRecord>> myOrders(
            HttpServletRequest request,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.page(current, size, userId, status));
    }
    
    @Operation(summary = "获取今日订单")
    @GetMapping("/today")
    public Result<List<OrderRecord>> today(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.getTodayOrders(userId));
    }
    
    @Operation(summary = "提交点餐", description = "用户提交点餐请求")
    @PostMapping("/save")
    public Result<Void> save(@Valid @RequestBody OrderDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.save(dto, userId);
        return Result.success();
    }
    
    @AdminRequired
    @Operation(summary = "更新订单状态")
    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(
            @Parameter(description = "订单ID") @PathVariable Long id, 
            @Parameter(description = "状态: 0-待处理 1-已完成 2-已取消") @RequestParam Integer status) {
        orderService.updateStatus(id, status);
        return Result.success();
    }
    
    @Operation(summary = "获取统计数据", description = "获取点餐统计信息")
    @GetMapping("/stats")
    public Result<StatsVO> stats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer role = (Integer) request.getAttribute("role");
        return Result.success(orderService.getStats(role == 1 ? null : userId));
    }
}
