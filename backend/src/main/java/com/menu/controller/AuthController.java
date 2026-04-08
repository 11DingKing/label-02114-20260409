package com.menu.controller;

import com.menu.common.Result;
import com.menu.dto.LoginDTO;
import com.menu.entity.User;
import com.menu.service.UserService;
import com.menu.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理", description = "用户登录、登出、获取用户信息")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @Operation(summary = "用户登录", description = "通过用户名密码登录，返回JWT令牌")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return userService.login(dto);
    }
    
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
    
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<User> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        user.setPassword(null);
        return Result.success(user);
    }
}
