package io.github.sebkaminski16.carrentaladmin.repository;

import io.github.sebkaminski16.carrentaladmin.entity.*;
import io.github.sebkaminski16.carrentaladmin.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RentalRepositoryTests {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CarModelRepository carModelRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testSaveRental() {
        //given
        Customer customer = TestDataFactory.customer("customer@example.com");
        Customer savedCustomer = customerRepository.save(customer);

        Brand brand = TestDataFactory.brand("Toyota");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Camry", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Sedan", BigDecimal.valueOf(5), BigDecimal.valueOf(10));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN123", "ABC123", savedModel, savedCategory,
                BigDecimal.valueOf(10), BigDecimal.valueOf(50), BigDecimal.valueOf(300));
        Car savedCar = carRepository.save(car);

        LocalDateTime startAt = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime plannedEndAt = LocalDateTime.of(2026, 3, 5, 10, 0);
        Rental rental = TestDataFactory.rental(savedCustomer, savedCar, startAt, plannedEndAt,
                RateType.DAILY, RentalStatus.ACTIVE);
        //when
        Rental saved = rentalRepository.save(rental);
        //then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCustomer().getId()).isEqualTo(savedCustomer.getId());
        assertThat(saved.getCar().getId()).isEqualTo(savedCar.getId());
        assertThat(saved.getStatus()).isEqualTo(RentalStatus.ACTIVE);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindById() {
        //given
        Customer customer = TestDataFactory.customer("find@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car = createAndSaveCar("VIN456", "XYZ456");
        LocalDateTime startAt = LocalDateTime.of(2026, 3, 10, 10, 0);
        LocalDateTime plannedEndAt = LocalDateTime.of(2026, 3, 15, 10, 0);
        Rental rental = TestDataFactory.rental(savedCustomer, car, startAt, plannedEndAt,
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental saved = rentalRepository.save(rental);
        //when
        Optional<Rental> found = rentalRepository.findById(saved.getId());
        //then
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(RentalStatus.ACTIVE);
    }

    @Test
    void testFindByIdNotFound() {
        // given
        Long nonExistentId = 999L;
        //when
        Optional<Rental> found = rentalRepository.findById(nonExistentId);
        //then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindAll() {
        //given
        Customer customer = TestDataFactory.customer("all@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car1 = createAndSaveCar("VIN111", "AAA111");
        Car car2 = createAndSaveCar("VIN222", "BBB222");

        LocalDateTime start1 = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime end1 = LocalDateTime.of(2026, 3, 5, 10, 0);
        Rental rental1 = TestDataFactory.rental(savedCustomer, car1, start1, end1,
                RateType.DAILY, RentalStatus.ACTIVE);

        LocalDateTime start2 = LocalDateTime.of(2026, 3, 10, 10, 0);
        LocalDateTime end2 = LocalDateTime.of(2026, 3, 15, 10, 0);
        Rental rental2 = TestDataFactory.rental(savedCustomer, car2, start2, end2,
                RateType.WEEKLY, RentalStatus.RETURNED);

        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        //when
        List<Rental> rentals = rentalRepository.findAll();
        //then
        assertThat(rentals).hasSize(2);
    }

    @Test
    void testUpdateRental() {
        //given
        Customer customer = TestDataFactory.customer("update@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car = createAndSaveCar("VIN333", "CCC333");
        LocalDateTime startAt = LocalDateTime.of(2026, 3, 20, 10, 0);
        LocalDateTime plannedEndAt = LocalDateTime.of(2026, 3, 25, 10, 0);
        Rental rental = TestDataFactory.rental(savedCustomer, car, startAt, plannedEndAt,
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental saved = rentalRepository.save(rental);

        saved.setStatus(RentalStatus.RETURNED);
        saved.setActualReturnAt(LocalDateTime.of(2026, 3, 24, 10, 0));
        //when
        Rental updated = rentalRepository.save(saved);
        //then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getStatus()).isEqualTo(RentalStatus.RETURNED);
        assertThat(updated.getActualReturnAt()).isNotNull();
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(updated.getCreatedAt());
    }

    @Test
    void testDeleteRental() {
        //given
        Customer customer = TestDataFactory.customer("delete@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car = createAndSaveCar("VIN444", "DDD444");
        LocalDateTime startAt = LocalDateTime.of(2026, 4, 1, 10, 0);
        LocalDateTime plannedEndAt = LocalDateTime.of(2026, 4, 5, 10, 0);
        Rental rental = TestDataFactory.rental(savedCustomer, car, startAt, plannedEndAt,
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental saved = rentalRepository.save(rental);
        Long rentalId = saved.getId();
        //when
        rentalRepository.delete(saved);
        //then
        Optional<Rental> found = rentalRepository.findById(rentalId);
        assertThat(found).isEmpty();
    }

    @Test
    void testDeleteById() {
        //given
        Customer customer = TestDataFactory.customer("deletebyid@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car = createAndSaveCar("VIN555", "EEE555");
        LocalDateTime startAt = LocalDateTime.of(2026, 4, 10, 10, 0);
        LocalDateTime plannedEndAt = LocalDateTime.of(2026, 4, 15, 10, 0);
        Rental rental = TestDataFactory.rental(savedCustomer, car, startAt, plannedEndAt,
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental saved = rentalRepository.save(rental);
        Long rentalId = saved.getId();
        //when
        rentalRepository.deleteById(rentalId);
        //then
        Optional<Rental> found = rentalRepository.findById(rentalId);
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByStatusOrderByStartAtDesc() {
        //given
        Customer customer = TestDataFactory.customer("status@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car1 = createAndSaveCar("VIN666", "FFF666");
        Car car2 = createAndSaveCar("VIN777", "GGG777");
        Car car3 = createAndSaveCar("VIN888", "HHH888");

        LocalDateTime start1 = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2026, 3, 10, 10, 0);
        LocalDateTime start3 = LocalDateTime.of(2026, 3, 5, 10, 0);

        Rental rental1 = TestDataFactory.rental(savedCustomer, car1, start1, start1.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental rental2 = TestDataFactory.rental(savedCustomer, car2, start2, start2.plusDays(4),
                RateType.DAILY, RentalStatus.RETURNED);
        Rental rental3 = TestDataFactory.rental(savedCustomer, car3, start3, start3.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);

        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        rentalRepository.save(rental3);
        //when
        List<Rental> activeRentals = rentalRepository.findByStatusOrderByStartAtDesc(RentalStatus.ACTIVE);
        //then
        assertThat(activeRentals).hasSize(2);
        assertThat(activeRentals.get(0).getStartAt()).isAfter(activeRentals.get(1).getStartAt());
    }

    @Test
    void testCountByStatus() {
        //given
        Customer customer = TestDataFactory.customer("count@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car1 = createAndSaveCar("VIN999", "III999");
        Car car2 = createAndSaveCar("VIN1010", "JJJ1010");
        Car car3 = createAndSaveCar("VIN1111", "KKK1111");

        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 10, 0);

        Rental rental1 = TestDataFactory.rental(savedCustomer, car1, start, start.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental rental2 = TestDataFactory.rental(savedCustomer, car2, start, start.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental rental3 = TestDataFactory.rental(savedCustomer, car3, start, start.plusDays(4),
                RateType.DAILY, RentalStatus.CANCELED);

        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        rentalRepository.save(rental3);
        //when
        long activeCount = rentalRepository.countByStatus(RentalStatus.ACTIVE);
        //then
        assertThat(activeCount).isEqualTo(2);
    }

    @Test
    void testExistsByCustomerIdTrue() {
        //given
        Customer customer = TestDataFactory.customer("existscustomer@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car = createAndSaveCar("VIN1212", "LLL1212");
        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 10, 0);
        Rental rental = TestDataFactory.rental(savedCustomer, car, start, start.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);
        rentalRepository.save(rental);
        //when
        boolean exists = rentalRepository.existsByCustomerId(savedCustomer.getId());
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByCustomerIdFalse() {
        //given
        Customer customer = TestDataFactory.customer("nocustomer@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        //when
        boolean exists = rentalRepository.existsByCustomerId(savedCustomer.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByCarIdTrue() {
        //given
        Customer customer = TestDataFactory.customer("existscar@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car = createAndSaveCar("VIN1313", "MMM1313");
        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 10, 0);
        Rental rental = TestDataFactory.rental(savedCustomer, car, start, start.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);
        rentalRepository.save(rental);
        //when
        boolean exists = rentalRepository.existsByCarId(car.getId());
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByCarIdFalse() {
        //given
        Car car = createAndSaveCar("VIN1414", "NNN1414");
        //when
        boolean exists = rentalRepository.existsByCarId(car.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testFindByCustomerIdOrderByStartAtDesc() {
        //given
        Customer customer1 = TestDataFactory.customer("customer1@example.com");
        Customer customer2 = TestDataFactory.customer("customer2@example.com");
        Customer savedCustomer1 = customerRepository.save(customer1);
        Customer savedCustomer2 = customerRepository.save(customer2);

        Car car1 = createAndSaveCar("VIN1515", "OOO1515");
        Car car2 = createAndSaveCar("VIN1616", "PPP1616");
        Car car3 = createAndSaveCar("VIN1717", "QQQ1717");

        LocalDateTime start1 = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2026, 3, 10, 10, 0);
        LocalDateTime start3 = LocalDateTime.of(2026, 3, 5, 10, 0);

        Rental rental1 = TestDataFactory.rental(savedCustomer1, car1, start1, start1.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental rental2 = TestDataFactory.rental(savedCustomer2, car2, start2, start2.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental rental3 = TestDataFactory.rental(savedCustomer1, car3, start3, start3.plusDays(4),
                RateType.DAILY, RentalStatus.RETURNED);

        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        rentalRepository.save(rental3);
        //when
        List<Rental> customer1Rentals = rentalRepository.findByCustomerIdOrderByStartAtDesc(savedCustomer1.getId());
        //then
        assertThat(customer1Rentals).hasSize(2);
        assertThat(customer1Rentals.get(0).getStartAt()).isAfter(customer1Rentals.get(1).getStartAt());
    }

    @Test
    void testFindByCarIdOrderByStartAtDesc() {
        //given
        Customer customer = TestDataFactory.customer("carhistory@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car1 = createAndSaveCar("VIN1818", "RRR1818");
        Car car2 = createAndSaveCar("VIN1919", "SSS1919");

        LocalDateTime start1 = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2026, 3, 10, 10, 0);
        LocalDateTime start3 = LocalDateTime.of(2026, 3, 5, 10, 0);

        Rental rental1 = TestDataFactory.rental(savedCustomer, car1, start1, start1.plusDays(4),
                RateType.DAILY, RentalStatus.RETURNED);
        Rental rental2 = TestDataFactory.rental(savedCustomer, car2, start2, start2.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental rental3 = TestDataFactory.rental(savedCustomer, car1, start3, start3.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);

        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        rentalRepository.save(rental3);
        //when
        List<Rental> car1Rentals = rentalRepository.findByCarIdOrderByStartAtDesc(car1.getId());
        //then
        assertThat(car1Rentals).hasSize(2);
        assertThat(car1Rentals.get(0).getStartAt()).isAfter(car1Rentals.get(1).getStartAt());
    }

    @Test
    void testFindOverdue() {
        //given
        Customer customer = TestDataFactory.customer("overdue@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car1 = createAndSaveCar("VIN2020", "TTT2020");
        Car car2 = createAndSaveCar("VIN2121", "UUU2121");
        Car car3 = createAndSaveCar("VIN2222", "VVV2222");

        LocalDateTime now = LocalDateTime.of(2026, 3, 15, 10, 0);
        LocalDateTime overdueEnd1 = LocalDateTime.of(2026, 3, 10, 10, 0);
        LocalDateTime overdueEnd2 = LocalDateTime.of(2026, 3, 12, 10, 0);
        LocalDateTime futureEnd = LocalDateTime.of(2026, 3, 20, 10, 0);

        Rental rental1 = TestDataFactory.rental(savedCustomer, car1, overdueEnd1.minusDays(4), overdueEnd1,
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental rental2 = TestDataFactory.rental(savedCustomer, car2, overdueEnd2.minusDays(4), overdueEnd2,
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental rental3 = TestDataFactory.rental(savedCustomer, car3, futureEnd.minusDays(4), futureEnd,
                RateType.DAILY, RentalStatus.ACTIVE);

        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        rentalRepository.save(rental3);
        //when
        List<Rental> overdueRentals = rentalRepository.findOverdue(RentalStatus.ACTIVE, now);
        //then
        assertThat(overdueRentals).hasSize(2);
        assertThat(overdueRentals.get(0).getPlannedEndAt()).isBefore(overdueRentals.get(1).getPlannedEndAt());
    }

    @Test
    void testCountOverdue() {
        //given
        Customer customer = TestDataFactory.customer("countoverdue@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car1 = createAndSaveCar("VIN2323", "WWW2323");
        Car car2 = createAndSaveCar("VIN2424", "XXX2424");

        LocalDateTime now = LocalDateTime.of(2026, 3, 15, 10, 0);
        LocalDateTime overdueEnd = LocalDateTime.of(2026, 3, 10, 10, 0);
        LocalDateTime futureEnd = LocalDateTime.of(2026, 3, 20, 10, 0);

        Rental rental1 = TestDataFactory.rental(savedCustomer, car1, overdueEnd.minusDays(4), overdueEnd,
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental rental2 = TestDataFactory.rental(savedCustomer, car2, futureEnd.minusDays(4), futureEnd,
                RateType.DAILY, RentalStatus.ACTIVE);

        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        //when
        long overdueCount = rentalRepository.countOverdue(RentalStatus.ACTIVE, now);
        //then
        assertThat(overdueCount).isEqualTo(1);
    }

    @Test
    void testFindLastRentalEndForCustomerWithActualReturn() {
        //given
        Customer customer = TestDataFactory.customer("lastend@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car1 = createAndSaveCar("VIN2525", "YYY2525");
        Car car2 = createAndSaveCar("VIN2626", "ZZZ2626");

        LocalDateTime actual1 = LocalDateTime.of(2026, 3, 10, 10, 0);
        LocalDateTime actual2 = LocalDateTime.of(2026, 3, 15, 10, 0);

        Rental rental1 = TestDataFactory.rental(savedCustomer, car1, actual1.minusDays(4), actual1.minusDays(1),
                RateType.DAILY, RentalStatus.RETURNED);
        rental1.setActualReturnAt(actual1);

        Rental rental2 = TestDataFactory.rental(savedCustomer, car2, actual2.minusDays(4), actual2.minusDays(1),
                RateType.DAILY, RentalStatus.RETURNED);
        rental2.setActualReturnAt(actual2);

        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        //when
        Optional<LocalDateTime> lastEnd = rentalRepository.findLastRentalEndForCustomer(savedCustomer.getId());
        //then
        assertThat(lastEnd).isPresent();
        assertThat(lastEnd.get()).isEqualTo(actual2);
    }

    @Test
    void testFindLastRentalEndForCustomerWithPlannedEnd() {
        //given
        Customer customer = TestDataFactory.customer("plannedend@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car = createAndSaveCar("VIN2727", "AAA2727");

        LocalDateTime planned = LocalDateTime.of(2026, 3, 20, 10, 0);

        Rental rental = TestDataFactory.rental(savedCustomer, car, planned.minusDays(4), planned,
                RateType.DAILY, RentalStatus.ACTIVE);

        rentalRepository.save(rental);
        //when
        Optional<LocalDateTime> lastEnd = rentalRepository.findLastRentalEndForCustomer(savedCustomer.getId());
        //then
        assertThat(lastEnd).isPresent();
        assertThat(lastEnd.get()).isEqualTo(planned);
    }

    @Test
    void testFindLastRentalEndForCustomerNoRentals() {
        //given
        Customer customer = TestDataFactory.customer("norentals@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        //when
        Optional<LocalDateTime> lastEnd = rentalRepository.findLastRentalEndForCustomer(savedCustomer.getId());
        //then
        assertThat(lastEnd).isEmpty();
    }

    @Test
    void testCountByCustomerId() {
        //given
        Customer customer1 = TestDataFactory.customer("countcust1@example.com");
        Customer customer2 = TestDataFactory.customer("countcust2@example.com");
        Customer savedCustomer1 = customerRepository.save(customer1);
        Customer savedCustomer2 = customerRepository.save(customer2);

        Car car1 = createAndSaveCar("VIN2828", "BBB2828");
        Car car2 = createAndSaveCar("VIN2929", "CCC2929");
        Car car3 = createAndSaveCar("VIN3030", "DDD3030");

        LocalDateTime start = LocalDateTime.of(2026, 3, 1, 10, 0);

        Rental rental1 = TestDataFactory.rental(savedCustomer1, car1, start, start.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental rental2 = TestDataFactory.rental(savedCustomer1, car2, start, start.plusDays(4),
                RateType.DAILY, RentalStatus.RETURNED);
        Rental rental3 = TestDataFactory.rental(savedCustomer2, car3, start, start.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);

        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        rentalRepository.save(rental3);

        //when
        long customer1Count = rentalRepository.countByCustomerId(savedCustomer1.getId());
        //then
        assertThat(customer1Count).isEqualTo(2);
    }

    @Test
    void testCountRentalsStartedBetween() {
        //given
        Customer customer = TestDataFactory.customer("started@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car1 = createAndSaveCar("VIN3131", "EEE3131");
        Car car2 = createAndSaveCar("VIN3232", "FFF3232");
        Car car3 = createAndSaveCar("VIN3333", "GGG3333");

        LocalDateTime start1 = LocalDateTime.of(2026, 3, 5, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2026, 3, 10, 10, 0);
        LocalDateTime start3 = LocalDateTime.of(2026, 3, 20, 10, 0);

        Rental rental1 = TestDataFactory.rental(savedCustomer, car1, start1, start1.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental rental2 = TestDataFactory.rental(savedCustomer, car2, start2, start2.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);
        Rental rental3 = TestDataFactory.rental(savedCustomer, car3, start3, start3.plusDays(4),
                RateType.DAILY, RentalStatus.ACTIVE);

        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        rentalRepository.save(rental3);

        LocalDateTime from = LocalDateTime.of(2026, 3, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 3, 15, 0, 0);
        //when
        long count = rentalRepository.countRentalsStartedBetween(from, to);
        //then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testSumRevenueBetween() {
        //given
        Customer customer = TestDataFactory.customer("revenue@example.com");
        Customer savedCustomer = customerRepository.save(customer);
        Car car1 = createAndSaveCar("VIN3434", "HHH3434");
        Car car2 = createAndSaveCar("VIN3535", "III3535");
        Car car3 = createAndSaveCar("VIN3636", "JJJ3636");

        LocalDateTime actual1 = LocalDateTime.of(2026, 3, 5, 10, 0);
        LocalDateTime actual2 = LocalDateTime.of(2026, 3, 10, 10, 0);
        LocalDateTime actual3 = LocalDateTime.of(2026, 3, 20, 10, 0);

        Rental rental1 = TestDataFactory.rental(savedCustomer, car1, actual1.minusDays(4), actual1.minusDays(1),
                RateType.DAILY, RentalStatus.RETURNED);
        rental1.setActualReturnAt(actual1);
        rental1.setTotalPrice(BigDecimal.valueOf(200));

        Rental rental2 = TestDataFactory.rental(savedCustomer, car2, actual2.minusDays(4), actual2.minusDays(1),
                RateType.DAILY, RentalStatus.RETURNED);
        rental2.setActualReturnAt(actual2);
        rental2.setTotalPrice(BigDecimal.valueOf(300));

        Rental rental3 = TestDataFactory.rental(savedCustomer, car3, actual3.minusDays(4), actual3.minusDays(1),
                RateType.DAILY, RentalStatus.RETURNED);
        rental3.setActualReturnAt(actual3);
        rental3.setTotalPrice(BigDecimal.valueOf(150));

        rentalRepository.save(rental1);
        rentalRepository.save(rental2);
        rentalRepository.save(rental3);

        LocalDateTime from = LocalDateTime.of(2026, 3, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 3, 15, 0, 0);
        //when
        BigDecimal revenue = rentalRepository.sumRevenueBetween(from, to);
        //then
        assertThat(revenue).isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    void testSumRevenueBetweenNoRentals() {
        //given
        LocalDateTime from = LocalDateTime.of(2026, 3, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2026, 3, 15, 0, 0);
        //hen
        BigDecimal revenue = rentalRepository.sumRevenueBetween(from, to);
        //then
        assertThat(revenue).isNull();
    }

    private Car createAndSaveCar(String vin, String plate) {
        Brand brand = TestDataFactory.brand("Brand-" + vin);
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Model-" + vin, savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Category-" + vin, BigDecimal.valueOf(5), BigDecimal.valueOf(10));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car(vin, plate, savedModel, savedCategory,
                BigDecimal.valueOf(10), BigDecimal.valueOf(50), BigDecimal.valueOf(300));
        return carRepository.save(car);
    }
}