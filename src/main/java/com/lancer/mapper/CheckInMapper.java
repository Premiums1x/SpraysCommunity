package com.lancer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lancer.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {

    /**
     * 根据动物ID分页查询打卡记录（关联用户信息）
     */
    @Select("SELECT ci.id, ci.user_id, ci.animal_id, ci.content, ci.create_time, " +
            "u.nickname AS user_nickname, u.avatar AS user_avatar " +
            "FROM check_in ci LEFT JOIN user u ON ci.user_id = u.id " +
            "WHERE ci.animal_id = #{animalId} " +
            "ORDER BY ci.create_time DESC")
    IPage<Map<String, Object>> selectCheckInWithUser(Page<?> page, @Param("animalId") Long animalId);
}
