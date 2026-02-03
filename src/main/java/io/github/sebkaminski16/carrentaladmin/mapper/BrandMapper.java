package io.github.sebkaminski16.carrentaladmin.mapper;

import io.github.sebkaminski16.carrentaladmin.dto.BrandDtos;
import io.github.sebkaminski16.carrentaladmin.entity.Brand;

public class BrandMapper {

    public static BrandDtos.BrandDto toDto(Brand b) {
        return new BrandDtos.BrandDto(b.getId(), b.getName());
    }
}
