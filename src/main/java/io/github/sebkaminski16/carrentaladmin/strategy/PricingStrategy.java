package io.github.sebkaminski16.carrentaladmin.strategy;

import io.github.sebkaminski16.carrentaladmin.entity.Car;
import io.github.sebkaminski16.carrentaladmin.entity.Category;
import io.github.sebkaminski16.carrentaladmin.entity.RateType;
import java.time.LocalDateTime;

public interface PricingStrategy {

    RateType supports();

    PricingResult calculate(Car car, Category category, LocalDateTime startAt, LocalDateTime endAt);
}
