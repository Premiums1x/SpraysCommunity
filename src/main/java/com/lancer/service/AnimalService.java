package com.lancer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lancer.dto.AnimalQueryRequest;
import com.lancer.entity.Animal;
import org.springframework.web.multipart.MultipartFile;

public interface AnimalService {
    IPage<Animal> getAnimalList(AnimalQueryRequest request);
    Animal getAnimalById(Long id);
    void addAnimal(Animal animal, MultipartFile file);
    void updateAnimal(Long id, String name, Integer type, String area, String description, MultipartFile file);
    void deleteAnimal(Long id);
}
