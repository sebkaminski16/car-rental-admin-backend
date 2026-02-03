package io.github.sebkaminski16.carrentaladmin.repository;

import io.github.sebkaminski16.carrentaladmin.entity.Rental;
import io.github.sebkaminski16.carrentaladmin.entity.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByStatusOrderByStartAtDesc(RentalStatus status);

    long countByStatus(RentalStatus status);

    boolean existsByCustomerId(Long customerId);

    boolean existsByCarId(Long carId);

    List<Rental> findByCustomerIdOrderByStartAtDesc(Long customerId);

    List<Rental> findByCarIdOrderByStartAtDesc(Long carId);

    @Query("select r from Rental r where r.status = :status and r.plannedEndAt < :now order by r.plannedEndAt asc")
    List<Rental> findOverdue(@Param("status") RentalStatus status, @Param("now") LocalDateTime now);

    @Query("select count(r) from Rental r where r.status = :status and r.plannedEndAt < :now")
    long countOverdue(@Param("status") RentalStatus status, @Param("now") LocalDateTime now);

    @Query("select max(coalesce(r.actualReturnAt, r.plannedEndAt)) from Rental r where r.customer.id = :customerId")
    Optional<LocalDateTime> findLastRentalEndForCustomer(@Param("customerId") Long customerId);

    @Query("select count(r) from Rental r where r.customer.id = :customerId")
    long countByCustomerId(@Param("customerId") Long customerId);

    @Query("select count(r) from Rental r where r.startAt >= :from and r.startAt < :to")
    long countRentalsStartedBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select sum(r.totalPrice) from Rental r where r.status = io.github.sebkaminski16.carrentaladmin.entity.RentalStatus.RETURNED and r.actualReturnAt >= :from and r.actualReturnAt < :to")
    java.math.BigDecimal sumRevenueBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
