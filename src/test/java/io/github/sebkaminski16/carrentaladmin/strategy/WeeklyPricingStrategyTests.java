package io.github.sebkaminski16.carrentaladmin.strategy;

import io.github.sebkaminski16.carrentaladmin.entity.Brand;
import io.github.sebkaminski16.carrentaladmin.entity.Car;
import io.github.sebkaminski16.carrentaladmin.entity.CarModel;
import io.github.sebkaminski16.carrentaladmin.entity.Category;
import io.github.sebkaminski16.carrentaladmin.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WeeklyPricingStrategyTests {

    private final WeeklyPricingStrategy strategy = new WeeklyPricingStrategy();

    @Test
    void shouldApplyWeeklyDiscount() {
        Category cat = TestDataFactory.category("C", BigDecimal.ZERO, new BigDecimal("10.0"));
        Brand brand = TestDataFactory.brand("Brand");
        CarModel model = TestDataFactory.model("Model", brand);
        Car car = TestDataFactory.car("VIN3", "WX125", model, cat,
                new BigDecimal("10.00"), new BigDecimal("100.00"), new BigDecimal("500.00"));

        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 8, 10, 0); // 7 days => 1 week

        PricingResult res = strategy.calculate(car, cat, start, end);
        assertEquals(new BigDecimal("450.00"), res.price());
        assertEquals(new BigDecimal("10.0"), res.discountPercent());
    }
}
