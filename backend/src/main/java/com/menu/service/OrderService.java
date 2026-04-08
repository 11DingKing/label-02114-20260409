package com.menu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.menu.common.PageResult;
import com.menu.dto.OrderDTO;
import com.menu.entity.OrderRecord;
import com.menu.enums.MealType;
import com.menu.enums.OrderStatus;
import com.menu.exception.BusinessException;
import com.menu.mapper.OrderRecordMapper;
import com.menu.vo.StatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRecordMapper orderRecordMapper;
    private final DishService dishService;
    
    /** 允许补点的最大天数（过去） */
    private static final int MAX_PAST_DAYS = 3;
    /** 允许预点的最大天数（未来） */
    private static final int MAX_FUTURE_DAYS = 7;
    
    public PageResult<OrderRecord> page(int current, int size, Long userId, Integer status) {
        log.debug("分页查询点餐记录: current={}, size={}, userId={}, status={}", current, size, userId, status);
        
        // 校验状态码
        if (status != null && !OrderStatus.isValid(status)) {
            throw new BusinessException("无效的状态参数");
        }
        
        IPage<OrderRecord> page = orderRecordMapper.selectPageWithDish(
            new Page<>(current, size), userId, status
        );
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }
    
    public List<OrderRecord> getTodayOrders(Long userId) {
        LocalDate today = LocalDate.now();
        log.debug("查询今日点餐: userId={}, date={}", userId, today);
        return orderRecordMapper.selectTodayOrders(userId, today);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void save(OrderDTO dto, Long userId) {
        // 校验餐次类型
        if (!MealType.isValid(dto.getMealType())) {
            throw new BusinessException("无效的餐次类型");
        }
        
        // 校验菜品是否存在
        if (dishService.getById(dto.getDishId()) == null) {
            throw new BusinessException("菜品不存在");
        }
        
        // 校验点餐日期
        LocalDate orderDate = dto.getOrderDate() != null ? dto.getOrderDate() : LocalDate.now();
        validateOrderDate(orderDate);
        
        OrderRecord order = new OrderRecord();
        order.setUserId(userId);
        order.setDishId(dto.getDishId());
        order.setMealType(dto.getMealType());
        order.setOrderDate(orderDate);
        order.setRemark(dto.getRemark());
        order.setStatus(OrderStatus.PENDING.getCode());
        
        orderRecordMapper.insert(order);
        dishService.incrementOrderCount(dto.getDishId());
        log.info("点餐成功: userId={}, dishId={}, mealType={}, date={}", 
                userId, dto.getDishId(), dto.getMealType(), order.getOrderDate());
    }
    
    /**
     * 校验点餐日期
     * - 允许补点过去 MAX_PAST_DAYS 天内的餐
     * - 允许预点未来 MAX_FUTURE_DAYS 天内的餐
     */
    private void validateOrderDate(LocalDate orderDate) {
        LocalDate today = LocalDate.now();
        LocalDate minDate = today.minusDays(MAX_PAST_DAYS);
        LocalDate maxDate = today.plusDays(MAX_FUTURE_DAYS);
        
        if (orderDate.isBefore(minDate)) {
            throw new BusinessException("只能补点" + MAX_PAST_DAYS + "天内的餐");
        }
        if (orderDate.isAfter(maxDate)) {
            throw new BusinessException("只能预点" + MAX_FUTURE_DAYS + "天内的餐");
        }
    }
    
    public void updateStatus(Long id, Integer status) {
        // 校验状态码
        if (!OrderStatus.isValid(status)) {
            throw new BusinessException("无效的状态参数");
        }
        
        OrderRecord order = orderRecordMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("点餐记录不存在");
        }
        
        Integer oldStatus = order.getStatus();
        order.setStatus(status);
        orderRecordMapper.updateById(order);
        log.info("更新点餐状态: id={}, oldStatus={}, newStatus={}", id, oldStatus, status);
    }
    
    public StatsVO getStats(Long userId) {
        log.debug("获取点餐统计: userId={}", userId);
        StatsVO stats = new StatsVO();
        
        // 总点餐数
        Long totalOrders = orderRecordMapper.selectCount(
            new LambdaQueryWrapper<OrderRecord>()
                .eq(userId != null, OrderRecord::getUserId, userId)
        );
        stats.setTotalOrders(totalOrders.intValue());
        
        // 已完成数
        Long completedOrders = orderRecordMapper.selectCount(
            new LambdaQueryWrapper<OrderRecord>()
                .eq(userId != null, OrderRecord::getUserId, userId)
                .eq(OrderRecord::getStatus, OrderStatus.COMPLETED.getCode())
        );
        stats.setCompletedOrders(completedOrders.intValue());
        
        // 菜品排行
        List<StatsVO.DishRankVO> topDishes = orderRecordMapper.selectTopDishes(userId);
        stats.setTopDishes(topDishes);
        
        log.debug("统计结果: totalOrders={}, completedOrders={}, topDishes={}", 
                stats.getTotalOrders(), stats.getCompletedOrders(), topDishes.size());
        return stats;
    }
}
