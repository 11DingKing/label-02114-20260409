package com.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.menu.config.JwtUtil;
import com.menu.dto.OrderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("点餐接口测试")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        adminToken = "Bearer " + jwtUtil.generateToken(1L, "admin", 1);
        userToken = "Bearer " + jwtUtil.generateToken(2L, "girlfriend", 0);
    }

    @Test
    @DisplayName("用户提交点餐")
    void testSubmitOrder() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setDishId(1L);
        dto.setMealType("breakfast");
        dto.setOrderDate(LocalDate.now());
        dto.setRemark("不要太咸");

        mockMvc.perform(post("/api/order/save")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("点餐失败 - 菜品为空")
    void testSubmitOrderFailEmptyDish() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setMealType("lunch");

        mockMvc.perform(post("/api/order/save")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("点餐失败 - 餐次为空")
    void testSubmitOrderFailEmptyMealType() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setDishId(1L);
        dto.setMealType("");

        mockMvc.perform(post("/api/order/save")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("获取今日点餐")
    void testGetTodayOrders() throws Exception {
        // 先提交一个点餐
        OrderDTO dto = new OrderDTO();
        dto.setDishId(2L);
        dto.setMealType("lunch");

        mockMvc.perform(post("/api/order/save")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // 获取今日点餐
        mockMvc.perform(get("/api/order/today")
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("获取我的点餐记录")
    void testGetMyOrders() throws Exception {
        mockMvc.perform(get("/api/order/my")
                .header("Authorization", userToken)
                .param("current", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @DisplayName("管理员获取所有点餐记录")
    void testGetAllOrders() throws Exception {
        mockMvc.perform(get("/api/order/list")
                .header("Authorization", adminToken)
                .param("current", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @DisplayName("更新点餐状态为已完成")
    void testUpdateOrderStatusToDone() throws Exception {
        // 先提交一个点餐
        OrderDTO dto = new OrderDTO();
        dto.setDishId(1L);
        dto.setMealType("dinner");

        mockMvc.perform(post("/api/order/save")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // 更新状态
        mockMvc.perform(put("/api/order/status/1")
                .header("Authorization", adminToken)
                .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("更新点餐状态为取消")
    void testUpdateOrderStatusToCancel() throws Exception {
        mockMvc.perform(put("/api/order/status/1")
                .header("Authorization", adminToken)
                .param("status", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取点餐统计")
    void testGetOrderStats() throws Exception {
        mockMvc.perform(get("/api/order/stats")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalOrders").isNumber());
    }
}
