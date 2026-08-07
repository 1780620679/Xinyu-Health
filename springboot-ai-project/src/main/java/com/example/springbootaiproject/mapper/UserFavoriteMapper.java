package com.example.springbootaiproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootaiproject.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
}
