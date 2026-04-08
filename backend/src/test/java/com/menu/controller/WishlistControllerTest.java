package com.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.menu.config.JwtUtil;
import com.menu.dto.WishlistDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("心愿单接口测试")
class WishlistControllerTest {

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
    @DisplayName("添加心愿")
    void testAddWishlist() throws Exception {
        WishlistDTO dto = new WishlistDTO();
        dto.setDishName("红烧肉");
        dto.setDescription("想吃软糯的红烧肉");

        mockMvc.perform(post("/api/wishlist/save")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("添加心愿失败 - 菜名为空")
    void testAddWishlistFailEmptyName() throws Exception {
        WishlistDTO dto = new WishlistDTO();
        dto.setDishName("");

        mockMvc.perform(post("/api/wishlist/save")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("获取心愿列表 - 用户")
    void testGetWishlistByUser() throws Exception {
        // 先添加心愿
        WishlistDTO dto = new WishlistDTO();
        dto.setDishName("糖醋排骨");

        mockMvc.perform(post("/api/wishlist/save")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // 获取列表
        mockMvc.perform(get("/api/wishlist/list")
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("获取心愿列表 - 管理员查看全部")
    void testGetWishlistByAdmin() throws Exception {
        mockMvc.perform(get("/api/wishlist/list")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("更新心愿状态为已添加")
    void testUpdateWishlistStatus() throws Exception {
        // 先添加心愿
        WishlistDTO dto = new WishlistDTO();
        dto.setDishName("宫保鸡丁");

        mockMvc.perform(post("/api/wishlist/save")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // 更新状态
        mockMvc.perform(put("/api/wishlist/status/1")
                .header("Authorization", adminToken)
                .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除心愿")
    void testDeleteWishlist() throws Exception {
        // 先添加心愿
        WishlistDTO dto = new WishlistDTO();
        dto.setDishName("待删除心愿");

        mockMvc.perform(post("/api/wishlist/save")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // 删除
        mockMvc.perform(delete("/api/wishlist/1")
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
