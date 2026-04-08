package com.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DishDTO {
    private Long id;
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    @NotBlank(message = "菜品名称不能为空")
    private String name;
    private String description;
    private Integer difficulty = 3;
    private Integer cookTime = 30;
    private Integer status = 1;
}
