package com.lancer.controller;

import com.lancer.annotation.RequireAdmin;
import com.lancer.common.result.Result;
import com.lancer.dto.AnimalCreateRequest;
import com.lancer.dto.AnimalQueryRequest;
import com.lancer.dto.AnimalResponse;
import com.lancer.dto.AnimalUpdateRequest;
import com.lancer.dto.PageResponse;
import com.lancer.service.AnimalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/animals")
@Validated
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    /**
     * 分页查询动物列表（公开接口）
     */
    @GetMapping
    public Result<PageResponse<AnimalResponse>> getAnimalList(@Valid AnimalQueryRequest request) {
        PageResponse<AnimalResponse> page = animalService.getAnimalList(request);
        return Result.success(page);
    }

    /**
     * 获取动物详情（公开接口）
     */
    @GetMapping("/{id}")
    public Result<AnimalResponse> getAnimalById(@PathVariable @Positive Long id) {
        AnimalResponse animal = animalService.getAnimalById(id);
        return Result.success(animal);
    }

    /**
     * 新增动物档案（管理员）
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequireAdmin
    public Result<Void> addAnimal(
            @Valid @ModelAttribute AnimalCreateRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        animalService.addAnimal(request, file);
        return Result.success("动物档案添加成功", null);
    }

    /**
     * 更新动物信息（管理员）
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequireAdmin
    public Result<Void> updateAnimal(
            @PathVariable @Positive Long id,
            @Valid @ModelAttribute AnimalUpdateRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        animalService.updateAnimal(id, request, file);
        return Result.success("动物档案更新成功", null);
    }

    /**
     * 删除动物档案（管理员）
     */
    @DeleteMapping("/{id}")
    @RequireAdmin
    public Result<Void> deleteAnimal(@PathVariable @Positive Long id) {
        animalService.deleteAnimal(id);
        return Result.success("动物档案删除成功", null);
    }
}
