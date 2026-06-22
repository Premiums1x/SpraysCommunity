package com.lancer.dto;

public record AnimalQueryRequest(
        String name,
        Integer type,
        Integer page,
        Integer size
) {
    public AnimalQueryRequest {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
    }
}
