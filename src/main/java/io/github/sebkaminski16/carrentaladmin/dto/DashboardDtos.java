package io.github.sebkaminski16.carrentaladmin.dto;

public class DashboardDtos {

    public record DashboardSummaryDto(
            long rentalsToday,
            long rentalsThisWeek,
            long activeRentals,
            long overdueRentals,
            double revenueToday,
            double revenueThisWeek
    ) {}

    public record CountByDayDto(
            String day,
            long count
    ) {}
}
