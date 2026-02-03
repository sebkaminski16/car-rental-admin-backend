package io.github.sebkaminski16.carrentaladmin.strategy;

import io.github.sebkaminski16.carrentaladmin.entity.RateType;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import org.springframework.stereotype.Component;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PricingStrategyFactory {

    private final Map<RateType, PricingStrategy> strategies = new EnumMap<>(RateType.class);

    public PricingStrategyFactory(List<PricingStrategy> strategyList) {
        for (PricingStrategy s : strategyList) {
            strategies.put(s.supports(), s);
        }
    }

    public PricingStrategy get(RateType rateType) {
        PricingStrategy strategy = strategies.get(rateType);
        if (strategy == null) {
            throw new BadRequestException("No pricing strategy for: " + rateType);
        }
        return strategy;
    }
}
