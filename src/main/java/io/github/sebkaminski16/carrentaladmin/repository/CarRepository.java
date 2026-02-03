package io.github.sebkaminski16.carrentaladmin.repository;

import io.github.sebkaminski16.carrentaladmin.entity.Car;
import io.github.sebkaminski16.carrentaladmin.entity.CarStatus;
import io.github.sebkaminski16.carrentaladmin.entity.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByStatus(CarStatus status);

    boolean existsByModelId(Long modelId);

    boolean existsByCategoryId(Long categoryId);

    List<Car> findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase(String licensePlate, String vin);

    @Query("""
        select c from Car c
        where c.status = io.github.sebkaminski16.carrentaladmin.entity.CarStatus.AVAILABLE
          and c.id not in (
              select r.car.id from Rental r
              where r.status = :activeStatus
                and (r.startAt < :to and r.plannedEndAt > :from)
          )
        """)
    List<Car> findAvailableBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("activeStatus") RentalStatus activeStatus
    );

    boolean existsByVin(String vin);

    boolean existsByLicensePlate(String licensePlate);

    boolean existsByVinAndIdNot(String vin, Long id);

    boolean existsByLicensePlateAndIdNot(String licensePlate, Long id);
}
