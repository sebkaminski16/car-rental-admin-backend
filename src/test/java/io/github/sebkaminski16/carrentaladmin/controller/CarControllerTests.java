package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.CarDtos;
import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.entity.CarStatus;
import io.github.sebkaminski16.carrentaladmin.entity.RateType;
import io.github.sebkaminski16.carrentaladmin.entity.RentalStatus;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.service.CarService;
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

@WebMvcTest(CarController.class)
public class CarControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    private final CarDtos.CarDto CAR_TOYOTA_COROLLA = new CarDtos.CarDto(
            1L, "VIN123", "ABC123", 2020, "black", CarStatus.AVAILABLE,
            1L, "Corolla", 1L, "Toyota", 1L, "Economy",
            null, new BigDecimal("10.00"), new BigDecimal("50.00"), new BigDecimal("300.00"), 10000
    );

    @Test
    public void testListReturnsAllCars() throws Exception {
        //given
        List<CarDtos.CarDto> cars = Arrays.asList(CAR_TOYOTA_COROLLA, new CarDtos.CarDto(
                2L, "VIN456", "XYZ789", 2021, "white", CarStatus.RENTED,
                2L, "Civic", 2L, "Honda", 1L, "Economy",
                null, new BigDecimal("12.00"), new BigDecimal("60.00"), new BigDecimal("350.00"), 5000
        ));
        when(carService.list()).thenReturn(cars);
        //when&then
        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].vin", is("VIN123")))
                .andExpect(jsonPath("$[0].licensePlate", is("ABC123")))
                .andExpect(jsonPath("$[0].status", is("AVAILABLE")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].status", is("RENTED")));

        verify(carService, times(1)).list();
    }

    @Test
    public void testListReturnsEmptyListWhenNoCars() throws Exception {
        //given
        when(carService.list()).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(carService, times(1)).list();
    }

    @Test
    public void testCreateCarSuccessfully() throws Exception {
        //given
        when(carService.create(any(CarDtos.CarCreateRequest.class))).thenReturn(CAR_TOYOTA_COROLLA);
        String requestBody = "{\"vin\":\"VIN123\",\"licensePlate\":\"ABC123\",\"productionYear\":2020,\"color\":\"black\",\"modelId\":1,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":10.00,\"dailyRate\":50.00,\"weeklyRate\":300.00,\"mileageKm\":10000}";
        //when&then
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.vin", is("VIN123")))
                .andExpect(jsonPath("$.licensePlate", is("ABC123")))
                .andExpect(jsonPath("$.status", is("AVAILABLE")));

        verify(carService, times(1)).create(any(CarDtos.CarCreateRequest.class));
    }

    @Test
    public void testCreateCarWithBlankVinReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"vin\":\"\",\"licensePlate\":\"ABC123\",\"productionYear\":2020,\"color\":\"black\",\"modelId\":1,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":10.00,\"dailyRate\":50.00,\"weeklyRate\":300.00,\"mileageKm\":10000}";
        //when&then
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carService, never()).create(any(CarDtos.CarCreateRequest.class));
    }

    @Test
    public void testCreateCarWithNullModelIdReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"vin\":\"VIN123\",\"licensePlate\":\"ABC123\",\"productionYear\":2020,\"color\":\"black\",\"modelId\":null,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":10.00,\"dailyRate\":50.00,\"weeklyRate\":300.00,\"mileageKm\":10000}";
        //when&then
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carService, never()).create(any(CarDtos.CarCreateRequest.class));
    }

    @Test
    public void testCreateCarWithInvalidProductionYearReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"vin\":\"VIN123\",\"licensePlate\":\"ABC123\",\"productionYear\":1900,\"color\":\"black\",\"modelId\":1,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":10.00,\"dailyRate\":50.00,\"weeklyRate\":300.00,\"mileageKm\":10000}";
        // when&then
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carService, never()).create(any(CarDtos.CarCreateRequest.class));
    }

    @Test
    public void testCreateCarWithNegativeHourlyRateReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"vin\":\"VIN123\",\"licensePlate\":\"ABC123\",\"productionYear\":2020,\"color\":\"black\",\"modelId\":1,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":-10.00,\"dailyRate\":50.00,\"weeklyRate\":300.00,\"mileageKm\":10000}";
        //when&then
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carService, never()).create(any(CarDtos.CarCreateRequest.class));
    }

    @Test
    public void testCreateCarWithNegativeMileageReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"vin\":\"VIN123\",\"licensePlate\":\"ABC123\",\"productionYear\":2020,\"color\":\"black\",\"modelId\":1,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":10.00,\"dailyRate\":50.00,\"weeklyRate\":300.00,\"mileageKm\":-1000}";
        //when&then
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carService, never()).create(any(CarDtos.CarCreateRequest.class));
    }

    @Test
    public void testCreateCarWhenVinAlreadyExistsReturnsBadRequest() throws Exception {
        // given
        when(carService.create(any(CarDtos.CarCreateRequest.class)))
                .thenThrow(new BadRequestException("Car with that vin already exists"));
        String requestBody = "{\"vin\":\"VIN123\",\"licensePlate\":\"ABC123\",\"productionYear\":2020,\"color\":\"black\",\"modelId\":1,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":10.00,\"dailyRate\":50.00,\"weeklyRate\":300.00,\"mileageKm\":10000}";
        // when&then
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carService, times(1)).create(any(CarDtos.CarCreateRequest.class));
    }

    @Test
    public void testCreateCarWhenLicensePlateAlreadyExistsReturnsBadRequest() throws Exception {
        //given
        when(carService.create(any(CarDtos.CarCreateRequest.class)))
                .thenThrow(new BadRequestException("Car with that license plate already exists"));
        String requestBody = "{\"vin\":\"VIN123\",\"licensePlate\":\"ABC123\",\"productionYear\":2020,\"color\":\"black\",\"modelId\":1,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":10.00,\"dailyRate\":50.00,\"weeklyRate\":300.00,\"mileageKm\":10000}";
        //when&then
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carService, times(1)).create(any(CarDtos.CarCreateRequest.class));
    }

    @Test
    public void testCreateCarWhenModelNotFoundReturnsNotFound() throws Exception {
        //given
        when(carService.create(any(CarDtos.CarCreateRequest.class)))
                .thenThrow(new NotFoundException("Model not found: 999"));
        String requestBody = "{\"vin\":\"VIN123\",\"licensePlate\":\"ABC123\",\"productionYear\":2020,\"color\":\"black\",\"modelId\":999,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":10.00,\"dailyRate\":50.00,\"weeklyRate\":300.00,\"mileageKm\":10000}";
        //when&then
        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(carService, times(1)).create(any(CarDtos.CarCreateRequest.class));
    }

    @Test
    public void testGetCarByIdSuccessfully() throws Exception {
        //given
        when(carService.get(1L)).thenReturn(CAR_TOYOTA_COROLLA);
        //when&then
        mockMvc.perform(get("/api/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.vin", is("VIN123")))
                .andExpect(jsonPath("$.licensePlate", is("ABC123")));

        verify(carService, times(1)).get(1L);
    }

    @Test
    public void testGetCarByIdWhenNotFoundReturnsNotFound() throws Exception {
        //given
        when(carService.get(999L))
                .thenThrow(new NotFoundException("Car not found: 999"));
        //when&then
        mockMvc.perform(get("/api/cars/999"))
                .andExpect(status().isNotFound());

        verify(carService, times(1)).get(999L);
    }

    @Test
    public void testUpdateCarSuccessfully() throws Exception {
        //given
        when(carService.update(eq(1L), any(CarDtos.CarUpdateRequest.class)))
                .thenReturn(new CarDtos.CarDto(
                        1L, "VIN123", "ABC123", 2021, "red", CarStatus.MAINTENANCE,
                        1L, "Corolla", 1L, "Toyota", 1L, "Economy",
                        null, new BigDecimal("12.00"), new BigDecimal("60.00"), new BigDecimal("350.00"), 15000
                ));
        String requestBody = "{\"vin\":\"VIN123\",\"licensePlate\":\"ABC123\",\"productionYear\":2021,\"color\":\"red\",\"modelId\":1,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":12.00,\"dailyRate\":60.00,\"weeklyRate\":350.00,\"mileageKm\":15000,\"status\":\"MAINTENANCE\"}";
        //when&then
        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("MAINTENANCE")))
                .andExpect(jsonPath("$.mileageKm", is(15000)));

        verify(carService, times(1)).update(eq(1L), any(CarDtos.CarUpdateRequest.class));
    }

    @Test
    public void testUpdateCarWithBlankVinReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"vin\":\"\",\"licensePlate\":\"ABC123\",\"productionYear\":2020,\"color\":\"black\",\"modelId\":1,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":10.00,\"dailyRate\":50.00,\"weeklyRate\":300.00,\"mileageKm\":10000,\"status\":\"AVAILABLE\"}";
        //when&then
        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carService, never()).update(any(Long.class), any(CarDtos.CarUpdateRequest.class));
    }

    @Test
    public void testUpdateCarWhenNotFoundReturnsNotFound() throws Exception {
        //given
        when(carService.update(eq(999L), any(CarDtos.CarUpdateRequest.class)))
                .thenThrow(new NotFoundException("Car not found: 999"));
        String requestBody = "{\"vin\":\"VIN123\",\"licensePlate\":\"ABC123\",\"productionYear\":2020,\"color\":\"black\",\"modelId\":1,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":10.00,\"dailyRate\":50.00,\"weeklyRate\":300.00,\"mileageKm\":10000,\"status\":\"AVAILABLE\"}";
        //when&then
        mockMvc.perform(put("/api/cars/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(carService, times(1)).update(eq(999L), any(CarDtos.CarUpdateRequest.class));
    }

    @Test
    public void testUpdateCarWhenHasActiveRentalReturnsBadRequest() throws Exception {
        //given
        when(carService.update(eq(1L), any(CarDtos.CarUpdateRequest.class)))
                .thenThrow(new BadRequestException("Car has an active rental. Return/cancel rental first."));
        String requestBody = "{\"vin\":\"VIN123\",\"licensePlate\":\"ABC123\",\"productionYear\":2020,\"color\":\"black\",\"modelId\":1,\"categoryId\":1,\"imageUrl\":null,\"hourlyRate\":10.00,\"dailyRate\":50.00,\"weeklyRate\":300.00,\"mileageKm\":10000,\"status\":\"AVAILABLE\"}";
        //when&then
        mockMvc.perform(put("/api/cars/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carService, times(1)).update(eq(1L), any(CarDtos.CarUpdateRequest.class));
    }

    @Test
    public void testDeleteCarSuccessfully() throws Exception {
        //given
        doNothing().when(carService).delete(1L);
        //when&then
        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isNoContent());

        verify(carService, times(1)).delete(1L);
    }

    @Test
    public void testDeleteCarWhenNotFoundReturnsNotFound() throws Exception {
        //given
        doThrow(new NotFoundException("Car not found: 999"))
                .when(carService).delete(999L);
        //when&then
        mockMvc.perform(delete("/api/cars/999"))
                .andExpect(status().isNotFound());

        verify(carService, times(1)).delete(999L);
    }

    @Test
    public void testDeleteCarWhenHasRentalsReturnsBadRequest() throws Exception {
        //given
        doThrow(new BadRequestException("Cannot delete, because a Rental with that car exists!"))
                .when(carService).delete(1L);
        //when&then
        mockMvc.perform(delete("/api/cars/1"))
                .andExpect(status().isBadRequest());

        verify(carService, times(1)).delete(1L);
    }

    @Test
    public void testSearchCarsWithQueryReturnsMatchingCars() throws Exception {
        //given
        List<CarDtos.CarDto> cars = List.of(CAR_TOYOTA_COROLLA);
        when(carService.search("ABC")).thenReturn(cars);
        //when&then
        mockMvc.perform(get("/api/cars/search")
                        .param("query", "ABC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].licensePlate", is("ABC123")));

        verify(carService, times(1)).search("ABC");
    }

    @Test
    public void testSearchCarsWithNullQueryReturnsAllCars() throws Exception {
        //given
        List<CarDtos.CarDto> cars = List.of(CAR_TOYOTA_COROLLA);
        when(carService.search(null)).thenReturn(cars);
        //when&then
        mockMvc.perform(get("/api/cars/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(carService, times(1)).search(null);
    }

    @Test
    public void testSearchCarsWithNoMatchesReturnsEmptyList() throws Exception {
        //given
        when(carService.search("xyz")).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/cars/search")
                        .param("query", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(carService, times(1)).search("xyz");
    }

    @Test
    public void testAvailableReturnsAvailableCars() throws Exception {
        //given
        List<CarDtos.CarDto> cars = List.of(CAR_TOYOTA_COROLLA);
        LocalDateTime from = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 5, 10, 0);
        when(carService.availableBetween(from, to)).thenReturn(cars);
        //when&then
        mockMvc.perform(get("/api/cars/available")
                        .param("from", "2026-01-01T10:00:00")
                        .param("to", "2026-01-05T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("AVAILABLE")));

        verify(carService, times(1)).availableBetween(from, to);
    }

    @Test
    public void testAvailableWithInvalidDateRangeReturnsBadRequest() throws Exception {
        //given
        LocalDateTime from = LocalDateTime.of(2026, 1, 5, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 1, 1, 10, 0);
        when(carService.availableBetween(from, to))
                .thenThrow(new BadRequestException("'to' must be after 'from'"));
        //when&then
        mockMvc.perform(get("/api/cars/available")
                        .param("from", "2026-01-05T10:00:00")
                        .param("to", "2026-01-01T10:00:00"))
                .andExpect(status().isBadRequest());

        verify(carService, times(1)).availableBetween(from, to);
    }

    @Test
    public void testByStatusReturnsCarsByStatus() throws Exception {
        //given
        List<CarDtos.CarDto> cars = List.of(CAR_TOYOTA_COROLLA);
        when(carService.listByStatus(CarStatus.AVAILABLE)).thenReturn(cars);
        //when&then
        mockMvc.perform(get("/api/cars/status/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("AVAILABLE")));

        verify(carService, times(1)).listByStatus(CarStatus.AVAILABLE);
    }

    @Test
    public void testByStatusReturnsEmptyListWhenNoCars() throws Exception {
        //given
        when(carService.listByStatus(CarStatus.MAINTENANCE)).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/cars/status/MAINTENANCE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(carService, times(1)).listByStatus(CarStatus.MAINTENANCE);
    }

    @Test
    public void testRentalsReturnsCarRentals() throws Exception {
        //given
        RentalDtos.RentalDto rental1 = new RentalDtos.RentalDto(
                1L, 1L, "John Doe", 1L, "Toyota Corolla",
                LocalDateTime.now(), LocalDateTime.now().plusDays(3), null,
                RateType.DAILY, RentalStatus.ACTIVE, new BigDecimal("300.00"), BigDecimal.ZERO,
                new BigDecimal("300.00"), "Test rental"
        );
        List<RentalDtos.RentalDto> rentals = List.of(rental1);
        when(carService.getRentals(1L)).thenReturn(rentals);
        //when&then
        mockMvc.perform(get("/api/cars/1/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].carId", is(1)))
                .andExpect(jsonPath("$[0].status", is("ACTIVE")));

        verify(carService, times(1)).getRentals(1L);
    }

    @Test
    public void testRentalsReturnsEmptyListWhenNoRentals() throws Exception {
        //given
        when(carService.getRentals(1L)).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/cars/1/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(carService, times(1)).getRentals(1L);
    }

    @Test
    public void testRentalsWhenCarNotFoundReturnsNotFound() throws Exception {
        //given
        when(carService.getRentals(999L))
                .thenThrow(new NotFoundException("Car not found: 999"));
        //when&then
        mockMvc.perform(get("/api/cars/999/rentals"))
                .andExpect(status().isNotFound());

        verify(carService, times(1)).getRentals(999L);
    }
}