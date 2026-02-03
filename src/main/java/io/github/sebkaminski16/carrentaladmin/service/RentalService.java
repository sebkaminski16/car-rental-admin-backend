package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.entity.*;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.mapper.RentalMapper;
import io.github.sebkaminski16.carrentaladmin.repository.CarRepository;
import io.github.sebkaminski16.carrentaladmin.repository.RentalRepository;
import io.github.sebkaminski16.carrentaladmin.strategy.PricingResult;
import io.github.sebkaminski16.carrentaladmin.strategy.PricingStrategyFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RentalService {

    private final BigDecimal LATE_FEE_HOURLY_PERCENT = new BigDecimal("50");

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CarService carService;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private PricingStrategyFactory pricingStrategyFactory;

    public List<RentalDtos.RentalDto> list() {
        return rentalRepository.findAll().stream().map(RentalMapper::toDto).toList();
    }

    public RentalDtos.RentalDto get(Long id) {
        return RentalMapper.toDto(getEntity(id));
    }

    public Rental getEntity(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rental not found: " + id));
    }

    public List<RentalDtos.RentalDto> listActive() {
        return rentalRepository.findByStatusOrderByStartAtDesc(RentalStatus.ACTIVE)
                .stream()
                .map(RentalMapper::toDto)
                .toList();
    }

    public List<RentalDtos.RentalDto> listOverdue() {
        return rentalRepository.findOverdue(RentalStatus.ACTIVE, LocalDateTime.now())
                .stream()
                .map(RentalMapper::toDto)
                .toList();
    }

    public RentalDtos.RentalDto create(RentalDtos.RentalCreateRequest req) {

        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime start = req.startAt().withSecond(0).withNano(0);

        if (start.isBefore(now)) {
            throw new BadRequestException("startAt must be now or in the future");
        }

        if (!req.plannedEndAt().isAfter(req.startAt())) {
            throw new BadRequestException("plannedEndAt must be after startAt");
        }

        Customer customer = customerService.getEntity(req.customerId());
        Car car = carService.getEntity(req.carId());

        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new BadRequestException("Car is not available (status=" + car.getStatus() + ")");
        }

        PricingResult result = pricingStrategyFactory.get(req.rateType())
                .calculate(car, car.getCategory(), req.startAt(), req.plannedEndAt());

        BigDecimal basePrice = scale2(result.price());
        Rental rental = Rental.builder()
                .customer(customer)
                .car(car)
                .startAt(req.startAt())
                .plannedEndAt(req.plannedEndAt())
                .rateType(req.rateType())
                .status(RentalStatus.ACTIVE)
                .basePrice(basePrice)
                .lateFee(BigDecimal.ZERO)
                .totalPrice(basePrice)
                .notes(req.notes())
                .build();

        car.setStatus(CarStatus.RENTED);

        Rental saved = rentalRepository.save(rental);
        carRepository.save(car);

        return RentalMapper.toDto(saved);
    }

    public RentalDtos.RentalDto update(Long id, RentalDtos.RentalUpdateRequest req) {

        Rental rental = getEntity(id);

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new BadRequestException("Only ACTIVE rentals can be updated");
        }
        if (!req.plannedEndAt().isAfter(rental.getStartAt())) {
            throw new BadRequestException("plannedEndAt must be after startAt");
        }

        rental.setPlannedEndAt(req.plannedEndAt());
        rental.setRateType(req.rateType());
        rental.setNotes(req.notes());

        PricingResult pricing = pricingStrategyFactory.get(req.rateType())
                .calculate(rental.getCar(), rental.getCar().getCategory(), rental.getStartAt(), req.plannedEndAt());

        BigDecimal basePrice = scale2(pricing.price());
        rental.setBasePrice(basePrice);
        rental.setTotalPrice(basePrice.add(rental.getLateFee()));

        return RentalMapper.toDto(rentalRepository.save(rental));
    }

    public RentalDtos.RentalDto extend(Long id, RentalDtos.RentalExtendRequest req) {

        Rental rental = getEntity(id);

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new BadRequestException("Only ACTIVE rentals can be extended");
        }
        if (!req.newPlannedEndAt().isAfter(rental.getPlannedEndAt())) {
            throw new BadRequestException("newPlannedEndAt must be after current plannedEndAt");
        }

        rental.setPlannedEndAt(req.newPlannedEndAt());

        PricingResult pricing = pricingStrategyFactory.get(rental.getRateType())
                .calculate(rental.getCar(), rental.getCar().getCategory(), rental.getStartAt(), rental.getPlannedEndAt());

        BigDecimal basePrice = scale2(pricing.price());
        rental.setBasePrice(basePrice);
        rental.setTotalPrice(basePrice.add(rental.getLateFee()));

        return RentalMapper.toDto(rentalRepository.save(rental));
    }

    public RentalDtos.RentalDto cancel(Long id) {

        Rental rental = getEntity(id);

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new BadRequestException("Only ACTIVE rentals can be canceled");
        }

        rental.setStatus(RentalStatus.CANCELED);
        rental.setActualReturnAt(LocalDateTime.now());
        rental.setLateFee(BigDecimal.ZERO);
        rental.setTotalPrice(rental.getBasePrice());

        Car car = rental.getCar();
        car.setStatus(CarStatus.AVAILABLE);
        carRepository.save(car);

        return RentalMapper.toDto(rentalRepository.save(rental));
    }

    public RentalDtos.RentalDto returnRental(Long id, RentalDtos.RentalReturnRequest req) {

        Rental rental = getEntity(id);

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new BadRequestException("Only ACTIVE rentals can be returned");
        }

        LocalDateTime actualReturn = req.actualReturnAt() != null ? req.actualReturnAt() : LocalDateTime.now();
        if (actualReturn.isBefore(rental.getStartAt())) {
            throw new BadRequestException("actualReturnAt must be after startAt");
        }

        rental.setActualReturnAt(actualReturn);
        rental.setStatus(RentalStatus.RETURNED);

        BigDecimal lateFee = calculateLateFee(rental.getCar(), rental.getPlannedEndAt(), actualReturn);
        rental.setLateFee(lateFee);
        rental.setTotalPrice(scale2(rental.getBasePrice().add(lateFee)));

        Car car = rental.getCar();
        car.setStatus(CarStatus.AVAILABLE);

        if (req.newMileageKm() != null && req.newMileageKm() >= car.getMileageKm()) {
            car.setMileageKm(req.newMileageKm());
        }

        carRepository.save(car);
        Rental saved = rentalRepository.save(rental);

        return RentalMapper.toDto(saved);
    }

    public void delete(Long id) {

        Rental rental = getEntity(id);

        if (rental.getStatus() == RentalStatus.ACTIVE) {
            Car car = rental.getCar();
            car.setStatus(CarStatus.AVAILABLE);
            carRepository.save(car);
        }

        rentalRepository.deleteById(id);
    }

    public RentalDtos.RentalPricePreviewResponse previewPrice(Long carId, RateType rateType, LocalDateTime startAt, LocalDateTime plannedEndAt) {

        if (startAt == null || plannedEndAt == null || !plannedEndAt.isAfter(startAt)) {
            throw new BadRequestException("plannedEndAt must be after startAt");
        }
        Car car = carService.getEntity(carId);

        PricingResult pricing = pricingStrategyFactory.get(rateType)
                .calculate(car, car.getCategory(), startAt, plannedEndAt);

        return new RentalDtos.RentalPricePreviewResponse(
                scale2(pricing.price()),
                pricing.discountPercent(),
                rateType
        );
    }

    private BigDecimal calculateLateFee(Car car, LocalDateTime plannedEndAt, LocalDateTime actualReturnAt) {

        if (!actualReturnAt.isAfter(plannedEndAt)) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        long minutesLate = Duration.between(plannedEndAt, actualReturnAt).toMinutes();
        long hoursLate = (long) Math.ceil(minutesLate / 60.0);
        if (hoursLate <= 0) hoursLate = 1;

        BigDecimal rateMultiplier = LATE_FEE_HOURLY_PERCENT.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        BigDecimal fee = car.getHourlyRate()
                .multiply(BigDecimal.valueOf(hoursLate))
                .multiply(rateMultiplier);

        return scale2(fee);
    }

    private static BigDecimal scale2(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
