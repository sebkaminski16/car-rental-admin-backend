package io.github.sebkaminski16.carrentaladmin.strategy;

import java.math.BigDecimal;

public record PricingResult(
        BigDecimal price,
        BigDecimal discountPercent
) {}
