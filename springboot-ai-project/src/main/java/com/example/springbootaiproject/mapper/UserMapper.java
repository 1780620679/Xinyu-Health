package com.example.springbootaiproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootaiproject.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
