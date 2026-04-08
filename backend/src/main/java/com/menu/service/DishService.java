package com.menu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.menu.common.PageResult;
import com.menu.dto.DishDTO;
import com.menu.entity.Dish;
import com.menu.exception.BusinessException;
import com.menu.mapper.DishMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DishService {
    
    private final DishMapper dishMapper;
    
    public PageResult<Dish> page(int current, int size, Long categoryId, String keyword) {
        log.debug("分页查询菜品: current={}, size={}, categoryId={}, keyword={}", current, size, categoryId, keyword);
        IPage<Dish> page = dishMapper.selectPageWithCategory(
            new Page<>(current, size), categoryId, keyword
        );
        log.debug("查询到 {} 条菜品记录", page.getTotal());
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }
    
    public List<Dish> listByCategory(Long categoryId) {
        log.debug("按分类查询菜品: categoryId={}", categoryId);
        return dishMapper.selectList(
            new LambdaQueryWrapper<Dish>()
                .eq(categoryId != null, Dish::getCategoryId, categoryId)
                .eq(Dish::getStatus, 1)
                .orderByDesc(Dish::getOrderCount)
        );
    }
    
    public List<Dish> random(int count) {
        log.debug("随机推荐菜品: count={}", count);
        List<Dish> dishes = dishMapper.selectRandom(count);
        log.debug("随机推荐了 {} 道菜品", dishes.size());
        return dishes;
    }
    
    public Dish getById(Long id) {
        log.debug("获取菜品详情: id={}", id);
        return dishMapper.selectById(id);
    }
    
    public void save(DishDTO dto) {
        // 校验菜品名称
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BusinessException("菜品名称不能为空");
        }
        if (dto.getCategoryId() == null) {
            throw new BusinessException("请选择分类");
        }
        
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);
        if (dto.getId() == null) {
            dish.setOrderCount(0);
            dishMapper.insert(dish);
            log.info("新增菜品成功: id={}, name={}, categoryId={}", dish.getId(), dto.getName(), dto.getCategoryId());
        } else {
            dishMapper.updateById(dish);
            log.info("更新菜品成功: id={}, name={}", dto.getId(), dto.getName());
        }
    }
    
    public void delete(Long id) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }
        
        dishMapper.deleteById(id);
        log.info("删除菜品成功: id={}, name={}", id, dish.getName());
    }
    
    public void incrementOrderCount(Long id) {
        int rows = dishMapper.incrementOrderCount(id);
        if (rows > 0) {
            log.debug("菜品点餐次数+1: id={}", id);
        }
    }
}
