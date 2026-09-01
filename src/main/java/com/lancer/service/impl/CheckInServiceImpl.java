package com.lancer.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lancer.common.exception.BusinessException;
import com.lancer.dto.CheckInRequest;
import com.lancer.dto.CheckInVO;
import com.lancer.dto.PageResponse;
import com.lancer.entity.Animal;
import com.lancer.entity.CheckIn;
import com.lancer.mapper.AnimalMapper;
import com.lancer.mapper.CheckInMapper;
import com.lancer.service.CheckInService;
import org.springframework.stereotype.Service;

@Service
public class CheckInServiceImpl implements CheckInService {

    private final CheckInMapper checkInMapper;
    private final AnimalMapper animalMapper;

    public CheckInServiceImpl(CheckInMapper checkInMapper, AnimalMapper animalMapper) {
        this.checkInMapper = checkInMapper;
        this.animalMapper = animalMapper;
    }

    @Override
    public void createCheckIn(CheckInRequest request, Long userId) {
        // 验证动物是否存在
        Animal animal = animalMapper.selectById(request.getAnimalId());
        if (animal == null) {
            throw new BusinessException(404, "动物档案不存在");
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setUserId(userId);
        checkIn.setAnimalId(request.getAnimalId());
        checkIn.setContent(request.getContent());
        checkIn.setAnonymous(Boolean.TRUE.equals(request.getAnonymous()));
        checkInMapper.insert(checkIn);
    }

    @Override
    public PageResponse<CheckInVO> getCheckInsByAnimalId(Long animalId, Integer page, Integer size) {
        if (animalMapper.selectById(animalId) == null) {
            throw new BusinessException(404, "动物档案不存在");
        }
        Page<CheckInVO> pageParam = new Page<>(page, size);
        return PageResponse.from(checkInMapper.selectCheckInWithUser(pageParam, animalId));
    }

    @Override
    public PageResponse<CheckInVO> getMyCheckIns(Long userId, Integer page, Integer size) {
        Page<CheckInVO> pageParam = new Page<>(page, size);
        return PageResponse.from(checkInMapper.selectMyCheckIns(pageParam, userId));
    }
}
