package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.DashboardDtos;
import io.github.sebkaminski16.carrentaladmin.entity.RentalStatus;
import io.github.sebkaminski16.carrentaladmin.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private RentalRepository rentalRepository;

    public DashboardDtos.DashboardSummaryDto getSummary() {

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowStart = todayStart.plusDays(1);

        LocalDate today = LocalDate.now();
        LocalDate weekStartDate = today.with(DayOfWeek.MONDAY);
        LocalDateTime weekStart = weekStartDate.atStartOfDay();
        LocalDateTime nextWeekStart = weekStart.plusDays(7);

        long rentalsToday = rentalRepository.countRentalsStartedBetween(todayStart, tomorrowStart);
        long rentalsThisWeek = rentalRepository.countRentalsStartedBetween(weekStart, nextWeekStart);

        long active = rentalRepository.countByStatus(RentalStatus.ACTIVE);
        long overdue = rentalRepository.countOverdue(RentalStatus.ACTIVE, LocalDateTime.now());

        double revenueToday = safeDouble(rentalRepository.sumRevenueBetween(todayStart, tomorrowStart));
        double revenueWeek = safeDouble(rentalRepository.sumRevenueBetween(weekStart, nextWeekStart));

        return new DashboardDtos.DashboardSummaryDto(
                rentalsToday,
                rentalsThisWeek,
                active,
                overdue,
                revenueToday,
                revenueWeek
        );
    }

    public List<DashboardDtos.CountByDayDto> rentalsPerDay(int daysBack) {

        if (daysBack <= 0) daysBack = 7;
        LocalDate today = LocalDate.now();
        List<DashboardDtos.CountByDayDto> result = new ArrayList<>();

        for (int i = daysBack - 1; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            LocalDateTime from = day.atStartOfDay();
            LocalDateTime to = day.plusDays(1).atStartOfDay();
            long count = rentalRepository.countRentalsStartedBetween(from, to);
            result.add(new DashboardDtos.CountByDayDto(day.toString(), count));
        }

        return result;
    }

    private static double safeDouble(java.math.BigDecimal v) {
        return v == null ? 0.0 : v.doubleValue();
    }
}
