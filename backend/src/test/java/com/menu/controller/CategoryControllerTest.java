package com.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.menu.config.JwtUtil;
import com.menu.dto.CategoryDTO;
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
@DisplayName("分类接口测试")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = "Bearer " + jwtUtil.generateToken(1L, "admin", 1);
    }

    @Test
    @DisplayName("获取分类列表")
    void testGetCategoryList() throws Exception {
        mockMvc.perform(get("/api/category/list")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    @DisplayName("新增分类")
    void testAddCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("甜点");
        dto.setIcon("cake");
        dto.setSortOrder(4);

        mockMvc.perform(post("/api/category/save")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增分类失败 - 名称为空")
    void testAddCategoryFailEmptyName() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("");

        mockMvc.perform(post("/api/category/save")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("更新分类")
    void testUpdateCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(1L);
        dto.setName("早餐更新");
        dto.setIcon("sunrise");
        dto.setSortOrder(1);

        mockMvc.perform(post("/api/category/save")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("删除分类")
    void testDeleteCategory() throws Exception {
        // 先添加一个分类
        CategoryDTO dto = new CategoryDTO();
        dto.setName("待删除分类");
        
        mockMvc.perform(post("/api/category/save")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // 删除分类
        mockMvc.perform(delete("/api/category/4")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
