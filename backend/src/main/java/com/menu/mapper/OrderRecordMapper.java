package com.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.menu.entity.OrderRecord;
import com.menu.vo.StatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface OrderRecordMapper extends BaseMapper<OrderRecord> {
    
    IPage<OrderRecord> selectPageWithDish(IPage<OrderRecord> page, @Param("userId") Long userId, @Param("status") Integer status);
    
    @Select("SELECT o.*, d.name as dish_name, d.image as dish_image FROM order_record o " +
            "LEFT JOIN dish d ON o.dish_id = d.id " +
            "WHERE o.user_id = #{userId} AND o.order_date = #{date}")
    List<OrderRecord> selectTodayOrders(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    List<StatsVO.DishRankVO> selectTopDishes(@Param("userId") Long userId);
}
