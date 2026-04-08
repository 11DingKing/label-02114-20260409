package com.menu.vo;

import lombok.Data;
import java.util.List;

@Data
public class StatsVO {
    private Integer totalOrders;
    private Integer completedOrders;
    private Integer totalDishes;
    private List<DishRankVO> topDishes;

    @Data
    public static class DishRankVO {
        private Long dishId;
        private String dishName;
        private Integer orderCount;
    }
}
