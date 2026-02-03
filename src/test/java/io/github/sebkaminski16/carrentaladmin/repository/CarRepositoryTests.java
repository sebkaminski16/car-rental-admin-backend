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
class CarRepositoryTests {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CarModelRepository carModelRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Test
    void testSaveCar() {
        //given
        Brand brand = TestDataFactory.brand("Toyota");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Camry", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Sedan", BigDecimal.valueOf(5), BigDecimal.valueOf(10));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN123", "ABC123", savedModel, savedCategory,
                BigDecimal.valueOf(10), BigDecimal.valueOf(50), BigDecimal.valueOf(300));
        //when
        Car saved = carRepository.save(car);
        //then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getVin()).isEqualTo("VIN123");
        assertThat(saved.getLicensePlate()).isEqualTo("ABC123");
        assertThat(saved.getStatus()).isEqualTo(CarStatus.AVAILABLE);
        assertThat(saved.getModel().getId()).isEqualTo(savedModel.getId());
        assertThat(saved.getCategory().getId()).isEqualTo(savedCategory.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindById() {
        //given
        Brand brand = TestDataFactory.brand("Honda");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Civic", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Compact", BigDecimal.ZERO, BigDecimal.ZERO);
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN456", "XYZ789", savedModel, savedCategory,
                BigDecimal.valueOf(8), BigDecimal.valueOf(40), BigDecimal.valueOf(250));
        Car saved = carRepository.save(car);
        //when
        Optional<Car> found = carRepository.findById(saved.getId());
        //then
        assertThat(found).isPresent();
        assertThat(found.get().getVin()).isEqualTo("VIN456");
        assertThat(found.get().getLicensePlate()).isEqualTo("XYZ789");
    }

    @Test
    void testFindByIdNotFound() {
        //given
        Long nonExistentId = 999L;
        //when
        Optional<Car> found = carRepository.findById(nonExistentId);
        //then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindAll() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("X5", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("SUV", BigDecimal.valueOf(3), BigDecimal.valueOf(8));
        Category savedCategory = categoryRepository.save(category);
        Car car1 = TestDataFactory.car("VIN111", "AAA111", savedModel, savedCategory,
                BigDecimal.valueOf(15), BigDecimal.valueOf(80), BigDecimal.valueOf(500));
        Car car2 = TestDataFactory.car("VIN222", "BBB222", savedModel, savedCategory,
                BigDecimal.valueOf(15), BigDecimal.valueOf(80), BigDecimal.valueOf(500));
        carRepository.save(car1);
        carRepository.save(car2);
        //when
        List<Car> cars = carRepository.findAll();
        //then
        assertThat(cars).hasSize(2);
        assertThat(cars).extracting(Car::getVin)
                .containsExactlyInAnyOrder("VIN111", "VIN222");
    }

    @Test
    void testUpdateCar() {
        //given
        Brand brand = TestDataFactory.brand("Mercedes");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("C-Class", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Luxury", BigDecimal.valueOf(7), BigDecimal.valueOf(12));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN333", "CCC333", savedModel, savedCategory,
                BigDecimal.valueOf(20), BigDecimal.valueOf(100), BigDecimal.valueOf(600));
        Car saved = carRepository.save(car);

        saved.setStatus(CarStatus.MAINTENANCE);
        //when
        Car updated = carRepository.save(saved);
        //then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getStatus()).isEqualTo(CarStatus.MAINTENANCE);
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(updated.getCreatedAt());
    }

    @Test
    void testDeleteCar() {
        //given
        Brand brand = TestDataFactory.brand("Volkswagen");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Golf", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Hatchback", BigDecimal.valueOf(4), BigDecimal.valueOf(9));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN444", "DDD444", savedModel, savedCategory,
                BigDecimal.valueOf(9), BigDecimal.valueOf(45), BigDecimal.valueOf(280));
        Car saved = carRepository.save(car);
        Long carId = saved.getId();
        //when
        carRepository.delete(saved);
        //then
        Optional<Car> found = carRepository.findById(carId);
        assertThat(found).isEmpty();
    }

    @Test
    void testDeleteById() {
        //given
        Brand brand = TestDataFactory.brand("Ford");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Mustang", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Sports", BigDecimal.valueOf(6), BigDecimal.valueOf(11));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN555", "EEE555", savedModel, savedCategory,
                BigDecimal.valueOf(25), BigDecimal.valueOf(120), BigDecimal.valueOf(700));
        Car saved = carRepository.save(car);
        Long carId = saved.getId();
        //when
        carRepository.deleteById(carId);
        //then
        Optional<Car> found = carRepository.findById(carId);
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByStatus() {
        //given
        Brand brand = TestDataFactory.brand("Audi");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("A4", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Executive", BigDecimal.valueOf(5), BigDecimal.valueOf(10));
        Category savedCategory = categoryRepository.save(category);

        Car car1 = TestDataFactory.car("VIN666", "FFF666", savedModel, savedCategory,
                BigDecimal.valueOf(12), BigDecimal.valueOf(60), BigDecimal.valueOf(350));
        car1.setStatus(CarStatus.AVAILABLE);

        Car car2 = TestDataFactory.car("VIN777", "GGG777", savedModel, savedCategory,
                BigDecimal.valueOf(12), BigDecimal.valueOf(60), BigDecimal.valueOf(350));
        car2.setStatus(CarStatus.RENTED);

        Car car3 = TestDataFactory.car("VIN888", "HHH888", savedModel, savedCategory,
                BigDecimal.valueOf(12), BigDecimal.valueOf(60), BigDecimal.valueOf(350));
        car3.setStatus(CarStatus.AVAILABLE);

        carRepository.save(car1);
        carRepository.save(car2);
        carRepository.save(car3);
        //when
        List<Car> availableCars = carRepository.findByStatus(CarStatus.AVAILABLE);
        //then
        assertThat(availableCars).hasSize(2);
        assertThat(availableCars).extracting(Car::getVin)
                .containsExactlyInAnyOrder("VIN666", "VIN888");
    }

    @Test
    void testFindByStatusEmpty() {
        //given
        Brand brand = TestDataFactory.brand("Nissan");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Altima", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Midsize", BigDecimal.valueOf(4), BigDecimal.valueOf(9));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN999", "III999", savedModel, savedCategory,
                BigDecimal.valueOf(11), BigDecimal.valueOf(55), BigDecimal.valueOf(320));
        car.setStatus(CarStatus.AVAILABLE);
        carRepository.save(car);
        //when
        List<Car> maintenanceCars = carRepository.findByStatus(CarStatus.MAINTENANCE);
        //then
        assertThat(maintenanceCars).isEmpty();
    }

    @Test
    void testExistsByModelIdTrue() {
        //given
        Brand brand = TestDataFactory.brand("Mazda");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("CX-5", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Crossover", BigDecimal.valueOf(5), BigDecimal.valueOf(10));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN1010", "JJJ1010", savedModel, savedCategory,
                BigDecimal.valueOf(13), BigDecimal.valueOf(65), BigDecimal.valueOf(380));
        carRepository.save(car);
        //when
        boolean exists = carRepository.existsByModelId(savedModel.getId());
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByModelIdFalse() {
        //given
        Brand brand = TestDataFactory.brand("Subaru");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Outback", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        //when
        boolean exists = carRepository.existsByModelId(savedModel.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByCategoryIdTrue() {
        // given
        Brand brand = TestDataFactory.brand("Chevrolet");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Malibu", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Family", BigDecimal.valueOf(6), BigDecimal.valueOf(11));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN1111", "KKK1111", savedModel, savedCategory,
                BigDecimal.valueOf(10), BigDecimal.valueOf(50), BigDecimal.valueOf(300));
        carRepository.save(car);
        //when
        boolean exists = carRepository.existsByCategoryId(savedCategory.getId());
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByCategoryIdFalse() {
        //given
        Category category = TestDataFactory.category("Minivan", BigDecimal.valueOf(7), BigDecimal.valueOf(12));
        Category savedCategory = categoryRepository.save(category);
        //when
        boolean exists = carRepository.existsByCategoryId(savedCategory.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testFindByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase() {
        //given
        Brand brand = TestDataFactory.brand("Hyundai");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Elantra", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Economy", BigDecimal.valueOf(8), BigDecimal.valueOf(15));
        Category savedCategory = categoryRepository.save(category);

        Car car1 = TestDataFactory.car("ABC123DEF", "LLL000", savedModel, savedCategory,
                BigDecimal.valueOf(8), BigDecimal.valueOf(40), BigDecimal.valueOf(240));
        Car car2 = TestDataFactory.car("XYZ789GHI", "MMM123", savedModel, savedCategory,
                BigDecimal.valueOf(8), BigDecimal.valueOf(40), BigDecimal.valueOf(240));
        Car car3 = TestDataFactory.car("TEST456UVW", "NNN789", savedModel, savedCategory,
                BigDecimal.valueOf(8), BigDecimal.valueOf(40), BigDecimal.valueOf(240));

        carRepository.save(car1);
        carRepository.save(car2);
        carRepository.save(car3);
        //when
        List<Car> found = carRepository.findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase("123", "123");
        //then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Car::getVin)
                .containsExactlyInAnyOrder("ABC123DEF", "XYZ789GHI");
    }

    @Test
    void testFindByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCaseCaseInsensitive() {
        //given
        Brand brand = TestDataFactory.brand("Kia");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Sportage", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Compact SUV", BigDecimal.valueOf(5), BigDecimal.valueOf(10));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("TESTVIN123", "OOO321", savedModel, savedCategory,
                BigDecimal.valueOf(11), BigDecimal.valueOf(55), BigDecimal.valueOf(320));
        carRepository.save(car);
        //when
        List<Car> found = carRepository.findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase("testvin", "testvin");
        //then
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getVin()).isEqualTo("TESTVIN123");
    }

    @Test
    void testFindByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCaseNoMatch() {
        //given
        Brand brand = TestDataFactory.brand("Lexus");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("RX350", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Premium SUV", BigDecimal.valueOf(6), BigDecimal.valueOf(11));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN12345", "PPP555", savedModel, savedCategory,
                BigDecimal.valueOf(18), BigDecimal.valueOf(90), BigDecimal.valueOf(550));
        carRepository.save(car);
        //when
        List<Car> found = carRepository.findByLicensePlateContainingIgnoreCaseOrVinContainingIgnoreCase("NOTFOUND", "NOTFOUND");
        //then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindAvailableBetweenNoConflict() {
        //given
        Brand brand = TestDataFactory.brand("Volvo");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("XC90", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Luxury SUV", BigDecimal.valueOf(7), BigDecimal.valueOf(12));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN54321", "QQQ111", savedModel, savedCategory,
                BigDecimal.valueOf(20), BigDecimal.valueOf(100), BigDecimal.valueOf(600));
        car.setStatus(CarStatus.AVAILABLE);
        Car savedCar = carRepository.save(car);

        LocalDateTime searchFrom = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime searchTo = LocalDateTime.of(2026, 3, 5, 10, 0);
        //when
        List<Car> available = carRepository.findAvailableBetween(searchFrom, searchTo, RentalStatus.ACTIVE);
        //then
        assertThat(available).hasSize(1);
        assertThat(available.getFirst().getId()).isEqualTo(savedCar.getId());
    }

    @Test
    void testFindAvailableBetweenWithConflictingRental() {
        //given
        Brand brand = TestDataFactory.brand("Jaguar");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("F-PACE", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Sport SUV", BigDecimal.valueOf(8), BigDecimal.valueOf(15));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN67890", "RRR222", savedModel, savedCategory,
                BigDecimal.valueOf(22), BigDecimal.valueOf(110), BigDecimal.valueOf(650));
        car.setStatus(CarStatus.AVAILABLE);
        Car savedCar = carRepository.save(car);

        Customer customer = TestDataFactory.customer("test@example.com");
        Customer savedCustomer = customerRepository.save(customer);

        LocalDateTime rentalStart = LocalDateTime.of(2026, 3, 2, 10, 0);
        LocalDateTime rentalEnd = LocalDateTime.of(2026, 3, 4, 10, 0);
        Rental rental = TestDataFactory.rental(savedCustomer, savedCar, rentalStart, rentalEnd,
                RateType.DAILY, RentalStatus.ACTIVE);
        rentalRepository.save(rental);

        LocalDateTime searchFrom = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime searchTo = LocalDateTime.of(2026, 3, 5, 10, 0);
        //when
        List<Car> available = carRepository.findAvailableBetween(searchFrom, searchTo, RentalStatus.ACTIVE);
        //then
        assertThat(available).isEmpty();
    }

    @Test
    void testFindAvailableBetweenWithNonActiveRental() {
        // given
        Brand brand = TestDataFactory.brand("Land Rover");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Range Rover", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Luxury Off-Road", BigDecimal.valueOf(9), BigDecimal.valueOf(16));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN11111", "SSS333", savedModel, savedCategory,
                BigDecimal.valueOf(25), BigDecimal.valueOf(125), BigDecimal.valueOf(750));
        car.setStatus(CarStatus.AVAILABLE);
        Car savedCar = carRepository.save(car);

        Customer customer = TestDataFactory.customer("customer@example.com");
        Customer savedCustomer = customerRepository.save(customer);

        LocalDateTime rentalStart = LocalDateTime.of(2026, 3, 2, 10, 0);
        LocalDateTime rentalEnd = LocalDateTime.of(2026, 3, 4, 10, 0);
        Rental rental = TestDataFactory.rental(savedCustomer, savedCar, rentalStart, rentalEnd,
                RateType.DAILY, RentalStatus.RETURNED);
        rentalRepository.save(rental);

        LocalDateTime searchFrom = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime searchTo = LocalDateTime.of(2026, 3, 5, 10, 0);
        //when
        List<Car> available = carRepository.findAvailableBetween(searchFrom, searchTo, RentalStatus.ACTIVE);
        //then
        assertThat(available).hasSize(1);
        assertThat(available.getFirst().getId()).isEqualTo(savedCar.getId());
    }

    @Test
    void testFindAvailableBetweenExcludesNonAvailableStatus() {
        //given
        Brand brand = TestDataFactory.brand("Tesla");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Model 3", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Electric", BigDecimal.valueOf(10), BigDecimal.valueOf(18));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN22222", "TTT444", savedModel, savedCategory,
                BigDecimal.valueOf(15), BigDecimal.valueOf(75), BigDecimal.valueOf(450));
        car.setStatus(CarStatus.MAINTENANCE);
        carRepository.save(car);

        LocalDateTime searchFrom = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime searchTo = LocalDateTime.of(2026, 3, 5, 10, 0);
        //when
        List<Car> available = carRepository.findAvailableBetween(searchFrom, searchTo, RentalStatus.ACTIVE);
        //then
        assertThat(available).isEmpty();
    }

    @Test
    void testExistsByVinTrue() {
        //given
        Brand brand = TestDataFactory.brand("Porsche");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("911", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Sports Car", BigDecimal.valueOf(12), BigDecimal.valueOf(20));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("UNIQUEVIN123", "UUU555", savedModel, savedCategory,
                BigDecimal.valueOf(30), BigDecimal.valueOf(150), BigDecimal.valueOf(900));
        carRepository.save(car);
        //when
        boolean exists = carRepository.existsByVin("UNIQUEVIN123");
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByVinFalse() {
        //given
        Brand brand = TestDataFactory.brand("Lamborghini");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Huracan", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Supercar", BigDecimal.valueOf(15), BigDecimal.valueOf(25));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN33333", "VVV666", savedModel, savedCategory,
                BigDecimal.valueOf(50), BigDecimal.valueOf(250), BigDecimal.valueOf(1500));
        carRepository.save(car);
        //when
        boolean exists = carRepository.existsByVin("NONEXISTENTVIN");
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByLicensePlateTrue() {
        //given
        Brand brand = TestDataFactory.brand("Ferrari");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("488", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Exotic", BigDecimal.valueOf(18), BigDecimal.valueOf(30));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN44444", "UNIQUE777", savedModel, savedCategory,
                BigDecimal.valueOf(60), BigDecimal.valueOf(300), BigDecimal.valueOf(1800));
        carRepository.save(car);
        //whne
        boolean exists = carRepository.existsByLicensePlate("UNIQUE777");
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByLicensePlateFalse() {
        //given
        Brand brand = TestDataFactory.brand("Bugatti");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Chiron", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Hypercar", BigDecimal.valueOf(20), BigDecimal.valueOf(35));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN55555", "WWW888", savedModel, savedCategory,
                BigDecimal.valueOf(100), BigDecimal.valueOf(500), BigDecimal.valueOf(3000));
        carRepository.save(car);
        //when
        boolean exists = carRepository.existsByLicensePlate("NONEXISTENT");
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByVinAndIdNotTrue() {
        //given
        Brand brand = TestDataFactory.brand("McLaren");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("720S", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Performance", BigDecimal.valueOf(14), BigDecimal.valueOf(22));
        Category savedCategory = categoryRepository.save(category);

        Car car1 = TestDataFactory.car("VIN66666", "XXX111", savedModel, savedCategory,
                BigDecimal.valueOf(40), BigDecimal.valueOf(200), BigDecimal.valueOf(1200));
        Car car2 = TestDataFactory.car("VIN77777", "YYY222", savedModel, savedCategory,
                BigDecimal.valueOf(40), BigDecimal.valueOf(200), BigDecimal.valueOf(1200));

        carRepository.save(car1);
        Car saved2 = carRepository.save(car2);
        //when
        boolean exists = carRepository.existsByVinAndIdNot("VIN66666", saved2.getId());
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByVinAndIdNotFalseWhenSameId() {
        //given
        Brand brand = TestDataFactory.brand("Aston Martin");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("DB11", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Grand Tourer", BigDecimal.valueOf(11), BigDecimal.valueOf(19));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN88888", "ZZZ333", savedModel, savedCategory,
                BigDecimal.valueOf(35), BigDecimal.valueOf(175), BigDecimal.valueOf(1050));
        Car saved = carRepository.save(car);
        //when
        boolean exists = carRepository.existsByVinAndIdNot("VIN88888", saved.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByVinAndIdNotFalseWhenVinDoesNotExist() {
        //given
        Brand brand = TestDataFactory.brand("Bentley");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Continental", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Ultra Luxury", BigDecimal.valueOf(13), BigDecimal.valueOf(21));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN99999", "AAA444", savedModel, savedCategory,
                BigDecimal.valueOf(45), BigDecimal.valueOf(225), BigDecimal.valueOf(1350));
        Car saved = carRepository.save(car);
        //when
        boolean exists = carRepository.existsByVinAndIdNot("NONEXISTENTVIN", saved.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByLicensePlateAndIdNotTrue() {
        //given
        Brand brand = TestDataFactory.brand("Rolls Royce");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Phantom", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Ultimate Luxury", BigDecimal.valueOf(16), BigDecimal.valueOf(28));
        Category savedCategory = categoryRepository.save(category);

        Car car1 = TestDataFactory.car("VIN00001", "BBB555", savedModel, savedCategory,
                BigDecimal.valueOf(70), BigDecimal.valueOf(350), BigDecimal.valueOf(2100));
        Car car2 = TestDataFactory.car("VIN00002", "CCC666", savedModel, savedCategory,
                BigDecimal.valueOf(70), BigDecimal.valueOf(350), BigDecimal.valueOf(2100));

        carRepository.save(car1);
        Car saved2 = carRepository.save(car2);
        //when
        boolean exists = carRepository.existsByLicensePlateAndIdNot("BBB555", saved2.getId());
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByLicensePlateAndIdNotFalseWhenSameId() {
        //given
        Brand brand = TestDataFactory.brand("Maserati");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Quattroporte", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Italian Luxury", BigDecimal.valueOf(10), BigDecimal.valueOf(17));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN00003", "DDD777", savedModel, savedCategory,
                BigDecimal.valueOf(28), BigDecimal.valueOf(140), BigDecimal.valueOf(840));
        Car saved = carRepository.save(car);
        //when
        boolean exists = carRepository.existsByLicensePlateAndIdNot("DDD777", saved.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByLicensePlateAndIdNotFalseWhenPlateDoesNotExist() {
        //given
        Brand brand = TestDataFactory.brand("Alfa Romeo");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Giulia", savedBrand);
        CarModel savedModel = carModelRepository.save(model);
        Category category = TestDataFactory.category("Italian Sport", BigDecimal.valueOf(8), BigDecimal.valueOf(14));
        Category savedCategory = categoryRepository.save(category);
        Car car = TestDataFactory.car("VIN00004", "EEE888", savedModel, savedCategory,
                BigDecimal.valueOf(18), BigDecimal.valueOf(90), BigDecimal.valueOf(540));
        Car saved = carRepository.save(car);
        //when
        boolean exists = carRepository.existsByLicensePlateAndIdNot("NONEXISTENT", saved.getId());
        //then
        assertThat(exists).isFalse();
    }
}