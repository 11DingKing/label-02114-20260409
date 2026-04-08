package com.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.menu.dto.LoginDTO;
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
@DisplayName("认证接口测试")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("管理员登录成功")
    void testAdminLoginSuccess() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("admin123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.role").value(1));
    }

    @Test
    @DisplayName("女友登录成功")
    void testGirlfriendLoginSuccess() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("girlfriend");
        dto.setPassword("love123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.role").value(0));
    }

    @Test
    @DisplayName("登录失败 - 用户名为空")
    void testLoginFailEmptyUsername() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("");
        dto.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void testLoginFailWrongPassword() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("密码错误"));
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void testLoginFailUserNotFound() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("notexist");
        dto.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    @DisplayName("未登录访问受保护接口")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/auth/info"))
                .andExpect(status().isUnauthorized());
    }
}
