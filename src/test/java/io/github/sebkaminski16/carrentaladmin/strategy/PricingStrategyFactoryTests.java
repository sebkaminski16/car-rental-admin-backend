package io.github.sebkaminski16.carrentaladmin.strategy;

import io.github.sebkaminski16.carrentaladmin.entity.RateType;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PricingStrategyFactoryTests {

    @Test
    void shouldReturnCorrectStrategy() {
        PricingStrategyFactory factory = new PricingStrategyFactory(List.of(
                new HourlyPricingStrategy(),
                new DailyPricingStrategy(),
                new WeeklyPricingStrategy()
        ));

        assertEquals(RateType.HOURLY, factory.get(RateType.HOURLY).supports());
        assertEquals(RateType.DAILY, factory.get(RateType.DAILY).supports());
        assertEquals(RateType.WEEKLY, factory.get(RateType.WEEKLY).supports());
    }

    @Test
    void shouldThrowForMissingStrategy() {
        PricingStrategyFactory factory = new PricingStrategyFactory(List.of(new HourlyPricingStrategy()));
        assertThrows(RuntimeException.class, () -> factory.get(RateType.WEEKLY));
    }
}
