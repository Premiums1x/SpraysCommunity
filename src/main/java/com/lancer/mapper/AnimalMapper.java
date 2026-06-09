package com.lancer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lancer.entity.Animal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnimalMapper extends BaseMapper<Animal> {
}
