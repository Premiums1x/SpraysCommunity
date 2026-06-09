package com.lancer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lancer.annotation.RequireAdmin;
import com.lancer.common.result.Result;
import com.lancer.dto.AnimalQueryRequest;
import com.lancer.entity.Animal;
import com.lancer.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    /**
     * 分页查询动物列表（公开接口）
     */
    @GetMapping
    public Result<IPage<Animal>> getAnimalList(AnimalQueryRequest request) {
        IPage<Animal> page = animalService.getAnimalList(request);
        return Result.success(page);
    }

    /**
     * 获取动物详情（公开接口）
     */
    @GetMapping("/{id}")
    public Result<Animal> getAnimalById(@PathVariable Long id) {
        Animal animal = animalService.getAnimalById(id);
        return Result.success(animal);
    }

    /**
     * 新增动物档案（管理员）
     */
    @PostMapping
    @RequireAdmin
    public Result<Void> addAnimal(
            @RequestParam("name") String name,
            @RequestParam("type") Integer type,
            @RequestParam("area") String area,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        Animal animal = new Animal();
        animal.setName(name);
        animal.setType(type);
        animal.setArea(area);
        animal.setDescription(description);

        animalService.addAnimal(animal, file);
        return Result.success("动物档案添加成功", null);
    }

    /**
     * 更新动物信息（管理员）
     */
    @PutMapping("/{id}")
    @RequireAdmin
    public Result<Void> updateAnimal(@PathVariable Long id, @RequestBody Animal animal) {
        animalService.updateAnimal(id, animal);
        return Result.success("动物档案更新成功", null);
    }

    /**
     * 删除动物档案（管理员）
     */
    @DeleteMapping("/{id}")
    @RequireAdmin
    public Result<Void> deleteAnimal(@PathVariable Long id) {
        animalService.deleteAnimal(id);
        return Result.success("动物档案删除成功", null);
    }
}
