package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.CarDtos;
import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.entity.*;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.repository.CarRepository;
import io.github.sebkaminski16.carrentaladmin.repository.RentalRepository;
import io.github.sebkaminski16.carrentaladmin.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTests {

    @Mock
    private CarRepository carRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarModelService carModelService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CarService carService;

    @Test
    void testListReturnsAllCars() {
        //given
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);

        Car car1 = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car1.setId(1L);
        Car car2 = TestDataFactory.car("VIN456", "XYZ789", corolla, economy,
                BigDecimal.valueOf(12.0), BigDecimal.valueOf(60.0), BigDecimal.valueOf(350.0));
        car2.setId(2L);

        when(carRepository.findAll()).thenReturn(Arrays.asList(car1, car2));
        //when
        List<CarDtos.CarDto> result = carService.list();
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ABC123", result.get(0).licensePlate());
        assertEquals("XYZ789", result.get(1).licensePlate());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void testListReturnsEmptyListWhenNoCars() {
        //given
        when(carRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<CarDtos.CarDto> result = carService.list();
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void testGetEntityReturnsCarWhenExists() {
        //given
        Long carId = 1L;
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);

        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(carId);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        //when
        Car result = carService.getEntity(carId);
        //then
        assertNotNull(result);
        assertEquals(carId, result.getId());
        assertEquals("ABC123", result.getLicensePlate());
        verify(carRepository, times(1)).findById(carId);
    }

    @Test
    void testGetEntityThrowsNotFoundExceptionWhenCarDoesNotExist() {
        //given
        Long carId = 1L;
        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carService.getEntity(carId));

        assertEquals("Car not found: 1", exception.getMessage());
        verify(carRepository, times(1)).findById(carId);
    }

    @Test
    void testGetReturnsCarDtoWhenExists() {
        //given
        Long carId = 1L;
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);

        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(carId);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        //when
        CarDtos.CarDto result = carService.get(carId);
        //then
        assertNotNull(result);
        assertEquals(carId, result.id());
        assertEquals("ABC123", result.licensePlate());
        assertEquals("VIN123", result.vin());
        verify(carRepository, times(1)).findById(carId);
    }

    @Test
    void testGetThrowsNotFoundExceptionWhenCarDoesNotExist() {
        //given
        Long carId = 1L;
        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carService.get(carId));

        assertEquals("Car not found: 1", exception.getMessage());
        verify(carRepository, times(1)).findById(carId);
    }

    @Test
    void testCreateSuccessfullyCreatesNewCar() {
        //given
        Long modelId = 1L;
        Long categoryId = 1L;

        CarDtos.CarCreateRequest request = new CarDtos.CarCreateRequest(
                "VIN123",
                "ABC123",
                2020,
                "black",
                modelId,
                categoryId,
                "http://example.com/image.jpg",
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(50.0),
                BigDecimal.valueOf(300.0),
                10000
        );

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(modelId);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(categoryId);

        Car savedCar = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        savedCar.setId(1L);

        when(carRepository.existsByVin("VIN123")).thenReturn(false);
        when(carRepository.existsByLicensePlate("ABC123")).thenReturn(false);
        when(carModelService.getEntity(modelId)).thenReturn(corolla);
        when(categoryService.getEntity(categoryId)).thenReturn(economy);
        when(carRepository.save(any(Car.class))).thenReturn(savedCar);
        //when
        CarDtos.CarDto result = carService.create(request);
        //hen
        assertNotNull(result);
        assertEquals(Long.valueOf(1), result.id());
        assertEquals("ABC123", result.licensePlate());
        assertEquals("VIN123", result.vin());
        assertEquals(CarStatus.AVAILABLE, result.status());
        verify(carRepository, times(1)).existsByVin("VIN123");
        verify(carRepository, times(1)).existsByLicensePlate("ABC123");
        verify(carModelService, times(1)).getEntity(modelId);
        verify(categoryService, times(1)).getEntity(categoryId);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenVinAlreadyExists() {
        // given
        CarDtos.CarCreateRequest request = new CarDtos.CarCreateRequest(
                "VIN123",
                "ABC123",
                2020,
                "black",
                1L,
                1L,
                "http://example.com/image.jpg",
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(50.0),
                BigDecimal.valueOf(300.0),
                10000
        );

        when(carRepository.existsByVin("VIN123")).thenReturn(true);

        // when & then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carService.create(request));

        assertEquals("Car with that vin already exists", exception.getMessage());
        verify(carRepository, times(1)).existsByVin("VIN123");
        verify(carRepository, never()).existsByLicensePlate(any());
        verify(carModelService, never()).getEntity(any());
        verify(categoryService, never()).getEntity(any());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenLicensePlateAlreadyExists() {
        //given
        CarDtos.CarCreateRequest request = new CarDtos.CarCreateRequest(
                "VIN123",
                "ABC123",
                2020,
                "black",
                1L,
                1L,
                "http://example.com/image.jpg",
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(50.0),
                BigDecimal.valueOf(300.0),
                10000
        );

        when(carRepository.existsByVin("VIN123")).thenReturn(false);
        when(carRepository.existsByLicensePlate("ABC123")).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carService.create(request));

        assertEquals("Car with that license plate already exists", exception.getMessage());
        verify(carRepository, times(1)).existsByVin("VIN123");
        verify(carRepository, times(1)).existsByLicensePlate("ABC123");
        verify(carModelService, never()).getEntity(any());
        verify(categoryService, never()).getEntity(any());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void testCreateThrowsNotFoundExceptionWhenModelDoesNotExist() {
        //given
        Long modelId = 1L;
        Long categoryId = 1L;

        CarDtos.CarCreateRequest request = new CarDtos.CarCreateRequest(
                "VIN123",
                "ABC123",
                2020,
                "black",
                modelId,
                categoryId,
                "http://example.com/image.jpg",
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(50.0),
                BigDecimal.valueOf(300.0),
                10000
        );

        when(carRepository.existsByVin("VIN123")).thenReturn(false);
        when(carRepository.existsByLicensePlate("ABC123")).thenReturn(false);
        when(carModelService.getEntity(modelId)).thenThrow(new NotFoundException("Model not found: 1"));
        //whe&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carService.create(request));

        assertEquals("Model not found: 1", exception.getMessage());
        verify(carRepository, times(1)).existsByVin("VIN123");
        verify(carRepository, times(1)).existsByLicensePlate("ABC123");
        verify(carModelService, times(1)).getEntity(modelId);
        verify(categoryService, never()).getEntity(any());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void testCreateThrowsNotFoundExceptionWhenCategoryDoesNotExist() {
        //given
        Long modelId = 1L;
        Long categoryId = 1L;

        CarDtos.CarCreateRequest request = new CarDtos.CarCreateRequest(
                "VIN123",
                "ABC123",
                2020,
                "black",
                modelId,
                categoryId,
                "http://example.com/image.jpg",
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(50.0),
                BigDecimal.valueOf(300.0),
                10000
        );

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(modelId);

        when(carRepository.existsByVin("VIN123")).thenReturn(false);
        when(carRepository.existsByLicensePlate("ABC123")).thenReturn(false);
        when(carModelService.getEntity(modelId)).thenReturn(corolla);
        when(categoryService.getEntity(categoryId)).thenThrow(new NotFoundException("Category not found: 1"));
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carService.create(request));

        assertEquals("Category not found: 1", exception.getMessage());
        verify(carRepository, times(1)).existsByVin("VIN123");
        verify(carRepository, times(1)).existsByLicensePlate("ABC123");
        verify(carModelService, times(1)).getEntity(modelId);
        verify(categoryService, times(1)).getEntity(categoryId);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void testUpdateSuccessfullyUpdatesCar() {
        //given
        Long carId = 1L;
        Long modelId = 1L;
        Long categoryId = 1L;

        CarDtos.CarUpdateRequest request = new CarDtos.CarUpdateRequest(
                "VIN123Updated",
                "ABC123Updated",
                2021,
                "red",
                modelId,
                categoryId,
                "http://example.com/new-image.jpg",
                BigDecimal.valueOf(12.0),
                BigDecimal.valueOf(60.0),
                BigDecimal.valueOf(350.0),
                15000,
                CarStatus.AVAILABLE
        );

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(modelId);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(categoryId);

        Car existingCar = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        existingCar.setId(carId);

        Car updatedCar = TestDataFactory.car("VIN123Updated", "ABC123Updated", corolla, economy,
                BigDecimal.valueOf(12.0), BigDecimal.valueOf(60.0), BigDecimal.valueOf(350.0));
        updatedCar.setId(carId);

        when(carRepository.existsByVinAndIdNot("VIN123Updated", carId)).thenReturn(false);
        when(carRepository.existsByLicensePlateAndIdNot("ABC123Updated", carId)).thenReturn(false);
        when(carRepository.findById(carId)).thenReturn(Optional.of(existingCar));
        when(carModelService.getEntity(modelId)).thenReturn(corolla);
        when(categoryService.getEntity(categoryId)).thenReturn(economy);
        when(carRepository.save(any(Car.class))).thenReturn(updatedCar);
        //when
        CarDtos.CarDto result = carService.update(carId, request);
        //then
        assertNotNull(result);
        assertEquals(carId, result.id());
        assertEquals("ABC123Updated", result.licensePlate());
        assertEquals("VIN123Updated", result.vin());
        verify(carRepository, times(1)).existsByVinAndIdNot("VIN123Updated", carId);
        verify(carRepository, times(1)).existsByLicensePlateAndIdNot("ABC123Updated", carId);
        verify(carRepository, times(1)).findById(carId);
        verify(carModelService, times(1)).getEntity(modelId);
        verify(categoryService, times(1)).getEntity(categoryId);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testUpdateThrowsNotFoundExceptionWhenCarDoesNotExist() {
        //given
        Long carId = 1L;

        CarDtos.CarUpdateRequest request = new CarDtos.CarUpdateRequest(
                "VIN123Updated",
                "ABC123Updated",
                2021,
                "red",
                1L,
                1L,
                "http://example.com/new-image.jpg",
                BigDecimal.valueOf(12.0),
                BigDecimal.valueOf(60.0),
                BigDecimal.valueOf(350.0),
                15000,
                CarStatus.AVAILABLE
        );

        when(carRepository.existsByVinAndIdNot("VIN123Updated", carId)).thenReturn(false);
        when(carRepository.existsByLicensePlateAndIdNot("ABC123Updated", carId)).thenReturn(false);
        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carService.update(carId, request));

        assertEquals("Car not found: 1", exception.getMessage());
        verify(carRepository, times(1)).existsByVinAndIdNot("VIN123Updated", carId);
        verify(carRepository, times(1)).existsByLicensePlateAndIdNot("ABC123Updated", carId);
        verify(carRepository, times(1)).findById(carId);
        verify(carModelService, never()).getEntity(any());
        verify(categoryService, never()).getEntity(any());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void testUpdateThrowsBadRequestExceptionWhenVinAlreadyExistsForDifferentCar() {
        //given
        Long carId = 1L;

        CarDtos.CarUpdateRequest request = new CarDtos.CarUpdateRequest(
                "VIN999",
                "ABC123Updated",
                2021,
                "red",
                1L,
                1L,
                "http://example.com/new-image.jpg",
                BigDecimal.valueOf(12.0),
                BigDecimal.valueOf(60.0),
                BigDecimal.valueOf(350.0),
                15000,
                CarStatus.AVAILABLE
        );

        when(carRepository.existsByVinAndIdNot("VIN999", carId)).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carService.update(carId, request));

        assertEquals("Car with that vin already exists", exception.getMessage());
        verify(carRepository, times(1)).existsByVinAndIdNot("VIN999", carId);
        verify(carRepository, never()).existsByLicensePlateAndIdNot(any(), any());
        verify(carRepository, never()).findById(any());
        verify(carModelService, never()).getEntity(any());
        verify(categoryService, never()).getEntity(any());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void testUpdateThrowsBadRequestExceptionWhenLicensePlateAlreadyExistsForDifferentCar() {
        //given
        Long carId = 1L;

        CarDtos.CarUpdateRequest request = new CarDtos.CarUpdateRequest(
                "VIN123Updated",
                "XYZ999",
                2021,
                "red",
                1L,
                1L,
                "http://example.com/new-image.jpg",
                BigDecimal.valueOf(12.0),
                BigDecimal.valueOf(60.0),
                BigDecimal.valueOf(350.0),
                15000,
                CarStatus.AVAILABLE
        );

        when(carRepository.existsByVinAndIdNot("VIN123Updated", carId)).thenReturn(false);
        when(carRepository.existsByLicensePlateAndIdNot("XYZ999", carId)).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carService.update(carId, request));

        assertEquals("Car with that license plate already exists", exception.getMessage());
        verify(carRepository, times(1)).existsByVinAndIdNot("VIN123Updated", carId);
        verify(carRepository, times(1)).existsByLicensePlateAndIdNot("XYZ999", carId);
        verify(carRepository, never()).findById(any());
        verify(carModelService, never()).getEntity(any());
        verify(categoryService, never()).getEntity(any());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void testUpdateThrowsBadRequestExceptionWhenChangingFromRentedStatusWithActiveRental() {
        //given
        Long carId = 1L;
        Long modelId = 1L;
        Long categoryId = 1L;

        CarDtos.CarUpdateRequest request = new CarDtos.CarUpdateRequest(
                "VIN123",
                "ABC123",
                2000,
                "black",
                modelId,
                categoryId,
                "http://example.com/image.jpg",
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(50.0),
                BigDecimal.valueOf(300.0),
                10000,
                CarStatus.AVAILABLE
        );

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(modelId);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(categoryId);

        Car existingCar = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        existingCar.setId(carId);
        existingCar.setStatus(CarStatus.RENTED);

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Rental activeRental = TestDataFactory.rental(customer, existingCar, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2), RateType.DAILY, RentalStatus.ACTIVE);
        activeRental.setId(1L);

        when(carRepository.existsByVinAndIdNot("VIN123", carId)).thenReturn(false);
        when(carRepository.existsByLicensePlateAndIdNot("ABC123", carId)).thenReturn(false);
        when(carRepository.findById(carId)).thenReturn(Optional.of(existingCar));
        when(rentalRepository.findByCarIdOrderByStartAtDesc(carId))
                .thenReturn(Collections.singletonList(activeRental));
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carService.update(carId, request));

        assertEquals("Car has an active rental. Return/cancel rental first.", exception.getMessage());
        verify(carRepository, times(1)).existsByVinAndIdNot("VIN123", carId);
        verify(carRepository, times(1)).existsByLicensePlateAndIdNot("ABC123", carId);
        verify(carRepository, times(1)).findById(carId);
        verify(rentalRepository, times(1)).findByCarIdOrderByStartAtDesc(carId);
        verify(carModelService, never()).getEntity(any());
        verify(categoryService, never()).getEntity(any());
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void testUpdateAllowsChangingFromRentedStatusWhenNoActiveRental() {
        //given
        Long carId = 1L;
        Long modelId = 1L;
        Long categoryId = 1L;

        CarDtos.CarUpdateRequest request = new CarDtos.CarUpdateRequest(
                "VIN123",
                "ABC123",
                2020,
                "black",
                modelId,
                categoryId,
                "http://example.com/image.jpg",
                BigDecimal.valueOf(10.0),
                BigDecimal.valueOf(50.0),
                BigDecimal.valueOf(300.0),
                10000,
                CarStatus.AVAILABLE
        );

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(modelId);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(categoryId);

        Car existingCar = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        existingCar.setId(carId);
        existingCar.setStatus(CarStatus.RENTED);

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Rental returnedRental = TestDataFactory.rental(customer, existingCar, LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(1), RateType.DAILY, RentalStatus.RETURNED);
        returnedRental.setId(1L);

        Car updatedCar = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        updatedCar.setId(carId);
        updatedCar.setStatus(CarStatus.AVAILABLE);

        when(carRepository.existsByVinAndIdNot("VIN123", carId)).thenReturn(false);
        when(carRepository.existsByLicensePlateAndIdNot("ABC123", carId)).thenReturn(false);
        when(carRepository.findById(carId)).thenReturn(Optional.of(existingCar));
        when(rentalRepository.findByCarIdOrderByStartAtDesc(carId))
                .thenReturn(Collections.singletonList(returnedRental));
        when(carModelService.getEntity(modelId)).thenReturn(corolla);
        when(categoryService.getEntity(categoryId)).thenReturn(economy);
        when(carRepository.save(any(Car.class))).thenReturn(updatedCar);
        //when
        CarDtos.CarDto result = carService.update(carId, request);
        //then
        assertNotNull(result);
        assertEquals(carId, result.id());
        assertEquals(CarStatus.AVAILABLE, result.status());
        verify(carRepository, times(1)).existsByVinAndIdNot("VIN123", carId);
        verify(carRepository, times(1)).existsByLicensePlateAndIdNot("ABC123", carId);
        verify(carRepository, times(1)).findById(carId);
        verify(rentalRepository, times(1)).findByCarIdOrderByStartAtDesc(carId);
        verify(carModelService, times(1)).getEntity(modelId);
        verify(categoryService, times(1)).getEntity(categoryId);
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void testDeleteSuccessfullyDeletesCar() {
        //given
        Long carId = 1L;

        when(carRepository.existsById(carId)).thenReturn(true);
        when(rentalRepository.existsByCarId(carId)).thenReturn(false);
        //when
        carService.delete(carId);
        //then
        verify(carRepository, times(1)).existsById(carId);
        verify(rentalRepository, times(1)).existsByCarId(carId);
        verify(carRepository, times(1)).deleteById(carId);
    }

    @Test
    void testDeleteThrowsNotFoundExceptionWhenCarDoesNotExist() {
        //given
        Long carId = 1L;
        when(carRepository.existsById(carId)).thenReturn(false);
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carService.delete(carId));

        assertEquals("Car not found: 1", exception.getMessage());
        verify(carRepository, times(1)).existsById(carId);
        verify(rentalRepository, never()).existsByCarId(any());
        verify(carRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteThrowsBadRequestExceptionWhenRentalsExist() {
        //given
        Long carId = 1L;

        when(carRepository.existsById(carId)).thenReturn(true);
        when(rentalRepository.existsByCarId(carId)).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carService.delete(carId));

        assertEquals("Cannot delete, because a Rental with that car exists!", exception.getMessage());
        verify(carRepository, times(1)).existsById(carId);
        verify(rentalRepository, times(1)).existsByCarId(carId);
        verify(carRepository, never()).deleteById(any());
    }

    @Test
    void testSearchReturnsMatchingCarsByLicensePlate() {
        //given
        String query = "abc";
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);

        Car car1 = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car1.setId(1L);

        when(carRepository.findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase(query, query))
                .thenReturn(Collections.singletonList(car1));
        //when
        List<CarDtos.CarDto> result = carService.search(query);
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ABC123", result.getFirst().licensePlate());
        verify(carRepository, times(1)).findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase(query, query);
        verify(carRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsMatchingCarsByVin() {
        //given
        String query = "vin";
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);

        Car car1 = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car1.setId(1L);
        Car car2 = TestDataFactory.car("VIN456", "XYZ789", corolla, economy,
                BigDecimal.valueOf(12.0), BigDecimal.valueOf(60.0), BigDecimal.valueOf(350.0));
        car2.setId(2L);

        when(carRepository.findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase(query, query))
                .thenReturn(Arrays.asList(car1, car2));
        //when
        List<CarDtos.CarDto> result = carService.search(query);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("VIN123", result.get(0).vin());
        assertEquals("VIN456", result.get(1).vin());
        verify(carRepository, times(1)).findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase(query, query);
        verify(carRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsEmptyListWhenNoMatches() {
        //given
        String query = "xyz";
        when(carRepository.findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase(query, query))
                .thenReturn(Collections.emptyList());
        //when
        List<CarDtos.CarDto> result = carService.search(query);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carRepository, times(1)).findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase(query, query);
        verify(carRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsAllCarsWhenQueryIsNull() {
        //given
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);

        Car car1 = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car1.setId(1L);
        Car car2 = TestDataFactory.car("VIN456", "XYZ789", corolla, economy,
                BigDecimal.valueOf(12.0), BigDecimal.valueOf(60.0), BigDecimal.valueOf(350.0));
        car2.setId(2L);

        when(carRepository.findAll()).thenReturn(Arrays.asList(car1, car2));
        //when
        List<CarDtos.CarDto> result = carService.search(null);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(carRepository, times(1)).findAll();
        verify(carRepository, never()).findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase(any(), any());
    }

    @Test
    void testSearchReturnsAllCarsWhenQueryIsBlank() {
        //given
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);

        Car car1 = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car1.setId(1L);
        Car car2 = TestDataFactory.car("VIN456", "XYZ789", corolla, economy,
                BigDecimal.valueOf(12.0), BigDecimal.valueOf(60.0), BigDecimal.valueOf(350.0));
        car2.setId(2L);

        when(carRepository.findAll()).thenReturn(Arrays.asList(car1, car2));
        //when
        List<CarDtos.CarDto> result = carService.search("   ");
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(carRepository, times(1)).findAll();
        verify(carRepository, never()).findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase(any(), any());
    }

    @Test
    void testSearchReturnsAllCarsWhenQueryIsEmpty() {
        //given
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);

        Car car1 = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car1.setId(1L);
        Car car2 = TestDataFactory.car("VIN456", "XYZ789", corolla, economy,
                BigDecimal.valueOf(12.0), BigDecimal.valueOf(60.0), BigDecimal.valueOf(350.0));
        car2.setId(2L);

        when(carRepository.findAll()).thenReturn(Arrays.asList(car1, car2));
        //when
        List<CarDtos.CarDto> result = carService.search("");
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(carRepository, times(1)).findAll();
        verify(carRepository, never()).findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase(any(), any());
    }

    @Test
    void testListByStatusReturnsCarsByStatus() {
        //given
        CarStatus status = CarStatus.AVAILABLE;
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);

        Car car1 = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car1.setId(1L);
        car1.setStatus(CarStatus.AVAILABLE);

        Car car2 = TestDataFactory.car("VIN456", "XYZ789", corolla, economy,
                BigDecimal.valueOf(12.0), BigDecimal.valueOf(60.0), BigDecimal.valueOf(350.0));
        car2.setId(2L);
        car2.setStatus(CarStatus.AVAILABLE);

        when(carRepository.findByStatus(status)).thenReturn(Arrays.asList(car1, car2));
        //when
        List<CarDtos.CarDto> result = carService.listByStatus(status);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(CarStatus.AVAILABLE, result.get(0).status());
        assertEquals(CarStatus.AVAILABLE, result.get(1).status());
        verify(carRepository, times(1)).findByStatus(status);
    }

    @Test
    void testListByStatusReturnsEmptyListWhenNoCarsWithStatus() {
        //given
        CarStatus status = CarStatus.MAINTENANCE;
        when(carRepository.findByStatus(status)).thenReturn(Collections.emptyList());
        //when
        List<CarDtos.CarDto> result = carService.listByStatus(status);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carRepository, times(1)).findByStatus(status);
    }

    @Test
    void testAvailableBetweenReturnsAvailableCars() {
        //given
        LocalDateTime from = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 6, 5, 10, 0);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);

        Car car1 = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car1.setId(1L);
        car1.setStatus(CarStatus.AVAILABLE);

        when(carRepository.findAvailableBetween(from, to, RentalStatus.ACTIVE))
                .thenReturn(Collections.singletonList(car1));
        //when
        List<CarDtos.CarDto> result = carService.availableBetween(from, to);
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ABC123", result.getFirst().licensePlate());
        verify(carRepository, times(1)).findAvailableBetween(from, to, RentalStatus.ACTIVE);
    }

    @Test
    void testAvailableBetweenReturnsEmptyListWhenNoCarsAvailable() {
        //given
        LocalDateTime from = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 6, 5, 10, 0);

        when(carRepository.findAvailableBetween(from, to, RentalStatus.ACTIVE))
                .thenReturn(Collections.emptyList());
        //when
        List<CarDtos.CarDto> result = carService.availableBetween(from, to);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carRepository, times(1)).findAvailableBetween(from, to, RentalStatus.ACTIVE);
    }

    @Test
    void testAvailableBetweenThrowsBadRequestExceptionWhenFromIsNull() {
        //given
        LocalDateTime to = LocalDateTime.of(2026, 6, 5, 10, 0);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carService.availableBetween(null, to));

        assertEquals("'to' must be after 'from'", exception.getMessage());
        verify(carRepository, never()).findAvailableBetween(any(), any(), any());
    }

    @Test
    void testAvailableBetweenThrowsBadRequestExceptionWhenToIsNull() {
        //given
        LocalDateTime from = LocalDateTime.of(2026, 6, 1, 10, 0);

        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carService.availableBetween(from, null));

        assertEquals("'to' must be after 'from'", exception.getMessage());
        verify(carRepository, never()).findAvailableBetween(any(), any(), any());
    }

    @Test
    void testAvailableBetweenThrowsBadRequestExceptionWhenToIsNotAfterFrom() {
        //given
        LocalDateTime from = LocalDateTime.of(2026, 6, 5, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 6, 1, 10, 0);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carService.availableBetween(from, to));

        assertEquals("'to' must be after 'from'", exception.getMessage());
        verify(carRepository, never()).findAvailableBetween(any(), any(), any());
    }

    @Test
    void testAvailableBetweenThrowsBadRequestExceptionWhenToEqualsFrom() {
        //given
        LocalDateTime from = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 6, 1, 10, 0);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carService.availableBetween(from, to));

        assertEquals("'to' must be after 'from'", exception.getMessage());
        verify(carRepository, never()).findAvailableBetween(any(), any(), any());
    }

    @Test
    void testGetRentalsReturnsRentalsForCar() {
        //given
        Long carId = 1L;

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(carId);

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(1L);

        Rental rental1 = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.RETURNED);
        rental1.setId(1L);

        Rental rental2 = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 2, 1, 10, 0),
                LocalDateTime.of(2026, 2, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental2.setId(2L);

        when(carRepository.existsById(carId)).thenReturn(true);
        when(rentalRepository.findByCarIdOrderByStartAtDesc(carId))
                .thenReturn(Arrays.asList(rental2, rental1));
        //when
        List<RentalDtos.RentalDto> result = carService.getRentals(carId);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.valueOf(2), result.get(0).id());
        assertEquals(RentalStatus.ACTIVE, result.get(0).status());
        assertEquals(Long.valueOf(1), result.get(1).id());
        assertEquals(RentalStatus.RETURNED, result.get(1).status());
        verify(carRepository, times(1)).existsById(carId);
        verify(rentalRepository, times(1)).findByCarIdOrderByStartAtDesc(carId);
    }

    @Test
    void testGetRentalsReturnsEmptyListWhenNoRentals() {
        //given
        Long carId = 1L;

        when(carRepository.existsById(carId)).thenReturn(true);
        when(rentalRepository.findByCarIdOrderByStartAtDesc(carId))
                .thenReturn(Collections.emptyList());
        //when
        List<RentalDtos.RentalDto> result = carService.getRentals(carId);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carRepository, times(1)).existsById(carId);
        verify(rentalRepository, times(1)).findByCarIdOrderByStartAtDesc(carId);
    }

    @Test
    void testGetRentalsThrowsNotFoundExceptionWhenCarDoesNotExist() {
        //given
        Long carId = 1L;
        when(carRepository.existsById(carId)).thenReturn(false);
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carService.getRentals(carId));

        assertEquals("Car not found: 1", exception.getMessage());
        verify(carRepository, times(1)).existsById(carId);
        verify(rentalRepository, never()).findByCarIdOrderByStartAtDesc(any());
    }
}