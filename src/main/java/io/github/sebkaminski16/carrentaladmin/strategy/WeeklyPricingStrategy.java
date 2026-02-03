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
public class WeeklyPricingStrategy implements PricingStrategy {

    @Override
    public RateType supports() {
        return RateType.WEEKLY;
    }

    @Override
    public PricingResult calculate(Car car, Category category, LocalDateTime startAt, LocalDateTime endAt) {

        long daysTotal = Duration.between(startAt, endAt).toDays();
        long weeks = (long) Math.ceil(daysTotal / 7.0);
        if (weeks <= 0) weeks = 1;

        BigDecimal discountPercent = category.getWeeklyDiscountPercent() != null ? category.getWeeklyDiscountPercent() : BigDecimal.ZERO;
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discountPercent.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));

        BigDecimal price = car.getWeeklyRate()
                .multiply(BigDecimal.valueOf(weeks))
                .multiply(discountMultiplier)
                .setScale(2, RoundingMode.HALF_UP);

        return new PricingResult(price, discountPercent);
    }
}
