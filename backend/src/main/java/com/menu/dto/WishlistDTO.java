package com.menu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WishlistDTO {
    private Long id;
    @NotBlank(message = "菜名不能为空")
    private String dishName;
    private String description;
}
