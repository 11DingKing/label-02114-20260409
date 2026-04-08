package com.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class OrderDTO {
    private Long id;
    @NotNull(message = "菜品不能为空")
    private Long dishId;
    @NotBlank(message = "餐次不能为空")
    private String mealType;
    private LocalDate orderDate;
    private String remark;
}
