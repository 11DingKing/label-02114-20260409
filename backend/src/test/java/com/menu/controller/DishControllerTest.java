package com.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.menu.config.JwtUtil;
import com.menu.dto.DishDTO;
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
@DisplayName("菜品接口测试")
class DishControllerTest {

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
    @DisplayName("分页查询菜品列表")
    void testGetDishList() throws Exception {
        mockMvc.perform(get("/api/dish/list")
                .header("Authorization", adminToken)
                .param("current", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").value(3));
    }

    @Test
    @DisplayName("按分类查询菜品")
    void testGetDishByCategory() throws Exception {
        mockMvc.perform(get("/api/dish/listByCategory")
                .header("Authorization", userToken)
                .param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("随机推荐菜品")
    void testGetRandomDish() throws Exception {
        mockMvc.perform(get("/api/dish/random")
                .header("Authorization", userToken)
                .param("count", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("获取菜品详情")
    void testGetDishById() throws Exception {
        mockMvc.perform(get("/api/dish/1")
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("爱心煎蛋"));
    }

    @Test
    @DisplayName("新增菜品")
    void testAddDish() throws Exception {
        DishDTO dto = new DishDTO();
        dto.setCategoryId(1L);
        dto.setName("牛奶燕麦粥");
        dto.setDescription("营养满满的早餐");
        dto.setDifficulty(1);
        dto.setCookTime(15);

        mockMvc.perform(post("/api/dish/save")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增菜品失败 - 名称为空")
    void testAddDishFailEmptyName() throws Exception {
        DishDTO dto = new DishDTO();
        dto.setCategoryId(1L);
        dto.setName("");

        mockMvc.perform(post("/api/dish/save")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("新增菜品失败 - 分类为空")
    void testAddDishFailEmptyCategory() throws Exception {
        DishDTO dto = new DishDTO();
        dto.setName("测试菜品");

        mockMvc.perform(post("/api/dish/save")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("更新菜品")
    void testUpdateDish() throws Exception {
        DishDTO dto = new DishDTO();
        dto.setId(1L);
        dto.setCategoryId(1L);
        dto.setName("爱心煎蛋升级版");
        dto.setDescription("更美味的煎蛋");
        dto.setDifficulty(2);
        dto.setCookTime(12);

        mockMvc.perform(post("/api/dish/save")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除菜品")
    void testDeleteDish() throws Exception {
        mockMvc.perform(delete("/api/dish/3")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("关键字搜索菜品")
    void testSearchDish() throws Exception {
        mockMvc.perform(get("/api/dish/list")
                .header("Authorization", adminToken)
                .param("keyword", "煎蛋"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].name").value("爱心煎蛋"));
    }
}
