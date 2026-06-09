package com.lancer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lancer.common.result.Result;
import com.lancer.dto.CheckInRequest;
import com.lancer.dto.CheckInVO;
import com.lancer.service.CheckInService;
import com.lancer.utils.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    /**
     * 发布打卡（需登录）
     */
    @PostMapping("/api/checkins")
    public Result<Void> createCheckIn(@Valid @RequestBody CheckInRequest request) {
        Long userId = UserContext.getCurrentUserId();
        checkInService.createCheckIn(request, userId);
        return Result.success("打卡成功", null);
    }

    /**
     * 查询某动物的打卡时间轴（公开接口）
     */
    @GetMapping("/api/animals/{animalId}/checkins")
    public Result<IPage<CheckInVO>> getCheckInsByAnimal(
            @PathVariable Long animalId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<CheckInVO> result = checkInService.getCheckInsByAnimalId(animalId, page, size);
        return Result.success(result);
    }

    /**
     * 查询我的打卡记录（需登录）
     */
    @GetMapping("/api/checkins/my")
    public Result<IPage<CheckInVO>> getMyCheckIns(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContext.getCurrentUserId();
        IPage<CheckInVO> result = checkInService.getMyCheckIns(userId, page, size);
        return Result.success(result);
    }
}
