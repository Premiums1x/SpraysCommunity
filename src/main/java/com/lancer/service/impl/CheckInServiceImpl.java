package com.lancer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lancer.common.exception.BusinessException;
import com.lancer.dto.CheckInRequest;
import com.lancer.dto.CheckInVO;
import com.lancer.entity.Animal;
import com.lancer.entity.CheckIn;
import com.lancer.mapper.AnimalMapper;
import com.lancer.mapper.CheckInMapper;
import com.lancer.mapper.UserMapper;
import com.lancer.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CheckInServiceImpl implements CheckInService {

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private AnimalMapper animalMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void createCheckIn(CheckInRequest request, Long userId) {
        // 验证动物是否存在
        Animal animal = animalMapper.selectById(request.animalId());
        if (animal == null) {
            throw new BusinessException(404, "动物档案不存在");
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(userId);
        checkIn.setAnimalId(request.animalId());
        checkIn.setContent(request.content());
        checkIn.setAnonymous(Boolean.TRUE.equals(request.anonymous()));
        checkInMapper.insert(checkIn);
    }

    @Override
    public IPage<CheckInVO> getCheckInsByAnimalId(Long animalId, Integer page, Integer size) {
        // 使用自定义 SQL 关联查询
        Page<Map<String, Object>> pageParam = new Page<>(page, size);
        IPage<Map<String, Object>> mapPage = checkInMapper.selectCheckInWithUser(pageParam, animalId);

        // 转换为 CheckInVO
        Page<CheckInVO> voPage = new Page<>(page, size, mapPage.getTotal());
        List<CheckInVO> voList = mapPage.getRecords().stream().map(map -> {
            CheckInVO vo = new CheckInVO();
            vo.setId(((Number) map.get("id")).longValue());
            vo.setUserId(((Number) map.get("user_id")).longValue());
            vo.setAnimalId(((Number) map.get("animal_id")).longValue());
            vo.setContent((String) map.get("content"));
            Boolean anonymous = toBoolean(map.get("is_anonymous"));
            vo.setAnonymous(anonymous);
            // 处理时间类型
            Object createTimeObj = map.get("create_time");
            if (createTimeObj instanceof java.time.LocalDateTime) {
                vo.setCreateTime((java.time.LocalDateTime) createTimeObj);
            }
            String username = (String) map.get("username");
            String userNickname = (String) map.get("user_nickname");
            vo.setUsername(Boolean.TRUE.equals(anonymous) ? null : username);
            vo.setUserNickname(Boolean.TRUE.equals(anonymous) ? null : userNickname);
            vo.setUserDisplayName(Boolean.TRUE.equals(anonymous) ? "匿名用户" : getDisplayName(userNickname, username));
            vo.setUserAvatar(Boolean.TRUE.equals(anonymous) ? null : (String) map.get("user_avatar"));
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public IPage<CheckInVO> getMyCheckIns(Long userId, Integer page, Integer size) {
        // 查询我的打卡记录
        Page<CheckIn> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getUserId, userId)
               .orderByDesc(CheckIn::getCreateTime);
        IPage<CheckIn> checkInPage = checkInMapper.selectPage(pageParam, wrapper);

        // 批量查询关联的动物名称（避免 N+1 查询）
        List<Long> animalIds = checkInPage.getRecords().stream()
                .map(CheckIn::getAnimalId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> animalNameMap = new java.util.HashMap<>();
        if (!animalIds.isEmpty()) {
            List<Animal> animals = animalMapper.selectBatchIds(animalIds);
            animals.forEach(a -> animalNameMap.put(a.getId(), a.getName()));
        }

        // 转换为 VO
        Page<CheckInVO> voPage = new Page<>(page, size, checkInPage.getTotal());
        List<CheckInVO> voList = checkInPage.getRecords().stream().map(checkIn -> {
            CheckInVO vo = new CheckInVO();
            vo.setId(checkIn.getId());
            vo.setUserId(checkIn.getUserId());
            vo.setAnimalId(checkIn.getAnimalId());
            vo.setContent(checkIn.getContent());
            vo.setAnonymous(checkIn.getAnonymous());
            vo.setCreateTime(checkIn.getCreateTime());
            vo.setAnimalName(animalNameMap.getOrDefault(checkIn.getAnimalId(), "未知动物"));
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    private Boolean toBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return false;
    }

    private String getDisplayName(String nickname, String username) {
        if (nickname != null && !nickname.isBlank()) {
            return nickname;
        }
        if (username != null && !username.isBlank()) {
            return username;
        }
        return "匿名用户";
    }
}
