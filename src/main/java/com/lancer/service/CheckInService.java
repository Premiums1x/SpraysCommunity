package com.lancer.service;

import com.lancer.dto.CheckInRequest;
import com.lancer.dto.CheckInVO;
import com.lancer.dto.PageResponse;

public interface CheckInService {
    void createCheckIn(CheckInRequest request, Long userId);
    PageResponse<CheckInVO> getCheckInsByAnimalId(Long animalId, Integer page, Integer size);
    PageResponse<CheckInVO> getMyCheckIns(Long userId, Integer page, Integer size);
}
