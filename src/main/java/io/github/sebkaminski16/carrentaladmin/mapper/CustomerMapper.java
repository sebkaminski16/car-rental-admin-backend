package io.github.sebkaminski16.carrentaladmin.mapper;

import io.github.sebkaminski16.carrentaladmin.dto.CustomerDtos;
import io.github.sebkaminski16.carrentaladmin.entity.Customer;

public class CustomerMapper {

    public static CustomerDtos.CustomerDto toDto(Customer c) {
        return new CustomerDtos.CustomerDto(
                c.getId(),
                c.getFirstName(),
                c.getLastName(),
                c.getEmail(),
                c.getPhone(),
                c.getAddress(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
