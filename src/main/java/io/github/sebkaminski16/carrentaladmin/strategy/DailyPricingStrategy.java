package io.github.sebkaminski16.carrentaladmin.strategy;

import io.github.sebkaminski16.carrentaladmin.entity.Car;
import io.github.sebkaminski16.carrentaladmin.entity.Category;
import io.github.sebkaminski16.carrentaladmin.entity.RateType;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class DailyPricingStrategy implements PricingStrategy {

    @Override
    public RateType supports() {
        return RateType.DAILY;
    }

    @Override
    public PricingResult calculate(Car car, Category category, LocalDateTime startAt, LocalDateTime endAt) {

        long hours = Duration.between(startAt, endAt).toHours();
        long days = (long) Math.ceil(hours / 24.0);
        if (days <= 0) days = 1;

        BigDecimal discountPercent = category.getDailyDiscountPercent() != null ? category.getDailyDiscountPercent() : BigDecimal.ZERO;
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discountPercent.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));

        BigDecimal price = car.getDailyRate()
                .multiply(BigDecimal.valueOf(days))
                .multiply(discountMultiplier)
                .setScale(2, RoundingMode.HALF_UP);

        return new PricingResult(price, discountPercent);
    }
}
