package com.example.springbootaiproject.DTO.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class UserLoginCommandDTO {
    //通过校验插件（spring-boot-starter-validation）对注解进行校验
    @NotBlank(message = "用户名或邮箱不能为空")
    @Size(max = 20, message = "用户名或邮箱长度必须在20以内")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;
}
