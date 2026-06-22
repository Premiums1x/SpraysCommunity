package com.lancer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lancer.common.exception.BusinessException;
import com.lancer.dto.AnimalQueryRequest;
import com.lancer.entity.Animal;
import com.lancer.mapper.AnimalMapper;
import com.lancer.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceImplTest {

    @Mock
    private AnimalMapper animalMapper;

    @Mock
    private FileService fileService;

    @InjectMocks
    private AnimalServiceImpl animalService;

    private Animal animal;

    @BeforeEach
    void setUp() {
        animal = new Animal();
        animal.setId(1L);
        animal.setName("小花");
        animal.setType(1);
        animal.setArea("图书馆草坪");
        animal.setDescription("一只橘猫");
        animal.setCoverImage("abc123.jpg");
        animal.setCreateTime(LocalDateTime.now());
        animal.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void getAnimalList_shouldReturnPage() {
        AnimalQueryRequest request = new AnimalQueryRequest(null, null, 1, 10);
        Page<Animal> page = new Page<>(1, 10);
        page.setRecords(List.of(animal));
        page.setTotal(1);
        when(animalMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        IPage<Animal> result = animalService.getAnimalList(request);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void getAnimalList_shouldFilterByName() {
        AnimalQueryRequest request = new AnimalQueryRequest("小花", null, 1, 10);
        when(animalMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(new Page<>());

        animalService.getAnimalList(request);

        verify(animalMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void getAnimalList_shouldFilterByType() {
        AnimalQueryRequest request = new AnimalQueryRequest(null, 1, 1, 10);
        when(animalMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(new Page<>());

        animalService.getAnimalList(request);

        verify(animalMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void getAnimalById_shouldReturnAnimal() {
        when(animalMapper.selectById(1L)).thenReturn(animal);

        Animal result = animalService.getAnimalById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("小花", result.getName());
    }

    @Test
    void getAnimalById_shouldThrowWhenNotFound() {
        when(animalMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> animalService.getAnimalById(999L));
        assertEquals(404, ex.getCode());
        assertEquals("动物档案不存在", ex.getMessage());
    }

    @Test
    void addAnimal_shouldUploadFileAndInsert() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(fileService.upload(file)).thenReturn("newfile.jpg");
        when(animalMapper.insert(any(Animal.class))).thenReturn(1);

        animalService.addAnimal(animal, file);

        ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
        verify(animalMapper).insert(captor.capture());
        assertEquals("newfile.jpg", captor.getValue().getCoverImage());
    }

    @Test
    void addAnimal_shouldInsertWithoutFile() {
        when(animalMapper.insert(any(Animal.class))).thenReturn(1);

        animalService.addAnimal(animal, null);

        verify(fileService, never()).upload(any());
        verify(animalMapper).insert(animal);
    }

    @Test
    void updateAnimal_shouldUpdateExisting() {
        when(animalMapper.selectById(1L)).thenReturn(animal);
        when(animalMapper.updateById(any(Animal.class))).thenReturn(1);

        animalService.updateAnimal(1L, "新名字", 2, "新区域", "新描述", null);

        ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
        verify(animalMapper).updateById(captor.capture());
        assertEquals(1L, captor.getValue().getId());
        assertEquals("新名字", captor.getValue().getName());
        assertEquals("新描述", captor.getValue().getDescription());
    }

    @Test
    void updateAnimal_shouldUploadNewCoverFile() {
        when(animalMapper.selectById(1L)).thenReturn(animal);
        when(animalMapper.updateById(any(Animal.class))).thenReturn(1);
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(fileService.upload(file)).thenReturn("newcover.jpg");

        animalService.updateAnimal(1L, "小花", 1, "图书馆草坪", null, file);

        ArgumentCaptor<Animal> captor = ArgumentCaptor.forClass(Animal.class);
        verify(animalMapper).updateById(captor.capture());
        assertEquals("newcover.jpg", captor.getValue().getCoverImage());
    }

    @Test
    void updateAnimal_shouldThrowWhenNotFound() {
        when(animalMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> animalService.updateAnimal(999L, "name", 1, "area", null, null));
        assertEquals(404, ex.getCode());
    }

    @Test
    void deleteAnimal_shouldDeleteExisting() {
        when(animalMapper.selectById(1L)).thenReturn(animal);
        when(animalMapper.deleteById(1L)).thenReturn(1);

        animalService.deleteAnimal(1L);

        verify(animalMapper).deleteById(1L);
    }

    @Test
    void deleteAnimal_shouldThrowWhenNotFound() {
        when(animalMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> animalService.deleteAnimal(999L));
        assertEquals(404, ex.getCode());
    }
}
