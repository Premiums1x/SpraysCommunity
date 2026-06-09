package com.lancer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lancer.common.exception.BusinessException;
import com.lancer.dto.AnimalQueryRequest;
import com.lancer.entity.Animal;
import com.lancer.mapper.AnimalMapper;
import com.lancer.service.AnimalService;
import com.lancer.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AnimalServiceImpl implements AnimalService {

    @Autowired
    private AnimalMapper animalMapper;

    @Autowired
    private FileService fileService;

    @Override
    public IPage<Animal> getAnimalList(AnimalQueryRequest request) {
        Page<Animal> page = new Page<>(request.getPage(), request.getSize());
        LambdaQueryWrapper<Animal> wrapper = new LambdaQueryWrapper<>();

        // 模糊查询名字
        if (StringUtils.hasText(request.getName())) {
            wrapper.like(Animal::getName, request.getName());
        }
        // 类型筛选
        if (request.getType() != null) {
            wrapper.eq(Animal::getType, request.getType());
        }
        // 按创建时间倒序
        wrapper.orderByDesc(Animal::getCreateTime);

        return animalMapper.selectPage(page, wrapper);
    }

    @Override
    public Animal getAnimalById(Long id) {
        Animal animal = animalMapper.selectById(id);
        if (animal == null) {
            throw new BusinessException(404, "动物档案不存在");
        }
        return animal;
    }

    @Override
    public void addAnimal(Animal animal, MultipartFile file) {
        // 上传封面图片
        if (file != null && !file.isEmpty()) {
            String fileName = fileService.upload(file);
            animal.setCoverImage(fileName);
        }
        animalMapper.insert(animal);
    }

    @Override
    public void updateAnimal(Long id, Animal animal) {
        Animal existing = animalMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "动物档案不存在");
        }
        animal.setId(id);
        animalMapper.updateById(animal);
    }

    @Override
    public void deleteAnimal(Long id) {
        Animal existing = animalMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "动物档案不存在");
        }
        animalMapper.deleteById(id);
    }
}
