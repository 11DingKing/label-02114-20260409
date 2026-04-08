package com.menu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.menu.dto.WishlistDTO;
import com.menu.entity.Wishlist;
import com.menu.enums.WishlistStatus;
import com.menu.exception.BusinessException;
import com.menu.mapper.WishlistMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {
    
    private final WishlistMapper wishlistMapper;
    
    public List<Wishlist> list(Long userId) {
        log.debug("查询用户心愿列表: userId={}", userId);
        return wishlistMapper.selectList(
            new LambdaQueryWrapper<Wishlist>()
                .eq(Wishlist::getUserId, userId)
                .orderByDesc(Wishlist::getCreatedAt)
        );
    }
    
    public List<Wishlist> listAll() {
        log.debug("查询所有心愿列表");
        return wishlistMapper.selectList(
            new LambdaQueryWrapper<Wishlist>().orderByDesc(Wishlist::getCreatedAt)
        );
    }
    
    public void save(WishlistDTO dto, Long userId) {
        // 校验菜名不能为空
        if (dto.getDishName() == null || dto.getDishName().trim().isEmpty()) {
            throw new BusinessException("菜名不能为空");
        }
        
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setDishName(dto.getDishName().trim());
        wishlist.setDescription(dto.getDescription());
        wishlist.setStatus(WishlistStatus.PENDING.getCode());
        
        wishlistMapper.insert(wishlist);
        log.info("添加心愿成功: userId={}, dishName={}", userId, dto.getDishName());
    }
    
    public void delete(Long id, Long currentUserId, Integer role) {
        Wishlist wishlist = wishlistMapper.selectById(id);
        if (wishlist == null) {
            throw new BusinessException("心愿不存在");
        }
        
        // 非管理员只能删除自己的心愿
        if (role != 1 && !wishlist.getUserId().equals(currentUserId)) {
            throw new BusinessException("无权删除他人的心愿");
        }
        
        wishlistMapper.deleteById(id);
        log.info("删除心愿成功: id={}, dishName={}", id, wishlist.getDishName());
    }
    
    public void updateStatus(Long id, Integer status) {
        // 校验状态枚举值
        if (!WishlistStatus.isValid(status)) {
            throw new BusinessException("无效的心愿状态参数，仅允许 0(待添加) 或 1(已添加)");
        }
        
        Wishlist wishlist = wishlistMapper.selectById(id);
        if (wishlist == null) {
            throw new BusinessException("心愿不存在");
        }
        
        Integer oldStatus = wishlist.getStatus();
        wishlist.setStatus(status);
        wishlistMapper.updateById(wishlist);
        log.info("更新心愿状态: id={}, dishName={}, oldStatus={}, newStatus={}", 
                id, wishlist.getDishName(), oldStatus, status);
    }
}
