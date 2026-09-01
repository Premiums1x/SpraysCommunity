package com.lancer.service;

import com.lancer.dto.AnimalCreateRequest;
import com.lancer.dto.AnimalQueryRequest;
import com.lancer.dto.AnimalResponse;
import com.lancer.dto.AnimalUpdateRequest;
import com.lancer.dto.PageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AnimalService {
    PageResponse<AnimalResponse> getAnimalList(AnimalQueryRequest request);
    AnimalResponse getAnimalById(Long id);
    void addAnimal(AnimalCreateRequest request, MultipartFile file);
    void updateAnimal(Long id, AnimalUpdateRequest request, MultipartFile file);
    void deleteAnimal(Long id);
}
