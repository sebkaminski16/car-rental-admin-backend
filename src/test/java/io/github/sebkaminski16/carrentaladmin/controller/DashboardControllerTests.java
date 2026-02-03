package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.DashboardDtos;
import io.github.sebkaminski16.carrentaladmin.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
public class DashboardControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    public void testSummaryReturnsDashboardData() throws Exception {
        //given
        DashboardDtos.DashboardSummaryDto summary = new DashboardDtos.DashboardSummaryDto(
                5L, 20L, 10L, 2L, 500.0, 2000.0
        );
        when(dashboardService.getSummary()).thenReturn(summary);
        //when&then
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalsToday", is(5)))
                .andExpect(jsonPath("$.rentalsThisWeek", is(20)))
                .andExpect(jsonPath("$.activeRentals", is(10)))
                .andExpect(jsonPath("$.overdueRentals", is(2)))
                .andExpect(jsonPath("$.revenueToday", is(500.0)))
                .andExpect(jsonPath("$.revenueThisWeek", is(2000.0)));

        verify(dashboardService, times(1)).getSummary();
    }

    @Test
    public void testSummaryReturnsZeroValues() throws Exception {
        //given
        DashboardDtos.DashboardSummaryDto summary = new DashboardDtos.DashboardSummaryDto(
                0L, 0L, 0L, 0L, 0.0, 0.0
        );
        when(dashboardService.getSummary()).thenReturn(summary);
        //when&then
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalsToday", is(0)))
                .andExpect(jsonPath("$.rentalsThisWeek", is(0)))
                .andExpect(jsonPath("$.activeRentals", is(0)))
                .andExpect(jsonPath("$.overdueRentals", is(0)))
                .andExpect(jsonPath("$.revenueToday", is(0.0)))
                .andExpect(jsonPath("$.revenueThisWeek", is(0.0)));

        verify(dashboardService, times(1)).getSummary();
    }

    @Test
    public void testRentalsPerDayWithDefaultDaysParameter() throws Exception {
        //given
        DashboardDtos.CountByDayDto day1 = new DashboardDtos.CountByDayDto("2026-01-01", 3L);
        DashboardDtos.CountByDayDto day2 = new DashboardDtos.CountByDayDto("2026-01-02", 5L);
        DashboardDtos.CountByDayDto day3 = new DashboardDtos.CountByDayDto("2026-01-03", 2L);
        List<DashboardDtos.CountByDayDto> counts = Arrays.asList(day1, day2, day3);
        when(dashboardService.rentalsPerDay(7)).thenReturn(counts);
        //when&then
        mockMvc.perform(get("/api/dashboard/rentals-per-day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].day", is("2026-01-01")))
                .andExpect(jsonPath("$[0].count", is(3)))
                .andExpect(jsonPath("$[1].day", is("2026-01-02")))
                .andExpect(jsonPath("$[1].count", is(5)))
                .andExpect(jsonPath("$[2].day", is("2026-01-03")))
                .andExpect(jsonPath("$[2].count", is(2)));

        verify(dashboardService, times(1)).rentalsPerDay(7);
    }

    @Test
    public void testRentalsPerDayWithCustomDaysParameter() throws Exception {
        //given
        DashboardDtos.CountByDayDto day1 = new DashboardDtos.CountByDayDto("2026-01-01", 10L);
        DashboardDtos.CountByDayDto day2 = new DashboardDtos.CountByDayDto("2026-01-02", 15L);
        List<DashboardDtos.CountByDayDto> counts = Arrays.asList(day1, day2);
        when(dashboardService.rentalsPerDay(14)).thenReturn(counts);
        //when&then
        mockMvc.perform(get("/api/dashboard/rentals-per-day")
                        .param("days", "14"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].day", is("2026-01-01")))
                .andExpect(jsonPath("$[0].count", is(10)))
                .andExpect(jsonPath("$[1].day", is("2026-01-02")))
                .andExpect(jsonPath("$[1].count", is(15)));

        verify(dashboardService, times(1)).rentalsPerDay(14);
    }

    @Test
    public void testRentalsPerDayWithSingleDay() throws Exception {
        //given
        DashboardDtos.CountByDayDto day1 = new DashboardDtos.CountByDayDto("2026-01-01", 7L);
        List<DashboardDtos.CountByDayDto> counts = List.of(day1);
        when(dashboardService.rentalsPerDay(1)).thenReturn(counts);
        //when&then
        mockMvc.perform(get("/api/dashboard/rentals-per-day")
                        .param("days", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].day", is("2026-01-01")))
                .andExpect(jsonPath("$[0].count", is(7)));

        verify(dashboardService, times(1)).rentalsPerDay(1);
    }

    @Test
    public void testRentalsPerDayReturnsEmptyList() throws Exception {
        //given
        when(dashboardService.rentalsPerDay(7)).thenReturn(Collections.emptyList());

        //when&then
        mockMvc.perform(get("/api/dashboard/rentals-per-day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(dashboardService, times(1)).rentalsPerDay(7);
    }

    @Test
    public void testRentalsPerDayWithZeroCounts() throws Exception {
        //given
        DashboardDtos.CountByDayDto day1 = new DashboardDtos.CountByDayDto("2026-01-01", 0L);
        DashboardDtos.CountByDayDto day2 = new DashboardDtos.CountByDayDto("2026-01-02", 0L);
        List<DashboardDtos.CountByDayDto> counts = Arrays.asList(day1, day2);
        when(dashboardService.rentalsPerDay(7)).thenReturn(counts);

        //when&then
        mockMvc.perform(get("/api/dashboard/rentals-per-day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].count", is(0)))
                .andExpect(jsonPath("$[1].count", is(0)));

        verify(dashboardService, times(1)).rentalsPerDay(7);
    }

    @Test
    public void testRentalsPerDayWithLargeDaysParameter() throws Exception {
        //given
        DashboardDtos.CountByDayDto day1 = new DashboardDtos.CountByDayDto("2026-01-01", 5L);
        List<DashboardDtos.CountByDayDto> counts = List.of(day1);
        when(dashboardService.rentalsPerDay(30)).thenReturn(counts);

        //when&then
        mockMvc.perform(get("/api/dashboard/rentals-per-day")
                        .param("days", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(dashboardService, times(1)).rentalsPerDay(30);
    }
}