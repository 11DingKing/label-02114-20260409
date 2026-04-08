package com.menu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.menu.dto.CategoryDTO;
import com.menu.entity.Category;
import com.menu.exception.BusinessException;
import com.menu.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryMapper categoryMapper;
    
    public List<Category> list() {
        log.debug("查询启用的分类列表");
        List<Category> categories = categoryMapper.selectList(
            new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSortOrder)
        );
        log.debug("查询到 {} 个分类", categories.size());
        return categories;
    }
    
    public List<Category> listAll() {
        log.debug("查询所有分类列表");
        return categoryMapper.selectList(
            new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder)
        );
    }
    
    public void save(CategoryDTO dto) {
        // 校验分类名称
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BusinessException("分类名称不能为空");
        }
        
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        if (dto.getId() == null) {
            categoryMapper.insert(category);
            log.info("新增分类成功: id={}, name={}", category.getId(), dto.getName());
        } else {
            categoryMapper.updateById(category);
            log.info("更新分类成功: id={}, name={}", dto.getId(), dto.getName());
        }
    }
    
    public void delete(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        categoryMapper.deleteById(id);
        log.info("删除分类成功: id={}, name={}", id, category.getName());
    }
}
