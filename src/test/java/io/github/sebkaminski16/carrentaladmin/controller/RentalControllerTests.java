package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.entity.RateType;
import io.github.sebkaminski16.carrentaladmin.entity.RentalStatus;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.service.RentalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalController.class)
public class RentalControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    @Test
    public void testListReturnsAllRentals() throws Exception {
        //given
        RentalDtos.RentalDto rental1 = new RentalDtos.RentalDto(
                1L, 1L, "John Doe", 1L, "Toyota Corolla",
                LocalDateTime.now(), LocalDateTime.now().plusDays(3), null,
                RateType.DAILY, RentalStatus.ACTIVE, new BigDecimal("300.00"), BigDecimal.ZERO,
                new BigDecimal("300.00"), "Test rental"
        );
        RentalDtos.RentalDto rental2 = new RentalDtos.RentalDto(
                2L, 2L, "Jane Doe", 2L, "Honda Civic",
                LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(7),
                RateType.WEEKLY, RentalStatus.RETURNED, new BigDecimal("500.00"), BigDecimal.ZERO,
                new BigDecimal("500.00"), null
        );
        List<RentalDtos.RentalDto> rentals = Arrays.asList(rental1, rental2);
        when(rentalService.list()).thenReturn(rentals);

        //when&then
        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is("ACTIVE")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].status", is("RETURNED")));

        verify(rentalService, times(1)).list();
    }

    @Test
    public void testListReturnsEmptyListWhenNoRentals() throws Exception {
        //given
        when(rentalService.list()).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(rentalService, times(1)).list();
    }

    @Test
    public void testCreateRentalSuccessfully() throws Exception {
        //given
        RentalDtos.RentalDto createdRental = new RentalDtos.RentalDto(
                1L, 1L, "John Doe", 1L, "Toyota Corolla",
                LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 5, 10, 0), null,
                RateType.DAILY, RentalStatus.ACTIVE, new BigDecimal("400.00"), BigDecimal.ZERO,
                new BigDecimal("400.00"), "Test rental"
        );
        when(rentalService.create(any(RentalDtos.RentalCreateRequest.class))).thenReturn(createdRental);

        String requestBody = "{\"customerId\":1,\"carId\":1,\"startAt\":\"2026-01-01T10:00:00\",\"plannedEndAt\":\"2026-01-05T10:00:00\",\"rateType\":\"DAILY\",\"notes\":\"Test rental\"}";
        //when&then
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.rateType", is("DAILY")));

        verify(rentalService, times(1)).create(any(RentalDtos.RentalCreateRequest.class));
    }

    @Test
    public void testCreateRentalWithNullCustomerIdReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"customerId\":null,\"carId\":1,\"startAt\":\"2026-01-01T10:00:00\",\"plannedEndAt\":\"2026-01-05T10:00:00\",\"rateType\":\"DAILY\",\"notes\":\"Test rental\"}";
        //when&then
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(rentalService, never()).create(any(RentalDtos.RentalCreateRequest.class));
    }

    @Test
    public void testCreateRentalWithNullCarIdReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"customerId\":1,\"carId\":null,\"startAt\":\"2026-01-01T10:00:00\",\"plannedEndAt\":\"2026-01-05T10:00:00\",\"rateType\":\"DAILY\",\"notes\":\"Test rental\"}";
        //when&then
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(rentalService, never()).create(any(RentalDtos.RentalCreateRequest.class));
    }

    @Test
    public void testCreateRentalWithNullRateTypeReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"customerId\":1,\"carId\":1,\"startAt\":\"2026-01-01T10:00:00\",\"plannedEndAt\":\"2026-01-05T10:00:00\",\"rateType\":null,\"notes\":\"Test rental\"}";
        //when&then
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(rentalService, never()).create(any(RentalDtos.RentalCreateRequest.class));
    }

    @Test
    public void testCreateRentalWithTooLongNotesReturnsValidationError() throws Exception {
        //given
        String longNotes = "A".repeat(256);
        String requestBody = "{\"customerId\":1,\"carId\":1,\"startAt\":\"2026-01-01T10:00:00\",\"plannedEndAt\":\"2026-01-05T10:00:00\",\"rateType\":\"DAILY\",\"notes\":\"" + longNotes + "\"}";
        //when&then
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(rentalService, never()).create(any(RentalDtos.RentalCreateRequest.class));
    }

    @Test
    public void testCreateRentalWhenCarNotAvailableReturnsBadRequest() throws Exception {
        //given
        when(rentalService.create(any(RentalDtos.RentalCreateRequest.class)))
                .thenThrow(new BadRequestException("Car is not available (status=RENTED)"));

        String requestBody = "{\"customerId\":1,\"carId\":1,\"startAt\":\"2026-01-01T10:00:00\",\"plannedEndAt\":\"2026-01-05T10:00:00\",\"rateType\":\"DAILY\",\"notes\":\"Test rental\"}";
        //when&then
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(rentalService, times(1)).create(any(RentalDtos.RentalCreateRequest.class));
    }

    @Test
    public void testCreateRentalWhenCustomerNotFoundReturnsNotFound() throws Exception {
        //given
        when(rentalService.create(any(RentalDtos.RentalCreateRequest.class)))
                .thenThrow(new NotFoundException("Customer not found: 999"));

        String requestBody = "{\"customerId\":999,\"carId\":1,\"startAt\":\"2026-01-01T10:00:00\",\"plannedEndAt\":\"2026-01-05T10:00:00\",\"rateType\":\"DAILY\",\"notes\":\"Test rental\"}";
        //when&then
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(rentalService, times(1)).create(any(RentalDtos.RentalCreateRequest.class));
    }

    @Test
    public void testGetRentalByIdSuccessfully() throws Exception {
        //given
        RentalDtos.RentalDto rental = new RentalDtos.RentalDto(
                1L, 1L, "John Doe", 1L, "Toyota Corolla",
                LocalDateTime.now(), LocalDateTime.now().plusDays(3), null,
                RateType.DAILY, RentalStatus.ACTIVE, new BigDecimal("300.00"), BigDecimal.ZERO,
                new BigDecimal("300.00"), "Test rental"
        );
        when(rentalService.get(1L)).thenReturn(rental);
        //when&then
        mockMvc.perform(get("/api/rentals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        verify(rentalService, times(1)).get(1L);
    }

    @Test
    public void testGetRentalByIdWhenNotFoundReturnsNotFound() throws Exception {
        //given
        when(rentalService.get(999L))
                .thenThrow(new NotFoundException("Rental not found: 999"));
        //when&then
        mockMvc.perform(get("/api/rentals/999"))
                .andExpect(status().isNotFound());

        verify(rentalService, times(1)).get(999L);
    }

    @Test
    public void testUpdateRentalSuccessfully() throws Exception {
        //given
        RentalDtos.RentalDto updatedRental = new RentalDtos.RentalDto(
                1L, 1L, "John Doe", 1L, "Toyota Corolla",
                LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 7, 10, 0), null,
                RateType.WEEKLY, RentalStatus.ACTIVE, new BigDecimal("500.00"), BigDecimal.ZERO,
                new BigDecimal("500.00"), "Updated notes"
        );
        when(rentalService.update(eq(1L), any(RentalDtos.RentalUpdateRequest.class)))
                .thenReturn(updatedRental);

        String requestBody = "{\"plannedEndAt\":\"2026-01-07T10:00:00\",\"rateType\":\"WEEKLY\",\"notes\":\"Updated notes\"}";
        //when&then
        mockMvc.perform(put("/api/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.rateType", is("WEEKLY")));

        verify(rentalService, times(1)).update(eq(1L), any(RentalDtos.RentalUpdateRequest.class));
    }

    @Test
    public void testUpdateRentalWithNullPlannedEndAtReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"plannedEndAt\":null,\"rateType\":\"DAILY\",\"notes\":\"Test\"}";
        //when&then
        mockMvc.perform(put("/api/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(rentalService, never()).update(any(Long.class), any(RentalDtos.RentalUpdateRequest.class));
    }

    @Test
    public void testUpdateRentalWhenNotActiveReturnsBadRequest() throws Exception {
        //given
        when(rentalService.update(eq(1L), any(RentalDtos.RentalUpdateRequest.class)))
                .thenThrow(new BadRequestException("Only ACTIVE rentals can be updated"));

        String requestBody = "{\"plannedEndAt\":\"2026-01-07T10:00:00\",\"rateType\":\"DAILY\",\"notes\":\"Test\"}";
        //when&then
        mockMvc.perform(put("/api/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(rentalService, times(1)).update(eq(1L), any(RentalDtos.RentalUpdateRequest.class));
    }

    @Test
    public void testDeleteRentalSuccessfully() throws Exception {
        //given
        doNothing().when(rentalService).delete(1L);
        //when&then
        mockMvc.perform(delete("/api/rentals/1"))
                .andExpect(status().isNoContent());

        verify(rentalService, times(1)).delete(1L);
    }

    @Test
    public void testDeleteRentalWhenNotFoundReturnsNotFound() throws Exception {
        //given
        doThrow(new NotFoundException("Rental not found: 999"))
                .when(rentalService).delete(999L);
        //when&then
        mockMvc.perform(delete("/api/rentals/999"))
                .andExpect(status().isNotFound());

        verify(rentalService, times(1)).delete(999L);
    }

    @Test
    public void testActiveReturnsActiveRentals() throws Exception {
        //given
        RentalDtos.RentalDto rental1 = new RentalDtos.RentalDto(
                1L, 1L, "John Doe", 1L, "Toyota Corolla",
                LocalDateTime.now(), LocalDateTime.now().plusDays(3), null,
                RateType.DAILY, RentalStatus.ACTIVE, new BigDecimal("300.00"), BigDecimal.ZERO,
                new BigDecimal("300.00"), null
        );
        List<RentalDtos.RentalDto> rentals = List.of(rental1);
        when(rentalService.listActive()).thenReturn(rentals);
        //when&then
        mockMvc.perform(get("/api/rentals/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("ACTIVE")));

        verify(rentalService, times(1)).listActive();
    }

    @Test
    public void testActiveReturnsEmptyListWhenNoActiveRentals() throws Exception {
        //given
        when(rentalService.listActive()).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/rentals/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(rentalService, times(1)).listActive();
    }

    @Test
    public void testOverdueReturnsOverdueRentals() throws Exception {
        //given
        RentalDtos.RentalDto rental1 = new RentalDtos.RentalDto(
                1L, 1L, "John Doe", 1L, "Toyota Corolla",
                LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(3), null,
                RateType.DAILY, RentalStatus.ACTIVE, new BigDecimal("300.00"), BigDecimal.ZERO,
                new BigDecimal("300.00"), null
        );
        List<RentalDtos.RentalDto> rentals = List.of(rental1);
        when(rentalService.listOverdue()).thenReturn(rentals);
        //when&then
        mockMvc.perform(get("/api/rentals/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(rentalService, times(1)).listOverdue();
    }

    @Test
    public void testOverdueReturnsEmptyListWhenNoOverdueRentals() throws Exception {
        //given
        when(rentalService.listOverdue()).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/rentals/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(rentalService, times(1)).listOverdue();
    }

    @Test
    public void testPreviewReturnsPricePreview() throws Exception {
        //given
        RentalDtos.RentalPricePreviewResponse preview = new RentalDtos.RentalPricePreviewResponse(
                new BigDecimal("400.00"), new BigDecimal("10.00"), RateType.DAILY
        );
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 5, 10, 0);
        when(rentalService.previewPrice(1L, RateType.DAILY, start, end)).thenReturn(preview);
        //when&then
        mockMvc.perform(get("/api/rentals/preview")
                        .param("carId", "1")
                        .param("rateType", "DAILY")
                        .param("startAt", "2026-01-01T10:00:00")
                        .param("plannedEndAt", "2026-01-05T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basePrice", is(400.00)))
                .andExpect(jsonPath("$.discountAppliedPercent", is(10.00)))
                .andExpect(jsonPath("$.rateType", is("DAILY")));

        verify(rentalService, times(1)).previewPrice(1L, RateType.DAILY, start, end);
    }

    @Test
    public void testPreviewWhenInvalidDateRangeReturnsBadRequest() throws Exception {
        //given
        LocalDateTime start = LocalDateTime.of(2026, 1, 5, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 10, 0);
        when(rentalService.previewPrice(1L, RateType.DAILY, start, end))
                .thenThrow(new BadRequestException("plannedEndAt must be after startAt"));
        //when&then
        mockMvc.perform(get("/api/rentals/preview")
                        .param("carId", "1")
                        .param("rateType", "DAILY")
                        .param("startAt", "2026-01-05T10:00:00")
                        .param("plannedEndAt", "2026-01-01T10:00:00"))
                .andExpect(status().isBadRequest());

        verify(rentalService, times(1)).previewPrice(1L, RateType.DAILY, start, end);
    }

    @Test
    public void testExtendRentalSuccessfully() throws Exception {
        //given
        RentalDtos.RentalDto extendedRental = new RentalDtos.RentalDto(
                1L, 1L, "John Doe", 1L, "Toyota Corolla",
                LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 10, 10, 0), null,
                RateType.DAILY, RentalStatus.ACTIVE, new BigDecimal("600.00"), BigDecimal.ZERO,
                new BigDecimal("600.00"), null
        );
        when(rentalService.extend(eq(1L), any(RentalDtos.RentalExtendRequest.class)))
                .thenReturn(extendedRental);

        String requestBody = "{\"newPlannedEndAt\":\"2026-01-10T10:00:00\"}";
        //when&then
        mockMvc.perform(put("/api/rentals/1/extend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.basePrice", is(600.00)));

        verify(rentalService, times(1)).extend(eq(1L), any(RentalDtos.RentalExtendRequest.class));
    }

    @Test
    public void testExtendRentalWithNullNewPlannedEndAtReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"newPlannedEndAt\":null}";
        //when&then
        mockMvc.perform(put("/api/rentals/1/extend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(rentalService, never()).extend(any(Long.class), any(RentalDtos.RentalExtendRequest.class));
    }

    @Test
    public void testExtendRentalWhenNotActiveReturnsBadRequest() throws Exception {
        //given
        when(rentalService.extend(eq(1L), any(RentalDtos.RentalExtendRequest.class)))
                .thenThrow(new BadRequestException("Only ACTIVE rentals can be extended"));

        String requestBody = "{\"newPlannedEndAt\":\"2026-01-10T10:00:00\"}";
        //when&then
        mockMvc.perform(put("/api/rentals/1/extend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(rentalService, times(1)).extend(eq(1L), any(RentalDtos.RentalExtendRequest.class));
    }

    @Test
    public void testReturnRentalSuccessfully() throws Exception {
        //given
        RentalDtos.RentalDto returnedRental = new RentalDtos.RentalDto(
                1L, 1L, "John Doe", 1L, "Toyota Corolla",
                LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 5, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.RETURNED,
                new BigDecimal("400.00"), BigDecimal.ZERO, new BigDecimal("400.00"), null
        );
        when(rentalService.returnRental(eq(1L), any(RentalDtos.RentalReturnRequest.class)))
                .thenReturn(returnedRental);

        String requestBody = "{\"actualReturnAt\":\"2026-01-05T10:00:00\",\"newMileageKm\":15000}";
        //when&then
        mockMvc.perform(put("/api/rentals/1/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("RETURNED")));

        verify(rentalService, times(1)).returnRental(eq(1L), any(RentalDtos.RentalReturnRequest.class));
    }

    @Test
    public void testReturnRentalWithLateFee() throws Exception {
        //given
        RentalDtos.RentalDto returnedRental = new RentalDtos.RentalDto(
                1L, 1L, "John Doe", 1L, "Toyota Corolla",
                LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 5, 10, 0),
                LocalDateTime.of(2026, 1, 7, 10, 0), RateType.DAILY, RentalStatus.RETURNED,
                new BigDecimal("400.00"), new BigDecimal("50.00"), new BigDecimal("450.00"), null
        );
        when(rentalService.returnRental(eq(1L), any(RentalDtos.RentalReturnRequest.class)))
                .thenReturn(returnedRental);

        String requestBody = "{\"actualReturnAt\":\"2026-01-07T10:00:00\",\"newMileageKm\":15000}";
        //when&then
        mockMvc.perform(put("/api/rentals/1/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lateFee", is(50.00)))
                .andExpect(jsonPath("$.totalPrice", is(450.00)));

        verify(rentalService, times(1)).returnRental(eq(1L), any(RentalDtos.RentalReturnRequest.class));
    }

    @Test
    public void testReturnRentalWhenNotActiveReturnsBadRequest() throws Exception {
        //given
        when(rentalService.returnRental(eq(1L), any(RentalDtos.RentalReturnRequest.class)))
                .thenThrow(new BadRequestException("Only ACTIVE rentals can be returned"));

        String requestBody = "{\"actualReturnAt\":\"2026-01-05T10:00:00\",\"newMileageKm\":15000}";
        //when&then
        mockMvc.perform(put("/api/rentals/1/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(rentalService, times(1)).returnRental(eq(1L), any(RentalDtos.RentalReturnRequest.class));
    }

    @Test
    public void testCancelRentalSuccessfully() throws Exception {
        //given
        RentalDtos.RentalDto canceledRental = new RentalDtos.RentalDto(
                1L, 1L, "John Doe", 1L, "Toyota Corolla",
                LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 5, 10, 0),
                LocalDateTime.now(), RateType.DAILY, RentalStatus.CANCELED,
                new BigDecimal("400.00"), BigDecimal.ZERO, new BigDecimal("400.00"), null
        );
        when(rentalService.cancel(1L)).thenReturn(canceledRental);
        //when&then
        mockMvc.perform(put("/api/rentals/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("CANCELED")));

        verify(rentalService, times(1)).cancel(1L);
    }

    @Test
    public void testCancelRentalWhenNotActiveReturnsBadRequest() throws Exception {
        //given
        when(rentalService.cancel(1L))
                .thenThrow(new BadRequestException("Only ACTIVE rentals can be canceled"));
        //when&then
        mockMvc.perform(put("/api/rentals/1/cancel"))
                .andExpect(status().isBadRequest());

        verify(rentalService, times(1)).cancel(1L);
    }

    @Test
    public void testCancelRentalWhenNotFoundReturnsNotFound() throws Exception {
        //given
        when(rentalService.cancel(999L))
                .thenThrow(new NotFoundException("Rental not found: 999"));
        //when&then
        mockMvc.perform(put("/api/rentals/999/cancel"))
                .andExpect(status().isNotFound());

        verify(rentalService, times(1)).cancel(999L);
    }
}