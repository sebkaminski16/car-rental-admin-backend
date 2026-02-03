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

class HourlyPricingStrategyTests {

    private final HourlyPricingStrategy strategy = new HourlyPricingStrategy();

    @Test
    void shouldCeilHours() {
        Category cat = TestDataFactory.category("A", BigDecimal.ZERO, BigDecimal.ZERO);
        Brand brand = TestDataFactory.brand("Brand");
        CarModel model = TestDataFactory.model("Model", brand);
        Car car = TestDataFactory.car("VIN1", "WX123", model, cat,
                new BigDecimal("10.00"), new BigDecimal("100.00"), new BigDecimal("500.00"));

        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2026, 1, 1, 10, 1);
        LocalDateTime end2 = LocalDateTime.of(2026, 1, 1, 11, 0);
        LocalDateTime end3 = LocalDateTime.of(2026, 1, 1, 11, 1);

        assertEquals(new BigDecimal("10.00"), strategy.calculate(car, cat, start, end1).price());
        assertEquals(new BigDecimal("10.00"), strategy.calculate(car, cat, start, end2).price());
        assertEquals(new BigDecimal("20.00"), strategy.calculate(car, cat, start, end3).price());
    }
}
