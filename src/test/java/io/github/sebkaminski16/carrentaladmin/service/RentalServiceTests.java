package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.entity.*;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.repository.CarRepository;
import io.github.sebkaminski16.carrentaladmin.repository.RentalRepository;
import io.github.sebkaminski16.carrentaladmin.strategy.PricingResult;
import io.github.sebkaminski16.carrentaladmin.strategy.PricingStrategy;
import io.github.sebkaminski16.carrentaladmin.strategy.PricingStrategyFactory;
import io.github.sebkaminski16.carrentaladmin.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTests {

    @Mock
    private CustomerService customerService;

    @Mock
    private CarService carService;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private PricingStrategyFactory pricingStrategyFactory;

    @Mock
    private PricingStrategy pricingStrategy;

    @InjectMocks
    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(rentalService, "LATE_FEE_HOURLY_PERCENT", new BigDecimal("50"));
    }

    @Test
    void testListReturnsAllRentals() {
        //given
        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental1 = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental1.setId(1L);

        Rental rental2 = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 2, 1, 10, 0),
                LocalDateTime.of(2026, 2, 5, 10, 0), RateType.DAILY, RentalStatus.RETURNED);
        rental2.setId(2L);

        when(rentalRepository.findAll()).thenReturn(Arrays.asList(rental1, rental2));
        //when
        List<RentalDtos.RentalDto> result = rentalService.list();
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(RentalStatus.ACTIVE, result.getFirst().status());
        assertEquals(RentalStatus.RETURNED, result.get(1).status());
        verify(rentalRepository, times(1)).findAll();
    }

    @Test
    void testListReturnsEmptyListWhenNoRentals() {
        //given
        when(rentalRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<RentalDtos.RentalDto> result = rentalService.list();
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(rentalRepository, times(1)).findAll();
    }

    @Test
    void testGetReturnsRentalDtoWhenExists() {
        //given
        Long rentalId = 1L;
        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental.setId(rentalId);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        //when
        RentalDtos.RentalDto result = rentalService.get(rentalId);
        //then
        assertNotNull(result);
        assertEquals(rentalId, result.id());
        assertEquals(RentalStatus.ACTIVE, result.status());
        verify(rentalRepository, times(1)).findById(rentalId);
    }

    @Test
    void testGetThrowsNotFoundExceptionWhenRentalDoesNotExist() {
        //given
        Long rentalId = 1L;
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> rentalService.get(rentalId));

        assertEquals("Rental not found: 1", exception.getMessage());
        verify(rentalRepository, times(1)).findById(rentalId);
    }

    @Test
    void testGetEntityReturnsRentalWhenExists() {
        //given
        Long rentalId = 1L;
        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental.setId(rentalId);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        //when
        Rental result = rentalService.getEntity(rentalId);
        //then
        assertNotNull(result);
        assertEquals(rentalId, result.getId());
        assertEquals(RentalStatus.ACTIVE, result.getStatus());
        verify(rentalRepository, times(1)).findById(rentalId);
    }

    @Test
    void testGetEntityThrowsNotFoundExceptionWhenRentalDoesNotExist() {
        //given
        Long rentalId = 1L;
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> rentalService.getEntity(rentalId));

        assertEquals("Rental not found: 1", exception.getMessage());
        verify(rentalRepository, times(1)).findById(rentalId);
    }

    @Test
    void testListActiveReturnsActiveRentals() {
        //given
        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental1 = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental1.setId(1L);

        when(rentalRepository.findByStatusOrderByStartAtDesc(RentalStatus.ACTIVE))
                .thenReturn(Collections.singletonList(rental1));
        //when
        List<RentalDtos.RentalDto> result = rentalService.listActive();
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(RentalStatus.ACTIVE, result.getFirst().status());
        verify(rentalRepository, times(1)).findByStatusOrderByStartAtDesc(RentalStatus.ACTIVE);
    }

    @Test
    void testListActiveReturnsEmptyListWhenNoActiveRentals() {
        //given
        when(rentalRepository.findByStatusOrderByStartAtDesc(RentalStatus.ACTIVE))
                .thenReturn(Collections.emptyList());
        //when
        List<RentalDtos.RentalDto> result = rentalService.listActive();
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(rentalRepository, times(1)).findByStatusOrderByStartAtDesc(RentalStatus.ACTIVE);
    }

    @Test
    void testListOverdueReturnsOverdueRentals() {
        //given
        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental overdueRental = TestDataFactory.rental(customer, car, LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(2), RateType.DAILY, RentalStatus.ACTIVE);
        overdueRental.setId(1L);

        when(rentalRepository.findOverdue(eq(RentalStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(overdueRental));
        //when
        List<RentalDtos.RentalDto> result = rentalService.listOverdue();
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(RentalStatus.ACTIVE, result.getFirst().status());
        verify(rentalRepository, times(1)).findOverdue(eq(RentalStatus.ACTIVE), any(LocalDateTime.class));
    }

    @Test
    void testListOverdueReturnsEmptyListWhenNoOverdueRentals() {
        //given
        when(rentalRepository.findOverdue(eq(RentalStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        //when
        List<RentalDtos.RentalDto> result = rentalService.listOverdue();
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(rentalRepository, times(1)).findOverdue(eq(RentalStatus.ACTIVE), any(LocalDateTime.class));
    }

    @Test
    void testCreateSuccessfullyCreatesNewRental() {
        //given
        Long customerId = 1L;
        Long carId = 1L;
        LocalDateTime startAt = LocalDateTime.now().plusHours(2).withSecond(0).withNano(0);
        LocalDateTime plannedEndAt = startAt.plusDays(3);

        RentalDtos.RentalCreateRequest request = new RentalDtos.RentalCreateRequest(
                customerId,
                carId,
                startAt,
                plannedEndAt,
                RateType.DAILY,
                "Test rental"
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(customerId);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(carId);
        car.setStatus(CarStatus.AVAILABLE);

        PricingResult pricingResult = new PricingResult(BigDecimal.valueOf(142.50), BigDecimal.valueOf(5.0));

        Rental savedRental = TestDataFactory.rental(customer, car, startAt, plannedEndAt, RateType.DAILY, RentalStatus.ACTIVE);
        savedRental.setId(1L);
        savedRental.setBasePrice(BigDecimal.valueOf(142.50));
        savedRental.setTotalPrice(BigDecimal.valueOf(142.50));
        savedRental.setNotes("Test rental");

        when(customerService.getEntity(customerId)).thenReturn(customer);
        when(carService.getEntity(carId)).thenReturn(car);
        when(pricingStrategyFactory.get(RateType.DAILY)).thenReturn(pricingStrategy);
        when(pricingStrategy.calculate(eq(car), eq(economy), eq(startAt), eq(plannedEndAt)))
                .thenReturn(pricingResult);
        when(rentalRepository.save(any(Rental.class))).thenReturn(savedRental);
        when(carRepository.save(any(Car.class))).thenReturn(car);
        //when
        RentalDtos.RentalDto result = rentalService.create(request);
        //then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(RentalStatus.ACTIVE, result.status());
        assertEquals(BigDecimal.valueOf(142.50), result.basePrice());
        verify(customerService, times(1)).getEntity(customerId);
        verify(carService, times(1)).getEntity(carId);
        verify(pricingStrategyFactory, times(1)).get(RateType.DAILY);
        verify(pricingStrategy, times(1)).calculate(eq(car), eq(economy), eq(startAt), eq(plannedEndAt));
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenStartAtIsInThePast() {
        //given
        LocalDateTime startAt = LocalDateTime.now().minusHours(1).withSecond(0).withNano(0);
        LocalDateTime plannedEndAt = startAt.plusDays(3);

        RentalDtos.RentalCreateRequest request = new RentalDtos.RentalCreateRequest(
                1L,
                1L,
                startAt,
                plannedEndAt,
                RateType.DAILY,
                "Test rental"
        );
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.create(request));

        assertEquals("startAt must be now or in the future", exception.getMessage());
        verify(customerService, never()).getEntity(any());
        verify(carService, never()).getEntity(any());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenPlannedEndAtIsNotAfterStartAt() {
        //given
        LocalDateTime startAt = LocalDateTime.now().plusHours(2).withSecond(0).withNano(0);
        LocalDateTime plannedEndAt = startAt.minusHours(1);

        RentalDtos.RentalCreateRequest request = new RentalDtos.RentalCreateRequest(
                1L,
                1L,
                startAt,
                plannedEndAt,
                RateType.DAILY,
                "Test rental"
        );
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.create(request));

        assertEquals("plannedEndAt must be after startAt", exception.getMessage());
        verify(customerService, never()).getEntity(any());
        verify(carService, never()).getEntity(any());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenCarIsNotAvailable() {
        //given
        Long customerId = 1L;
        Long carId = 1L;
        LocalDateTime startAt = LocalDateTime.now().plusHours(2).withSecond(0).withNano(0);
        LocalDateTime plannedEndAt = startAt.plusDays(3);

        RentalDtos.RentalCreateRequest request = new RentalDtos.RentalCreateRequest(
                customerId,
                carId,
                startAt,
                plannedEndAt,
                RateType.DAILY,
                "Test rental"
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(customerId);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(carId);
        car.setStatus(CarStatus.RENTED);

        when(customerService.getEntity(customerId)).thenReturn(customer);
        when(carService.getEntity(carId)).thenReturn(car);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.create(request));

        assertEquals("Car is not available (status=RENTED)", exception.getMessage());
        verify(customerService, times(1)).getEntity(customerId);
        verify(carService, times(1)).getEntity(carId);
        verify(pricingStrategyFactory, never()).get(any());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void testUpdateSuccessfullyUpdatesActiveRental() {
        //given
        Long rentalId = 1L;
        LocalDateTime newPlannedEndAt = LocalDateTime.of(2026, 1, 10, 10, 0);

        RentalDtos.RentalUpdateRequest request = new RentalDtos.RentalUpdateRequest(
                newPlannedEndAt,
                RateType.WEEKLY,
                "Updated notes"
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental existingRental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        existingRental.setId(rentalId);

        PricingResult pricingResult = new PricingResult(BigDecimal.valueOf(255.0), BigDecimal.valueOf(15.0));

        Rental updatedRental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                newPlannedEndAt, RateType.WEEKLY, RentalStatus.ACTIVE);
        updatedRental.setId(rentalId);
        updatedRental.setBasePrice(BigDecimal.valueOf(255.0));
        updatedRental.setTotalPrice(BigDecimal.valueOf(255.0));
        updatedRental.setNotes("Updated notes");

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(existingRental));
        when(pricingStrategyFactory.get(RateType.WEEKLY)).thenReturn(pricingStrategy);
        when(pricingStrategy.calculate(eq(car), eq(economy), any(LocalDateTime.class), eq(newPlannedEndAt)))
                .thenReturn(pricingResult);
        when(rentalRepository.save(any(Rental.class))).thenReturn(updatedRental);
        //when
        RentalDtos.RentalDto result = rentalService.update(rentalId, request);
        //then
        assertNotNull(result);
        assertEquals(rentalId, result.id());
        assertEquals(BigDecimal.valueOf(255.0), result.basePrice());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(pricingStrategyFactory, times(1)).get(RateType.WEEKLY);
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void testUpdateThrowsBadRequestExceptionWhenRentalIsNotActive() {
        //given
        Long rentalId = 1L;

        RentalDtos.RentalUpdateRequest request = new RentalDtos.RentalUpdateRequest(
                LocalDateTime.of(2026, 1, 10, 10, 0),
                RateType.DAILY,
                "Updated notes"
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.RETURNED);
        rental.setId(rentalId);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.update(rentalId, request));

        assertEquals("Only ACTIVE rentals can be updated", exception.getMessage());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(pricingStrategyFactory, never()).get(any());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void testUpdateThrowsBadRequestExceptionWhenPlannedEndAtIsNotAfterStartAt() {
        //given
        Long rentalId = 1L;

        RentalDtos.RentalUpdateRequest request = new RentalDtos.RentalUpdateRequest(
                LocalDateTime.of(2023, 12, 31, 10, 0),
                RateType.DAILY,
                "Updated notes"
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental.setId(rentalId);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.update(rentalId, request));

        assertEquals("plannedEndAt must be after startAt", exception.getMessage());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(pricingStrategyFactory, never()).get(any());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void testExtendSuccessfullyExtendsActiveRental() {
        //given
        Long rentalId = 1L;
        LocalDateTime newPlannedEndAt = LocalDateTime.of(2026, 1, 10, 10, 0);

        RentalDtos.RentalExtendRequest request = new RentalDtos.RentalExtendRequest(newPlannedEndAt);

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental existingRental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        existingRental.setId(rentalId);

        PricingResult pricingResult = new PricingResult(BigDecimal.valueOf(450.0), BigDecimal.valueOf(5.0));

        Rental extendedRental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                newPlannedEndAt, RateType.DAILY, RentalStatus.ACTIVE);
        extendedRental.setId(rentalId);
        extendedRental.setBasePrice(BigDecimal.valueOf(450.0));
        extendedRental.setTotalPrice(BigDecimal.valueOf(450.0));

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(existingRental));
        when(pricingStrategyFactory.get(RateType.DAILY)).thenReturn(pricingStrategy);
        when(pricingStrategy.calculate(eq(car), eq(economy), any(LocalDateTime.class), eq(newPlannedEndAt)))
                .thenReturn(pricingResult);
        when(rentalRepository.save(any(Rental.class))).thenReturn(extendedRental);
        //when
        RentalDtos.RentalDto result = rentalService.extend(rentalId, request);
        //then
        assertNotNull(result);
        assertEquals(rentalId, result.id());
        assertEquals(BigDecimal.valueOf(450.0), result.basePrice());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(pricingStrategyFactory, times(1)).get(RateType.DAILY);
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void testExtendThrowsBadRequestExceptionWhenRentalIsNotActive() {
        //given
        Long rentalId = 1L;

        RentalDtos.RentalExtendRequest request = new RentalDtos.RentalExtendRequest(
                LocalDateTime.of(2026, 1, 10, 10, 0)
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.CANCELED);
        rental.setId(rentalId);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.extend(rentalId, request));

        assertEquals("Only ACTIVE rentals can be extended", exception.getMessage());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(pricingStrategyFactory, never()).get(any());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void testExtendThrowsBadRequestExceptionWhenNewPlannedEndAtIsNotAfterCurrentPlannedEndAt() {
        //given
        Long rentalId = 1L;

        RentalDtos.RentalExtendRequest request = new RentalDtos.RentalExtendRequest(
                LocalDateTime.of(2026, 1, 3, 10, 0)
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental.setId(rentalId);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.extend(rentalId, request));

        assertEquals("newPlannedEndAt must be after current plannedEndAt", exception.getMessage());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(pricingStrategyFactory, never()).get(any());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void testCancelSuccessfullyCancelsActiveRental() {
        //given
        Long rentalId = 1L;

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);
        car.setStatus(CarStatus.RENTED);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental.setId(rentalId);

        Rental canceledRental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.CANCELED);
        canceledRental.setId(rentalId);
        canceledRental.setActualReturnAt(LocalDateTime.now());

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(rentalRepository.save(any(Rental.class))).thenReturn(canceledRental);
        //when
        RentalDtos.RentalDto result = rentalService.cancel(rentalId);
        //then
        assertNotNull(result);
        assertEquals(rentalId, result.id());
        assertEquals(RentalStatus.CANCELED, result.status());
        assertEquals(BigDecimal.ZERO, result.lateFee());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void testCancelThrowsBadRequestExceptionWhenRentalIsNotActive() {
        //given
        Long rentalId = 1L;

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.RETURNED);
        rental.setId(rentalId);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.cancel(rentalId));

        assertEquals("Only ACTIVE rentals can be canceled", exception.getMessage());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(carRepository, never()).save(any(Car.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void testReturnRentalSuccessfullyReturnsRentalOnTime() {
        //given
        Long rentalId = 1L;
        LocalDateTime actualReturnAt = LocalDateTime.of(2026, 1, 4, 10, 0);

        RentalDtos.RentalReturnRequest request = new RentalDtos.RentalReturnRequest(
                actualReturnAt,
                12000
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);
        car.setStatus(CarStatus.RENTED);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental.setId(rentalId);

        Rental returnedRental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.RETURNED);
        returnedRental.setId(rentalId);
        returnedRental.setActualReturnAt(actualReturnAt);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(rentalRepository.save(any(Rental.class))).thenReturn(returnedRental);
        //when
        RentalDtos.RentalDto result = rentalService.returnRental(rentalId, request);
        //then
        assertNotNull(result);
        assertEquals(rentalId, result.id());
        assertEquals(RentalStatus.RETURNED, result.status());
        assertEquals(BigDecimal.ZERO, result.lateFee());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void testReturnRentalSuccessfullyReturnsRentalLateWithLateFee() {
        //given
        Long rentalId = 1L;
        LocalDateTime plannedEndAt = LocalDateTime.of(2026, 1, 5, 10, 0);
        LocalDateTime actualReturnAt = LocalDateTime.of(2026, 1, 5, 13, 0);

        RentalDtos.RentalReturnRequest request = new RentalDtos.RentalReturnRequest(
                actualReturnAt,
                12000
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);
        car.setStatus(CarStatus.RENTED);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                plannedEndAt, RateType.DAILY, RentalStatus.ACTIVE);
        rental.setId(rentalId);

        Rental returnedRental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                plannedEndAt, RateType.DAILY, RentalStatus.RETURNED);
        returnedRental.setId(rentalId);
        returnedRental.setActualReturnAt(actualReturnAt);
        returnedRental.setLateFee(BigDecimal.valueOf(15.0));
        returnedRental.setTotalPrice(BigDecimal.valueOf(215.0));

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(rentalRepository.save(any(Rental.class))).thenReturn(returnedRental);
        //when
        RentalDtos.RentalDto result = rentalService.returnRental(rentalId, request);
        //then
        assertNotNull(result);
        assertEquals(rentalId, result.id());
        assertEquals(RentalStatus.RETURNED, result.status());
        assertTrue(result.lateFee().compareTo(BigDecimal.ZERO) > 0);
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void testReturnRentalUsesCurrentTimeWhenActualReturnAtIsNull() {
        //given
        Long rentalId = 1L;

        RentalDtos.RentalReturnRequest request = new RentalDtos.RentalReturnRequest(
                null,
                12000
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);
        car.setStatus(CarStatus.RENTED);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(1), RateType.DAILY, RentalStatus.ACTIVE);
        rental.setId(rentalId);

        Rental returnedRental = TestDataFactory.rental(customer, car, rental.getStartAt(),
                rental.getPlannedEndAt(), RateType.DAILY, RentalStatus.RETURNED);
        returnedRental.setId(rentalId);
        returnedRental.setActualReturnAt(LocalDateTime.now());

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(rentalRepository.save(any(Rental.class))).thenReturn(returnedRental);
        //when
        RentalDtos.RentalDto result = rentalService.returnRental(rentalId, request);
        //then
        assertNotNull(result);
        assertEquals(rentalId, result.id());
        assertEquals(RentalStatus.RETURNED, result.status());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void testReturnRentalThrowsBadRequestExceptionWhenRentalIsNotActive() {
        //given
        Long rentalId = 1L;

        RentalDtos.RentalReturnRequest request = new RentalDtos.RentalReturnRequest(
                LocalDateTime.of(2026, 1, 5, 10, 0),
                12000
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.CANCELED);
        rental.setId(rentalId);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.returnRental(rentalId, request));

        assertEquals("Only ACTIVE rentals can be returned", exception.getMessage());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(carRepository, never()).save(any(Car.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void testReturnRentalThrowsBadRequestExceptionWhenActualReturnAtIsBeforeStartAt() {
        //given
        Long rentalId = 1L;

        RentalDtos.RentalReturnRequest request = new RentalDtos.RentalReturnRequest(
                LocalDateTime.of(2023, 12, 31, 10, 0),
                12000
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental.setId(rentalId);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.returnRental(rentalId, request));

        assertEquals("actualReturnAt must be after startAt", exception.getMessage());
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(carRepository, never()).save(any(Car.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void testReturnRentalUpdatesCarMileageWhenNewMileageIsProvided() {
        //given
        Long rentalId = 1L;

        RentalDtos.RentalReturnRequest request = new RentalDtos.RentalReturnRequest(
                LocalDateTime.of(2026, 1, 4, 10, 0),
                15000
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);
        car.setStatus(CarStatus.RENTED);
        car.setMileageKm(10000);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental.setId(rentalId);

        Rental returnedRental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.RETURNED);
        returnedRental.setId(rentalId);
        returnedRental.setActualReturnAt(LocalDateTime.of(2026, 1, 4, 10, 0));

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(rentalRepository.save(any(Rental.class))).thenReturn(returnedRental);
        //when
        RentalDtos.RentalDto result = rentalService.returnRental(rentalId, request);
        //then
        assertNotNull(result);
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void testReturnRentalDoesNotUpdateCarMileageWhenNewMileageIsLessThanCurrent() {
        //given
        Long rentalId = 1L;

        RentalDtos.RentalReturnRequest request = new RentalDtos.RentalReturnRequest(
                LocalDateTime.of(2026, 1, 4, 10, 0),
                5000
        );

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);
        car.setStatus(CarStatus.RENTED);
        car.setMileageKm(10000);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental.setId(rentalId);

        Rental returnedRental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.RETURNED);
        returnedRental.setId(rentalId);
        returnedRental.setActualReturnAt(LocalDateTime.of(2026, 1, 4, 10, 0));

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        when(rentalRepository.save(any(Rental.class))).thenReturn(returnedRental);
        //when
        RentalDtos.RentalDto result = rentalService.returnRental(rentalId, request);
        //then
        assertNotNull(result);
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void testDeleteSuccessfullyDeletesActiveRentalAndSetsCarToAvailable() {
        //given
        Long rentalId = 1L;

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);
        car.setStatus(CarStatus.RENTED);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental.setId(rentalId);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(carRepository.save(any(Car.class))).thenReturn(car);
        //when
        rentalService.delete(rentalId);
        //then
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(carRepository, times(1)).save(any(Car.class));
        verify(rentalRepository, times(1)).deleteById(rentalId);
    }

    @Test
    void testDeleteSuccessfullyDeletesNonActiveRentalWithoutChangingCarStatus() {
        //given
        Long rentalId = 1L;

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.RETURNED);
        rental.setId(rentalId);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        //when
        rentalService.delete(rentalId);
        //then
        verify(rentalRepository, times(1)).findById(rentalId);
        verify(carRepository, never()).save(any(Car.class));
        verify(rentalRepository, times(1)).deleteById(rentalId);
    }

    @Test
    void testPreviewPriceReturnsCorrectPricing() {
        //given
        Long carId = 1L;
        RateType rateType = RateType.DAILY;
        LocalDateTime startAt = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime plannedEndAt = LocalDateTime.of(2026, 6, 5, 10, 0);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(carId);

        PricingResult pricingResult = new PricingResult(BigDecimal.valueOf(190.0), BigDecimal.valueOf(5.0));

        when(carService.getEntity(carId)).thenReturn(car);
        when(pricingStrategyFactory.get(rateType)).thenReturn(pricingStrategy);
        when(pricingStrategy.calculate(eq(car), eq(economy), eq(startAt), eq(plannedEndAt)))
                .thenReturn(pricingResult);
        //when
        RentalDtos.RentalPricePreviewResponse result = rentalService.previewPrice(carId, rateType, startAt, plannedEndAt);
        //then
        assertNotNull(result);
        assertEquals(new BigDecimal("190.00"), result.basePrice());
        assertEquals(BigDecimal.valueOf(5.0), result.discountAppliedPercent());
        assertEquals(RateType.DAILY, result.rateType());
        verify(carService, times(1)).getEntity(carId);
        verify(pricingStrategyFactory, times(1)).get(rateType);
        verify(pricingStrategy, times(1)).calculate(eq(car), eq(economy), eq(startAt), eq(plannedEndAt));
    }

    @Test
    void testPreviewPriceThrowsBadRequestExceptionWhenStartAtIsNull() {
        //given
        Long carId = 1L;
        RateType rateType = RateType.DAILY;
        LocalDateTime plannedEndAt = LocalDateTime.of(2026, 6, 5, 10, 0);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.previewPrice(carId, rateType, null, plannedEndAt));

        assertEquals("plannedEndAt must be after startAt", exception.getMessage());
        verify(carService, never()).getEntity(any());
        verify(pricingStrategyFactory, never()).get(any());
    }

    @Test
    void testPreviewPriceThrowsBadRequestExceptionWhenPlannedEndAtIsNull() {
        //given
        Long carId = 1L;
        RateType rateType = RateType.DAILY;
        LocalDateTime startAt = LocalDateTime.of(2026, 6, 1, 10, 0);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.previewPrice(carId, rateType, startAt, null));

        assertEquals("plannedEndAt must be after startAt", exception.getMessage());
        verify(carService, never()).getEntity(any());
        verify(pricingStrategyFactory, never()).get(any());
    }

    @Test
    void testPreviewPriceThrowsBadRequestExceptionWhenPlannedEndAtIsNotAfterStartAt() {
        //given
        Long carId = 1L;
        RateType rateType = RateType.DAILY;
        LocalDateTime startAt = LocalDateTime.of(2026, 6, 5, 10, 0);
        LocalDateTime plannedEndAt = LocalDateTime.of(2026, 6, 1, 10, 0);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> rentalService.previewPrice(carId, rateType, startAt, plannedEndAt));

        assertEquals("plannedEndAt must be after startAt", exception.getMessage());
        verify(carService, never()).getEntity(any());
        verify(pricingStrategyFactory, never()).get(any());
    }
}