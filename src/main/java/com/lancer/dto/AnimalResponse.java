package com.lancer.dto;

import com.lancer.entity.Animal;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AnimalResponse {
    private Long id;
    private String name;
    private Integer type;
    private String area;
    private String coverImage;
    private String description;
    private String aliases;
    private Integer gender;
    private String personalityTags;
    private Boolean sterilized;
    private String healthStatus;
    private LocalDate firstSeenDate;
    private String activeTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static AnimalResponse from(Animal animal) {
        AnimalResponse response = new AnimalResponse();
        response.setId(animal.getId());
        response.setName(animal.getName());
        response.setType(animal.getType());
        response.setArea(animal.getArea());
        response.setCoverImage(animal.getCoverImage());
        response.setDescription(animal.getDescription());
        response.setAliases(animal.getAliases());
        response.setGender(animal.getGender());
        response.setPersonalityTags(animal.getPersonalityTags());
        response.setSterilized(animal.getSterilized());
        response.setHealthStatus(animal.getHealthStatus());
        response.setFirstSeenDate(animal.getFirstSeenDate());
        response.setActiveTime(animal.getActiveTime());
        response.setCreateTime(animal.getCreateTime());
        response.setUpdateTime(animal.getUpdateTime());
        return response;
    }
}
