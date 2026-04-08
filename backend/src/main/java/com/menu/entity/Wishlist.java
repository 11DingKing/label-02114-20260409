package com.menu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wishlist")
public class Wishlist {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String dishName;
    private String description;
    private Integer status; // 0-待添加 1-已添加
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
