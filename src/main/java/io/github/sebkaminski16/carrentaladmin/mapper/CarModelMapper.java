package io.github.sebkaminski16.carrentaladmin.mapper;

import io.github.sebkaminski16.carrentaladmin.dto.CarModelDtos;
import io.github.sebkaminski16.carrentaladmin.entity.CarModel;

public class CarModelMapper {

    public static CarModelDtos.CarModelDto toDto(CarModel m) {
        return new CarModelDtos.CarModelDto(
                m.getId(),
                m.getName(),
                m.getBrand().getId(),
                m.getBrand().getName()
        );
    }
}
