package io.github.sebkaminski16.carrentaladmin.mapper;

import io.github.sebkaminski16.carrentaladmin.dto.CategoryDtos;
import io.github.sebkaminski16.carrentaladmin.entity.Category;

public class CategoryMapper {

    public static CategoryDtos.CategoryDto toDto(Category c) {
        return new CategoryDtos.CategoryDto(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getDailyDiscountPercent(),
                c.getWeeklyDiscountPercent()
        );
    }
}
