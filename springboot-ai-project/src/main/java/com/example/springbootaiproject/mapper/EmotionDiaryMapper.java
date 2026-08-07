package com.example.springbootaiproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootaiproject.entity.EmotionDiary;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmotionDiaryMapper extends BaseMapper<EmotionDiary> {
}
