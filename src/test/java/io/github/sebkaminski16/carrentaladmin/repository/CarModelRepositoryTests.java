package io.github.sebkaminski16.carrentaladmin.repository;

import io.github.sebkaminski16.carrentaladmin.entity.Brand;
import io.github.sebkaminski16.carrentaladmin.entity.CarModel;
import io.github.sebkaminski16.carrentaladmin.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CarModelRepositoryTests {

    @Autowired
    private CarModelRepository carModelRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Test
    void testSaveCarModel() {
        //given
        Brand brand = TestDataFactory.brand("Toyota");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Corolla", savedBrand);
        //when
        CarModel saved = carModelRepository.save(model);
        //then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Corolla");
        assertThat(saved.getBrand().getId()).isEqualTo(savedBrand.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindById() {
        //given
        Brand brand = TestDataFactory.brand("Honda");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Civic", savedBrand);
        CarModel saved = carModelRepository.save(model);
        //when
        Optional<CarModel> found = carModelRepository.findById(saved.getId());
        //then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Civic");
        assertThat(found.get().getBrand().getId()).isEqualTo(savedBrand.getId());
    }

    @Test
    void testFindByIdNotFound() {
        //given
        Long nonExistentId = 999L;
        //when
        Optional<CarModel> found = carModelRepository.findById(nonExistentId);
        //then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindAll() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model1 = TestDataFactory.model("X5", savedBrand);
        CarModel model2 = TestDataFactory.model("X3", savedBrand);
        carModelRepository.save(model1);
        carModelRepository.save(model2);
        //when
        List<CarModel> models = carModelRepository.findAll();
        //then
        assertThat(models).hasSize(2);
        assertThat(models).extracting(CarModel::getName)
                .containsExactlyInAnyOrder("X5", "X3");
    }

    @Test
    void testUpdateCarModel() {
        //given
        Brand brand = TestDataFactory.brand("Mercedes");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("C-Class", savedBrand);
        CarModel saved = carModelRepository.save(model);
        saved.setName("C-Class AMG");
        //when
        CarModel updated = carModelRepository.save(saved);
        //then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getName()).isEqualTo("C-Class AMG");
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(updated.getCreatedAt());
    }

    @Test
    void testDeleteCarModel() {
        //given
        Brand brand = TestDataFactory.brand("Volkswagen");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Golf", savedBrand);
        CarModel saved = carModelRepository.save(model);
        Long modelId = saved.getId();
        //when
        carModelRepository.delete(saved);
        //then
        Optional<CarModel> found = carModelRepository.findById(modelId);
        assertThat(found).isEmpty();
    }

    @Test
    void testDeleteById() {
        //given
        Brand brand = TestDataFactory.brand("Ford");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Mustang", savedBrand);
        CarModel saved = carModelRepository.save(model);
        Long modelId = saved.getId();
        //when
        carModelRepository.deleteById(modelId);
        //then
        Optional<CarModel> found = carModelRepository.findById(modelId);
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByBrandId() {
        // given
        Brand brand1 = TestDataFactory.brand("Audi");
        Brand brand2 = TestDataFactory.brand("Porsche");
        Brand savedBrand1 = brandRepository.save(brand1);
        Brand savedBrand2 = brandRepository.save(brand2);

        CarModel model1 = TestDataFactory.model("A4", savedBrand1);
        CarModel model2 = TestDataFactory.model("A6", savedBrand1);
        CarModel model3 = TestDataFactory.model("911", savedBrand2);
        carModelRepository.save(model1);
        carModelRepository.save(model2);
        carModelRepository.save(model3);
        //when
        List<CarModel> found = carModelRepository.findByBrandId(savedBrand1.getId());
        //then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(CarModel::getName)
                .containsExactlyInAnyOrder("A4", "A6");
    }

    @Test
    void testFindByBrandIdEmpty() {
        //given
        Brand brand = TestDataFactory.brand("Tesla");
        Brand savedBrand = brandRepository.save(brand);
        //when
        List<CarModel> found = carModelRepository.findByBrandId(savedBrand.getId());
        //then
        assertThat(found).isEmpty();
    }

    @Test
    void testExistsByBrandIdTrue() {
        //given
        Brand brand = TestDataFactory.brand("Nissan");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Altima", savedBrand);
        carModelRepository.save(model);
        //when
        boolean exists = carModelRepository.existsByBrandId(savedBrand.getId());
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByBrandIdFalse() {
        //given
        Brand brand = TestDataFactory.brand("Mazda");
        Brand savedBrand = brandRepository.save(brand);
        //when
        boolean exists = carModelRepository.existsByBrandId(savedBrand.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testFindByNameContainingIgnoreCaseExactMatch() {
        //given
        Brand brand = TestDataFactory.brand("Chevrolet");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Camaro", savedBrand);
        carModelRepository.save(model);
        //when
        List<CarModel> found = carModelRepository.findByNameContainingIgnoreCase("Camaro");
        //then
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("Camaro");
    }

    @Test
    void testFindByNameContainingIgnoreCasePartialMatch() {
        //given
        Brand brand = TestDataFactory.brand("Hyundai");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model1 = TestDataFactory.model("Elantra", savedBrand);
        CarModel model2 = TestDataFactory.model("Sonata", savedBrand);
        CarModel model3 = TestDataFactory.model("Tucson", savedBrand);
        carModelRepository.save(model1);
        carModelRepository.save(model2);
        carModelRepository.save(model3);
        //when
        List<CarModel> found = carModelRepository.findByNameContainingIgnoreCase("son");
        //then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(CarModel::getName)
                .containsExactlyInAnyOrder("Sonata", "Tucson");
    }

    @Test
    void testFindByNameContainingIgnoreCaseCaseInsensitive() {
        //given
        Brand brand = TestDataFactory.brand("Kia");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Sportage", savedBrand);
        carModelRepository.save(model);
        //when
        List<CarModel> found = carModelRepository.findByNameContainingIgnoreCase("SPORTAGE");
        //then
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("Sportage");
    }

    @Test
    void testFindByNameContainingIgnoreCaseNoMatch() {
        //given
        Brand brand = TestDataFactory.brand("Subaru");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Outback", savedBrand);
        carModelRepository.save(model);
        //when
        List<CarModel> found = carModelRepository.findByNameContainingIgnoreCase("Impreza");
        //then
        assertThat(found).isEmpty();
    }

    @Test
    void testExistsByNameIgnoreCaseTrue() {
        //given
        Brand brand = TestDataFactory.brand("Lexus");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("RX350", savedBrand);
        carModelRepository.save(model);
        //when
        boolean exists = carModelRepository.existsByNameIgnoreCase("rx350");
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByNameIgnoreCaseFalse() {
        //given
        Brand brand = TestDataFactory.brand("Acura");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("MDX", savedBrand);
        carModelRepository.save(model);
        //when
        boolean exists = carModelRepository.existsByNameIgnoreCase("RDX");
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByNameIgnoreCaseAndIdNotTrue() {
        //given
        Brand brand = TestDataFactory.brand("Volvo");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model1 = TestDataFactory.model("XC90", savedBrand);
        CarModel model2 = TestDataFactory.model("XC60", savedBrand);
        carModelRepository.save(model1);
        CarModel saved2 = carModelRepository.save(model2);
        //when
        boolean exists = carModelRepository.existsByNameIgnoreCaseAndIdNot("xc90", saved2.getId());
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByNameIgnoreCaseAndIdNotFalseWhenSameId() {
        //given
        Brand brand = TestDataFactory.brand("Jaguar");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("F-PACE", savedBrand);
        CarModel saved = carModelRepository.save(model);
        //when
        boolean exists = carModelRepository.existsByNameIgnoreCaseAndIdNot("f-pace", saved.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByNameIgnoreCaseAndIdNotFalseWhenNameDoesNotExist() {
        //given
        Brand brand = TestDataFactory.brand("Land Rover");
        Brand savedBrand = brandRepository.save(brand);
        CarModel model = TestDataFactory.model("Range Rover", savedBrand);
        CarModel saved = carModelRepository.save(model);
        //when
        boolean exists = carModelRepository.existsByNameIgnoreCaseAndIdNot("Discovery", saved.getId());
        //then
        assertThat(exists).isFalse();
    }
}