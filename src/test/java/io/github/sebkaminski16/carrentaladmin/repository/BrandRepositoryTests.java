package io.github.sebkaminski16.carrentaladmin.repository;

import io.github.sebkaminski16.carrentaladmin.entity.Brand;
import io.github.sebkaminski16.carrentaladmin.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BrandRepositoryTests {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    void testSaveBrand() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        //when
        Brand saved = brandRepository.save(brand);
        //then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("BMW");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindById() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        Brand saved = brandRepository.save(brand);
        //when
        Optional<Brand> found = brandRepository.findById(saved.getId());
        //then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("BMW");
    }

    @Test
    void testFindByIdNotFound() {
        //given
        Long nonExistentId = 999L;
        //when
        Optional<Brand> found = brandRepository.findById(nonExistentId);
        //then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindAll() {
        //given
        Brand brand1 = TestDataFactory.brand("BMW");
        Brand brand2 = TestDataFactory.brand("Audi");
        brandRepository.save(brand1);
        brandRepository.save(brand2);
        //when
        List<Brand> brands = brandRepository.findAll();
        //then
        assertThat(brands).hasSize(2);
        assertThat(brands).extracting(Brand::getName)
                .containsExactlyInAnyOrder("BMW", "Audi");
    }

    @Test
    void testUpdateBrand() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        Brand saved = brandRepository.save(brand);
        saved.setName("Mercedes-Benz");
        //when
        Brand updated = brandRepository.save(saved);
        //then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getName()).isEqualTo("Mercedes-Benz");
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(updated.getCreatedAt());
    }

    @Test
    void testDeleteBrand() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        Brand saved = brandRepository.save(brand);
        Long brandId = saved.getId();
        //when
        brandRepository.delete(saved);
        //then
        Optional<Brand> found = brandRepository.findById(brandId);
        assertThat(found).isEmpty();
    }

    @Test
    void testDeleteById() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        Brand saved = brandRepository.save(brand);
        Long brandId = saved.getId();
        //when
        brandRepository.deleteById(brandId);
        //then
        Optional<Brand> found = brandRepository.findById(brandId);
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByNameContainingIgnoreCaseExactMatch() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        brandRepository.save(brand);
        //when
        List<Brand> found = brandRepository.findByNameContainingIgnoreCase("BMW");
        //then
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("BMW");
    }

    @Test
    void testFindByNameContainingIgnoreCasePartialMatch() {
        //given
        Brand brand1 = TestDataFactory.brand("Chevrolet");
        Brand brand2 = TestDataFactory.brand("Chrysler");
        Brand brand3 = TestDataFactory.brand("Toyota");
        brandRepository.save(brand1);
        brandRepository.save(brand2);
        brandRepository.save(brand3);
        //when
        List<Brand> found = brandRepository.findByNameContainingIgnoreCase("ch");
        //then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Brand::getName)
                .containsExactlyInAnyOrder("Chevrolet", "Chrysler");
    }

    @Test
    void testFindByNameContainingIgnoreCaseCaseInsensitive() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        brandRepository.save(brand);
        //when
        List<Brand> found = brandRepository.findByNameContainingIgnoreCase("bmw");
        //then
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("BMW");
    }

    @Test
    void testFindByNameContainingIgnoreCaseNoMatch() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        brandRepository.save(brand);
        //when
        List<Brand> found = brandRepository.findByNameContainingIgnoreCase("Ferrari");
        //then
        assertThat(found).isEmpty();
    }

    @Test
    void testExistsByNameIgnoreCaseTrue() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        brandRepository.save(brand);
        //when
        boolean exists = brandRepository.existsByNameIgnoreCase("bMw");
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByNameIgnoreCaseFalse() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        brandRepository.save(brand);
        //when
        boolean exists = brandRepository.existsByNameIgnoreCase("Bugatti");
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByNameIgnoreCaseAndIdNotTrue() {
        //given
        Brand brand1 = TestDataFactory.brand("Volvo");
        Brand brand2 = TestDataFactory.brand("Saab");
        brandRepository.save(brand1);
        Brand saved2 = brandRepository.save(brand2);
        //when
        boolean exists = brandRepository.existsByNameIgnoreCaseAndIdNot("volvo", saved2.getId());
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByNameIgnoreCaseAndIdNotFalseWhenSameId() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        Brand saved = brandRepository.save(brand);
        //when
        boolean exists = brandRepository.existsByNameIgnoreCaseAndIdNot("bmw", saved.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByNameIgnoreCaseAndIdNotFalseWhenNameDoesNotExist() {
        //given
        Brand brand = TestDataFactory.brand("BMW");
        Brand saved = brandRepository.save(brand);
        //when
        boolean exists = brandRepository.existsByNameIgnoreCaseAndIdNot("Tesla", saved.getId());
        //then
        assertThat(exists).isFalse();
    }
}