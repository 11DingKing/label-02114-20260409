package com.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.menu.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    
    @Select("SELECT d.*, c.name as category_name FROM dish d " +
            "LEFT JOIN category c ON d.category_id = c.id " +
            "WHERE d.status = 1 ORDER BY RAND() LIMIT #{count}")
    List<Dish> selectRandom(@Param("count") int count);
    
    IPage<Dish> selectPageWithCategory(IPage<Dish> page, @Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    @Update("UPDATE dish SET order_count = order_count + 1, updated_at = NOW() WHERE id = #{id}")
    int incrementOrderCount(@Param("id") Long id);
}
