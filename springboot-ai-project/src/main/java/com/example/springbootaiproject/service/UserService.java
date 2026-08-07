package com.example.springbootaiproject.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springbootaiproject.DTO.command.UserLoginCommandDTO;
import com.example.springbootaiproject.DTO.command.UserRegisterCommandDTO;
import com.example.springbootaiproject.DTO.response.UserLoginResponseDTO;
import com.example.springbootaiproject.entity.User;
import com.example.springbootaiproject.enumClass.UserType;
import com.example.springbootaiproject.exception.BusinessException;
import com.example.springbootaiproject.mapper.UserMapper;
import com.example.springbootaiproject.service.convert.UserConvert;
import com.example.springbootaiproject.utils.JwtTokenUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class UserService {

    //注入BCryptPasswordEncoder ，用于加密密码,同时还能进行明文和加密后的密码的对比
    //BCryptPasswordEncoder 是Spring Security提供的一个密码加密器，使用BCrypt算法对密码进行加密
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Resource
    private UserMapper userMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    //用户登录
    public UserLoginResponseDTO login(UserLoginCommandDTO commandDTO) {
        //构建查询逻辑
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, commandDTO.getUsername())
                .or()
                .eq(User::getEmail, commandDTO.getUsername());//用户可以用"用户名"或"邮箱"来登录。前端输入框的值 commandDTO.getUsername() 既可能是用户名，也可能是邮箱，所以用 OR 同时查两个字段，只要匹配其中一个就说明用户存在。
        //调用MP API
        User user = userMapper.selectOne(queryWrapper);
        //判断用户是否存在
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        //判断密码是否正确
        if (!user.getPassword().equals(commandDTO.getPassword())) {
            if (!bCryptPasswordEncoder.matches(commandDTO.getPassword(), user.getPassword())) {
                throw new BusinessException("密码错误");
            }
        }
        //判断用户状态（是否封号） , 用数据库查到的user信息来调用user实体类的isActive方法判断当前用户是否为正常状态
        if (!user.isActive()) {
            throw new BusinessException("用户已被封号,请联系管理员解封");
        }
        //生成JWT token
        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
        //登录成功
        //除了原先的对实体类通过Result包装（new出来一个Result对象，然后set方法设置数据和状态，然后返回），还可以使用实体类的builder模式来创建对象
        UserLoginResponseDTO.UserDetailResponseDTO userInfo = UserConvert.entityToDetailResponse(user); //构造的内曾userInfo
        return UserConvert.entityToLoginResponse(token, userInfo); //构造的登录响应对象(外层)
    }

    //用户注册
    public UserLoginResponseDTO.UserDetailResponseDTO register(UserRegisterCommandDTO commandDTO) {
        //hutool 打印commandDTO对象，将commandDTO对象转换为JSON字符串，因为commandDTO对象是一个对象，不能直接打印，只能转换为JSON字符串
//        System.out.println(JSONUtil.parseObj(commandDTO));

        //验证两次密码是否一致
        if (!commandDTO.getPassword().equals(commandDTO.getConfirmPassword())) {
            throw new BusinessException("两次密码不一致");
        }
        //判断用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, commandDTO.getUsername());//根据用户名查询  翻译成sql就是WHERE username = '前端传的值'
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        //判断邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, commandDTO.getEmail());//根据邮箱查询  翻译成sql就是WHERE email = '前端传的值'
        if (userMapper.selectCount(emailWrapper) > 0) {
            throw new BusinessException("邮箱已存在");
        }
        //判断用户类型是否正确
        if (!UserType.isValidCode(commandDTO.getUserType())) {
            throw new BusinessException("用户类型错误");
        }
        //创建用户对象
        String password = commandDTO.getPassword().trim();
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        //同理convert类的entityToRegisterResponse方法 来builder构建用户对象的注册响应对象
        User user = UserConvert.entityToRegisterResponse(commandDTO, encodedPassword);//往数据库插入用户对象需要用到user实体类，下面return返回仍然是UserLoginResponseDTO对象

        //调用MP API 插入用户对象
        userMapper.insert(user);
        //返回注册响应对象
        return UserConvert.entityToDetailResponse(user);
    }

    //根据用户ID查询用户信息
    public UserLoginResponseDTO.UserDetailResponseDTO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return UserConvert.entityToDetailResponse(user);
    }
}
