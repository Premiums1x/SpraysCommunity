package com.lancer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lancer.common.exception.BusinessException;
import com.lancer.dto.AnimalCreateRequest;
import com.lancer.dto.AnimalQueryRequest;
import com.lancer.dto.AnimalResponse;
import com.lancer.dto.AnimalUpdateRequest;
import com.lancer.dto.PageResponse;
import com.lancer.entity.Animal;
import com.lancer.mapper.AnimalMapper;
import com.lancer.service.AnimalService;
import com.lancer.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
public class AnimalServiceImpl implements AnimalService {

    private final AnimalMapper animalMapper;
    private final FileService fileService;

    public AnimalServiceImpl(AnimalMapper animalMapper, FileService fileService) {
        this.animalMapper = animalMapper;
        this.fileService = fileService;
    }

    @Override
    public PageResponse<AnimalResponse> getAnimalList(AnimalQueryRequest request) {
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

        Page<AnimalResponse> responsePage = new Page<>(page.getCurrent(), page.getSize());
        var result = animalMapper.selectPage(page, wrapper);
        responsePage.setTotal(result.getTotal());
        responsePage.setRecords(result.getRecords().stream().map(AnimalResponse::from).toList());
        return PageResponse.from(responsePage);
    }

    @Override
    public AnimalResponse getAnimalById(Long id) {
        Animal animal = animalMapper.selectById(id);
        if (animal == null) {
            throw new BusinessException(404, "动物档案不存在");
        }
        return AnimalResponse.from(animal);
    }

    @Override
    @Transactional
    public void addAnimal(AnimalCreateRequest request, MultipartFile file) {
        Animal animal = new Animal();
        copyEditableFields(request, animal);

        String uploadedFile = null;
        if (file != null && !file.isEmpty()) {
            uploadedFile = fileService.upload(file);
            animal.setCoverImage(uploadedFile);
            deleteOnRollback(uploadedFile);
        }
        try {
            if (animalMapper.insert(animal) != 1) {
                throw new BusinessException(500, "动物档案保存失败");
            }
        } catch (RuntimeException exception) {
            fileService.delete(uploadedFile);
            throw exception;
        }
    }

    @Override
    @Transactional
    public void updateAnimal(Long id, AnimalUpdateRequest request, MultipartFile file) {
        Animal existing = animalMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "动物档案不存在");
        }

        String uploadedFile = null;
        if (file != null && !file.isEmpty()) {
            uploadedFile = fileService.upload(file);
            deleteOnRollback(uploadedFile);
        }

        Animal updated = new Animal();
        updated.setId(id);
        copyEditableFields(request, updated);
        updated.setCoverImage(uploadedFile == null ? existing.getCoverImage() : uploadedFile);

        try {
            if (animalMapper.updateById(updated) != 1) {
                throw new BusinessException(409, "动物档案已发生变化，请刷新后重试");
            }
        } catch (RuntimeException exception) {
            fileService.delete(uploadedFile);
            throw exception;
        }

        if (uploadedFile != null && !Objects.equals(existing.getCoverImage(), uploadedFile)) {
            deleteAfterCommit(existing.getCoverImage());
        }
    }

    @Override
    public void deleteAnimal(Long id) {
        Animal existing = animalMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(404, "动物档案不存在");
        }
        if (animalMapper.deleteById(id) != 1) {
            throw new BusinessException(409, "动物档案已发生变化，请刷新后重试");
        }
        deleteAfterCommit(existing.getCoverImage());
    }

    private void copyEditableFields(AnimalCreateRequest request, Animal target) {
        target.setName(request.getName().trim());
        target.setType(request.getType());
        target.setArea(request.getArea().trim());
        target.setDescription(clean(request.getDescription()));
        target.setAliases(clean(request.getAliases()));
        target.setGender(request.getGender() == null ? 0 : request.getGender());
        target.setPersonalityTags(clean(request.getPersonalityTags()));
        target.setSterilized(Boolean.TRUE.equals(request.getSterilized()));
        target.setHealthStatus(StringUtils.hasText(request.getHealthStatus()) ? request.getHealthStatus() : "HEALTHY");
        target.setFirstSeenDate(request.getFirstSeenDate());
        target.setActiveTime(clean(request.getActiveTime()));
    }

    private String clean(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private void deleteAfterCommit(String fileName) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            fileService.delete(fileName);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                fileService.delete(fileName);
            }
        });
    }

    private void deleteOnRollback(String fileName) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != TransactionSynchronization.STATUS_COMMITTED) {
                    fileService.delete(fileName);
                }
            }
        });
    }
}
