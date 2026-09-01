package com.lancer.controller;

import com.lancer.common.result.Result;
import com.lancer.dto.CheckInRequest;
import com.lancer.dto.CheckInVO;
import com.lancer.dto.PageResponse;
import com.lancer.service.CheckInService;
import com.lancer.utils.UserContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

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
    public Result<PageResponse<CheckInVO>> getCheckInsByAnimal(
            @PathVariable @Positive Long animalId,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer size) {
        PageResponse<CheckInVO> result = checkInService.getCheckInsByAnimalId(animalId, page, size);
        return Result.success(result);
    }

    /**
     * 查询我的打卡记录（需登录）
     */
    @GetMapping("/api/checkins/my")
    public Result<PageResponse<CheckInVO>> getMyCheckIns(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer size) {
        Long userId = UserContext.getCurrentUserId();
        PageResponse<CheckInVO> result = checkInService.getMyCheckIns(userId, page, size);
        return Result.success(result);
    }
}
