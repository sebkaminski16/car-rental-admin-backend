package io.github.sebkaminski16.carrentaladmin.strategy;

import io.github.sebkaminski16.carrentaladmin.entity.Car;
import io.github.sebkaminski16.carrentaladmin.entity.Category;
import io.github.sebkaminski16.carrentaladmin.entity.RateType;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class HourlyPricingStrategy implements PricingStrategy {

    @Override
    public RateType supports() {
        return RateType.HOURLY;
    }

    @Override
    public PricingResult calculate(Car car, Category category, LocalDateTime startAt, LocalDateTime endAt) {

        long minutes = Duration.between(startAt, endAt).toMinutes();
        long hours = (long) Math.ceil(minutes / 60.0);
        if (hours <= 0) hours = 1;

        BigDecimal price = car.getHourlyRate().multiply(BigDecimal.valueOf(hours));
        return new PricingResult(price, BigDecimal.ZERO);
    }
}
