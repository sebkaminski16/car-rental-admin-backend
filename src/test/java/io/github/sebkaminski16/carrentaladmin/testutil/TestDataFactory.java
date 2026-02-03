package io.github.sebkaminski16.carrentaladmin.testutil;

import io.github.sebkaminski16.carrentaladmin.entity.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestDataFactory {

    public static Category category(String name, BigDecimal dailyDiscountPercent, BigDecimal weeklyDiscountPercent) {
        return Category.builder()
                .name(name)
                .description("Test category")
                .dailyDiscountPercent(dailyDiscountPercent)
                .weeklyDiscountPercent(weeklyDiscountPercent)
                .build();
    }

    public static Brand brand(String name) {
        return Brand.builder().name(name).build();
    }

    public static CarModel model(String name, Brand brand) {
        return CarModel.builder().name(name).brand(brand).build();
    }

    public static Car car(String vin, String plate, CarModel model, Category category,
                          BigDecimal hourly, BigDecimal daily, BigDecimal weekly) {
        return Car.builder()
                .vin(vin)
                .licensePlate(plate)
                .productionYear(2000)
                .color("black")
                .status(CarStatus.AVAILABLE)
                .model(model)
                .category(category)
                .hourlyRate(hourly)
                .dailyRate(daily)
                .weeklyRate(weekly)
                .mileageKm(10000)
                .imageUrl(null)
                .build();
    }

    public static Customer customer(String email) {
        return Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .phone("123456789")
                .address("Test 1")
                .build();
    }

    public static Rental rental(Customer customer, Car car, LocalDateTime startAt,
                                LocalDateTime plannedEndAt, RateType rateType, RentalStatus status) {
        return Rental.builder()
                .customer(customer)
                .car(car)
                .startAt(startAt)
                .plannedEndAt(plannedEndAt)
                .rateType(rateType)
                .status(status)
                .basePrice(BigDecimal.valueOf(100))
                .lateFee(BigDecimal.ZERO)
                .totalPrice(BigDecimal.valueOf(100))
                .build();
    }
}
