package com.lancer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lancer.dto.CheckInRequest;
import com.lancer.dto.CheckInVO;

public interface CheckInService {
    void createCheckIn(CheckInRequest request, Long userId);
    IPage<CheckInVO> getCheckInsByAnimalId(Long animalId, Integer page, Integer size);
    IPage<CheckInVO> getMyCheckIns(Long userId, Integer page, Integer size);
}
