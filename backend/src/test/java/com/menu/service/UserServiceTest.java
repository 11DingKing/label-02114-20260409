package com.menu.service;

import com.menu.common.Result;
import com.menu.dto.LoginDTO;
import com.menu.entity.User;
import com.menu.vo.LoginVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("用户服务测试")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("管理员登录成功")
    void testAdminLogin() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("admin123");

        Result<LoginVO> result = userService.login(dto);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertNotNull(result.getData().getToken());
        assertEquals("admin", result.getData().getUsername());
        assertEquals(1, result.getData().getRole());
    }

    @Test
    @DisplayName("女友登录成功")
    void testGirlfriendLogin() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("girlfriend");
        dto.setPassword("love123");

        Result<LoginVO> result = userService.login(dto);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().getRole());
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void testLoginUserNotFound() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("notexist");
        dto.setPassword("password");

        Result<LoginVO> result = userService.login(dto);

        assertEquals(500, result.getCode());
        assertEquals("用户不存在", result.getMessage());
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void testLoginWrongPassword() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("wrongpassword");

        Result<LoginVO> result = userService.login(dto);

        assertEquals(500, result.getCode());
        assertEquals("密码错误", result.getMessage());
    }

    @Test
    @DisplayName("根据ID获取用户")
    void testGetById() {
        User user = userService.getById(1L);

        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    @DisplayName("获取不存在的用户")
    void testGetByIdNotFound() {
        User user = userService.getById(999L);

        assertNull(user);
    }
}
