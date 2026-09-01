package com.lancer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lancer.dto.CheckInVO;
import com.lancer.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {

    /**
     * 根据动物ID分页查询打卡记录（关联用户信息）
     */
    @Select("SELECT ci.id, ci.user_id, ci.animal_id, ci.content, ci.is_anonymous AS anonymous, ci.create_time, " +
            "CASE WHEN ci.is_anonymous = 1 THEN NULL ELSE u.username END AS username, " +
            "CASE WHEN ci.is_anonymous = 1 THEN NULL ELSE u.nickname END AS user_nickname, " +
            "CASE WHEN ci.is_anonymous = 1 THEN '匿名用户' " +
            "ELSE COALESCE(NULLIF(u.nickname, ''), u.username, '匿名用户') END AS user_display_name, " +
            "CASE WHEN ci.is_anonymous = 1 THEN NULL ELSE u.avatar END AS user_avatar " +
            "FROM check_in ci LEFT JOIN user u ON ci.user_id = u.id " +
            "WHERE ci.animal_id = #{animalId} " +
            "ORDER BY ci.create_time DESC")
    IPage<CheckInVO> selectCheckInWithUser(Page<CheckInVO> page, @Param("animalId") Long animalId);

    @Select("SELECT ci.id, ci.user_id, ci.animal_id, ci.content, ci.is_anonymous AS anonymous, " +
            "ci.create_time, a.name AS animal_name " +
            "FROM check_in ci LEFT JOIN animal a ON ci.animal_id = a.id " +
            "WHERE ci.user_id = #{userId} ORDER BY ci.create_time DESC")
    IPage<CheckInVO> selectMyCheckIns(Page<CheckInVO> page, @Param("userId") Long userId);
}
