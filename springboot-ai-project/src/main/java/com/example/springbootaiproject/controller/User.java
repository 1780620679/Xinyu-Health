package com.example.springbootaiproject.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springbootaiproject.DTO.command.UserLoginCommandDTO;
import com.example.springbootaiproject.DTO.command.UserRegisterCommandDTO;
import com.example.springbootaiproject.DTO.response.UserLoginResponseDTO;
import com.example.springbootaiproject.common.Result;
import com.example.springbootaiproject.common.ResultCode;
import com.example.springbootaiproject.service.UserService;
import com.example.springbootaiproject.utils.JwtTokenUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/user")
public class User {

    @Resource
    private UserService userService;

    //用户登录
    @RequestMapping("/login")
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO commandDTO) { // @Valid 校验插件注解，对 userLoginCommandDTO 进行校验

        //调用服务层的登录方法
        UserLoginResponseDTO result = userService.login(commandDTO);
        return Result.ok(result);
    }
    //用户注册
    @RequestMapping("/add")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> add(@Valid @RequestBody UserRegisterCommandDTO commandDTO) {
        //调用服务层的注册方法
        UserLoginResponseDTO.UserDetailResponseDTO result = userService.register(commandDTO);
        return Result.ok(result);
    }

    //获取当前用户信息
    @RequestMapping("/current")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> getCurrentUser() {
        // 如何从token中解析出用户的id
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        // 调用service层获取用户详情
        UserLoginResponseDTO.UserDetailResponseDTO result = userService.getUserById(userId);
        return Result.ok(result);
    }

    //退出登录
    @RequestMapping("/logout")
    public Result<Void> logout() {
        return Result.ok();
    }
}
