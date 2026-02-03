package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.CarDtos;
import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.entity.*;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.mapper.CarMapper;
import io.github.sebkaminski16.carrentaladmin.mapper.RentalMapper;
import io.github.sebkaminski16.carrentaladmin.repository.CarRepository;
import io.github.sebkaminski16.carrentaladmin.repository.RentalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CarModelService carModelService;

    @Autowired
    private CategoryService categoryService;

    public List<CarDtos.CarDto> list() {
        return carRepository.findAll().stream().map(CarMapper::toDto).toList();
    }

    public Car getEntity(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Car not found: " + id));
    }

    public CarDtos.CarDto get(Long id) {
        return CarMapper.toDto(getEntity(id));
    }

    public CarDtos.CarDto create(CarDtos.CarCreateRequest req) {

        if(carRepository.existsByVin(req.vin())) {
            throw new BadRequestException("Car with that vin already exists");
        }

        if(carRepository.existsByLicensePlate(req.licensePlate())) {
            throw new BadRequestException("Car with that license plate already exists");
        }

        CarModel model = carModelService.getEntity(req.modelId());
        Category category = categoryService.getEntity(req.categoryId());

        Car car = Car.builder()
                .vin(req.vin())
                .licensePlate(req.licensePlate())
                .productionYear(req.productionYear())
                .color(req.color())
                .model(model)
                .category(category)
                .imageUrl(req.imageUrl())
                .hourlyRate(req.hourlyRate())
                .dailyRate(req.dailyRate())
                .weeklyRate(req.weeklyRate())
                .mileageKm(req.mileageKm())
                .status(CarStatus.AVAILABLE)
                .build();

        return CarMapper.toDto(carRepository.save(car));
    }

    public CarDtos.CarDto update(Long id, CarDtos.CarUpdateRequest req) {

        if(carRepository.existsByVinAndIdNot(req.vin(), id)) {
            throw new BadRequestException("Car with that vin already exists");
        }

        if(carRepository.existsByLicensePlateAndIdNot(req.licensePlate(), id)) {
            throw new BadRequestException("Car with that license plate already exists");
        }

        Car car = getEntity(id);

        if (car.getStatus() == CarStatus.RENTED && req.status() != CarStatus.RENTED) {
            boolean hasActiveRental = rentalRepository.findByCarIdOrderByStartAtDesc(id)
                    .stream()
                    .anyMatch(r -> r.getStatus() == RentalStatus.ACTIVE);
            if (hasActiveRental) {
                throw new BadRequestException("Car has an active rental. Return/cancel rental first.");
            }
        }

        CarModel model = carModelService.getEntity(req.modelId());
        Category category = categoryService.getEntity(req.categoryId());

        car.setVin(req.vin());
        car.setLicensePlate(req.licensePlate());
        car.setProductionYear(req.productionYear());
        car.setColor(req.color());
        car.setModel(model);
        car.setCategory(category);
        car.setImageUrl(req.imageUrl());
        car.setHourlyRate(req.hourlyRate());
        car.setDailyRate(req.dailyRate());
        car.setWeeklyRate(req.weeklyRate());
        car.setMileageKm(req.mileageKm());
        car.setStatus(req.status());

        return CarMapper.toDto(carRepository.save(car));
    }

    public void delete(Long id) {

        if(!carRepository.existsById(id)) {
            throw new NotFoundException("Car not found: " + id);
        }
        if(rentalRepository.existsByCarId(id)) {
            throw new BadRequestException("Cannot delete, because a Rental with that car exists!");
        }

        carRepository.deleteById(id);
    }

    public List<CarDtos.CarDto> search(String query) {
        if (query == null || query.isBlank()) return list();
        return carRepository.findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase(query, query)
                .stream()
                .map(CarMapper::toDto)
                .toList();
    }

    public List<CarDtos.CarDto> listByStatus(CarStatus status) {
        return carRepository.findByStatus(status).stream().map(CarMapper::toDto).toList();
    }

    public List<CarDtos.CarDto> availableBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null || !to.isAfter(from)) {
            throw new BadRequestException("'to' must be after 'from'");
        }
        return carRepository.findAvailableBetween(from, to, RentalStatus.ACTIVE)
                .stream()
                .map(CarMapper::toDto)
                .toList();
    }

    public List<RentalDtos.RentalDto> getRentals(Long carId) {

        if(!carRepository.existsById(carId)) {
            throw new NotFoundException("Car not found: " + carId);
        }

        return rentalRepository.findByCarIdOrderByStartAtDesc(carId)
                .stream()
                .map(RentalMapper::toDto)
                .toList();
    }
}
