package io.github.sebkaminski16.carrentaladmin.dto;

import io.github.sebkaminski16.carrentaladmin.entity.RateType;
import io.github.sebkaminski16.carrentaladmin.entity.RentalStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RentalDtos {

    public record RentalCreateRequest(
            @NotNull Long customerId,
            @NotNull Long carId,
            @NotNull LocalDateTime startAt,
            @NotNull LocalDateTime plannedEndAt,
            @NotNull RateType rateType,
            @Size(max = 255) String notes
    ) {}

    public record RentalUpdateRequest(
            @NotNull LocalDateTime plannedEndAt,
            @NotNull RateType rateType,
            @Size(max = 255) String notes
    ) {}

    public record RentalExtendRequest(
            @NotNull LocalDateTime newPlannedEndAt
    ) {}

    public record RentalReturnRequest(
            LocalDateTime actualReturnAt,
            Integer newMileageKm
    ) {}

    public record RentalPricePreviewResponse(
            BigDecimal basePrice,
            BigDecimal discountAppliedPercent,
            RateType rateType
    ) {}

    public record RentalDto(
            Long id,
            Long customerId,
            String customerName,
            Long carId,
            String carLabel,
            LocalDateTime startAt,
            LocalDateTime plannedEndAt,
            LocalDateTime actualReturnAt,
            RateType rateType,
            RentalStatus status,
            BigDecimal basePrice,
            BigDecimal lateFee,
            BigDecimal totalPrice,
            String notes
    ) {}
}
