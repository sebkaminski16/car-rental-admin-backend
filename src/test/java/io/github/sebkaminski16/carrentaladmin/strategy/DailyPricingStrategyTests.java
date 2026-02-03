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

class DailyPricingStrategyTests {

    private final DailyPricingStrategy strategy = new DailyPricingStrategy();

    @Test
    void shouldApplyCategoryDiscount() {
        Category cat = TestDataFactory.category("B", new BigDecimal("5.0"), BigDecimal.ZERO);
        Brand brand = TestDataFactory.brand("Brand");
        CarModel model = TestDataFactory.model("Model", brand);
        Car car = TestDataFactory.car("VIN2", "WX124", model, cat,
                new BigDecimal("10.00"), new BigDecimal("100.00"), new BigDecimal("500.00"));

        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 2, 9, 0); // 23h => 1 day

        PricingResult res = strategy.calculate(car, cat, start, end);
        assertEquals(new BigDecimal("95.00"), res.price());
        assertEquals(new BigDecimal("5.0"), res.discountPercent());
    }
}
