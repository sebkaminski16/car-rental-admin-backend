package io.github.sebkaminski16.carrentaladmin.mapper;

import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.entity.Rental;

public class RentalMapper {

    public static RentalDtos.RentalDto toDto(Rental r) {
        return new RentalDtos.RentalDto(
                r.getId(),
                r.getCustomer().getId(),
                r.getCustomer().getFirstName() + " " + r.getCustomer().getLastName(),
                r.getCar().getId(),
                CarMapper.toLabel(r.getCar()),
                r.getStartAt(),
                r.getPlannedEndAt(),
                r.getActualReturnAt(),
                r.getRateType(),
                r.getStatus(),
                r.getBasePrice(),
                r.getLateFee(),
                r.getTotalPrice(),
                r.getNotes()
        );
    }
}
