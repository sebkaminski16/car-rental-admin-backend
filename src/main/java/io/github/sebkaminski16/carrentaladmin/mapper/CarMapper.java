package io.github.sebkaminski16.carrentaladmin.mapper;

import io.github.sebkaminski16.carrentaladmin.dto.CarDtos;
import io.github.sebkaminski16.carrentaladmin.entity.Car;

public class CarMapper {

    public static CarDtos.CarDto toDto(Car c) {
        return new CarDtos.CarDto(
                c.getId(),
                c.getVin(),
                c.getLicensePlate(),
                c.getProductionYear(),
                c.getColor(),
                c.getStatus(),
                c.getModel().getId(),
                c.getModel().getName(),
                c.getModel().getBrand().getId(),
                c.getModel().getBrand().getName(),
                c.getCategory().getId(),
                c.getCategory().getName(),
                c.getImageUrl(),
                c.getHourlyRate(),
                c.getDailyRate(),
                c.getWeeklyRate(),
                c.getMileageKm()
        );
    }

    public static String toLabel(Car c) {
        return c.getLicensePlate() + " (" + c.getModel().getBrand().getName() + " " + c.getModel().getName() + ")";
    }
}
