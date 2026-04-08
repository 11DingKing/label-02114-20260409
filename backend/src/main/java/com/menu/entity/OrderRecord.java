package com.menu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("order_record")
public class OrderRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long dishId;
    private String mealType;
    private LocalDate orderDate;
    private String remark;
    private Integer status; // 0-待做 1-已做 2-取消
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableField(exist = false)
    private String dishName;
    @TableField(exist = false)
    private Long categoryId;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String nickname;
}
