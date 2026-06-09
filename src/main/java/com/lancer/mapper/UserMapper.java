package com.lancer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lancer.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
