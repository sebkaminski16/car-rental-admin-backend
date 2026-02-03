package io.github.sebkaminski16.carrentaladmin.repository;

import io.github.sebkaminski16.carrentaladmin.entity.Category;
import io.github.sebkaminski16.carrentaladmin.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testSaveCategory() {
        //given
        Category category = TestDataFactory.category("Sedan", BigDecimal.valueOf(5), BigDecimal.valueOf(10));
        //when
        Category saved = categoryRepository.save(category);
        //then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Sedan");
        assertThat(saved.getDailyDiscountPercent()).isEqualByComparingTo(BigDecimal.valueOf(5));
        assertThat(saved.getWeeklyDiscountPercent()).isEqualByComparingTo(BigDecimal.valueOf(10));
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindById() {
        //given
        Category category = TestDataFactory.category("SUV", BigDecimal.valueOf(7), BigDecimal.valueOf(12));
        Category saved = categoryRepository.save(category);
        //when
        Optional<Category> found = categoryRepository.findById(saved.getId());
        //then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("SUV");
    }

    @Test
    void testFindByIdNotFound() {
        //given
        Long nonExistentId = 999L;
        //when
        Optional<Category> found = categoryRepository.findById(nonExistentId);
        //then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindAll() {
        //given
        Category category1 = TestDataFactory.category("Compact", BigDecimal.valueOf(3), BigDecimal.valueOf(8));
        Category category2 = TestDataFactory.category("Luxury", BigDecimal.valueOf(10), BigDecimal.valueOf(15));
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        //when
        List<Category> categories = categoryRepository.findAll();
        //then
        assertThat(categories).hasSize(2);
        assertThat(categories).extracting(Category::getName)
                .containsExactlyInAnyOrder("Compact", "Luxury");
    }

    @Test
    void testUpdateCategory() {
        //given
        Category category = TestDataFactory.category("Economy", BigDecimal.valueOf(4), BigDecimal.valueOf(9));
        Category saved = categoryRepository.save(category);

        saved.setName("Budget");
        saved.setDailyDiscountPercent(BigDecimal.valueOf(6));
        //when
        Category updated = categoryRepository.save(saved);
        //then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getName()).isEqualTo("Budget");
        assertThat(updated.getDailyDiscountPercent()).isEqualByComparingTo(BigDecimal.valueOf(6));
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(updated.getCreatedAt());
    }

    @Test
    void testDeleteCategory() {
        //given
        Category category = TestDataFactory.category("Sports", BigDecimal.valueOf(8), BigDecimal.valueOf(14));
        Category saved = categoryRepository.save(category);
        Long categoryId = saved.getId();
        //when
        categoryRepository.delete(saved);
        //then
        Optional<Category> found = categoryRepository.findById(categoryId);
        assertThat(found).isEmpty();
    }

    @Test
    void testDeleteById() {
        //given
        Category category = TestDataFactory.category("Van", BigDecimal.valueOf(6), BigDecimal.valueOf(11));
        Category saved = categoryRepository.save(category);
        Long categoryId = saved.getId();
        //when
        categoryRepository.deleteById(categoryId);
        //then
        Optional<Category> found = categoryRepository.findById(categoryId);
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByNameContainingIgnoreCaseExactMatch() {
        //given
        Category category = TestDataFactory.category("Convertible", BigDecimal.valueOf(9), BigDecimal.valueOf(16));
        categoryRepository.save(category);
        //when
        List<Category> found = categoryRepository.findByNameContainingIgnoreCase("Convertible");
        //then
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("Convertible");
    }

    @Test
    void testFindByNameContainingIgnoreCasePartialMatch() {
        //given
        Category category1 = TestDataFactory.category("Compact SUV", BigDecimal.valueOf(5), BigDecimal.valueOf(10));
        Category category2 = TestDataFactory.category("Midsize SUV", BigDecimal.valueOf(7), BigDecimal.valueOf(12));
        Category category3 = TestDataFactory.category("Sedan", BigDecimal.valueOf(4), BigDecimal.valueOf(9));
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        //when
        List<Category> found = categoryRepository.findByNameContainingIgnoreCase("SUV");
        //then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Category::getName)
                .containsExactlyInAnyOrder("Compact SUV", "Midsize SUV");
    }

    @Test
    void testFindByNameContainingIgnoreCaseCaseInsensitive() {
        //given
        Category category = TestDataFactory.category("Hatchback", BigDecimal.valueOf(3), BigDecimal.valueOf(7));
        categoryRepository.save(category);
        //when
        List<Category> found = categoryRepository.findByNameContainingIgnoreCase("HATCHBACK");
        //then
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("Hatchback");
    }

    @Test
    void testFindByNameContainingIgnoreCaseNoMatch() {
        //given
        Category category = TestDataFactory.category("Coupe", BigDecimal.valueOf(8), BigDecimal.valueOf(13));
        categoryRepository.save(category);
        //when
        List<Category> found = categoryRepository.findByNameContainingIgnoreCase("Truck");
        //then
        assertThat(found).isEmpty();
    }

    @Test
    void testExistsByNameIgnoreCaseTrue() {
        //given
        Category category = TestDataFactory.category("Minivan", BigDecimal.valueOf(6), BigDecimal.valueOf(11));
        categoryRepository.save(category);
        //when
        boolean exists = categoryRepository.existsByNameIgnoreCase("minivan");
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByNameIgnoreCaseFalse() {
        //given
        Category category = TestDataFactory.category("Crossover", BigDecimal.valueOf(5), BigDecimal.valueOf(10));
        categoryRepository.save(category);
        //when
        boolean exists = categoryRepository.existsByNameIgnoreCase("Pickup");
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByNameIgnoreCaseAndIdNotTrue() {
        //iven
        Category category1 = TestDataFactory.category("Premium", BigDecimal.valueOf(10), BigDecimal.valueOf(18));
        Category category2 = TestDataFactory.category("Standard", BigDecimal.valueOf(4), BigDecimal.valueOf(8));
        categoryRepository.save(category1);
        Category saved2 = categoryRepository.save(category2);
        //when
        boolean exists = categoryRepository.existsByNameIgnoreCaseAndIdNot("premium", saved2.getId());
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByNameIgnoreCaseAndIdNotFalseWhenSameId() {
        //given
        Category category = TestDataFactory.category("Electric", BigDecimal.valueOf(12), BigDecimal.valueOf(20));
        Category saved = categoryRepository.save(category);
        //when
        boolean exists = categoryRepository.existsByNameIgnoreCaseAndIdNot("electric", saved.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByNameIgnoreCaseAndIdNotFalseWhenNameDoesNotExist() {
        //given
        Category category = TestDataFactory.category("Hybrid", BigDecimal.valueOf(9), BigDecimal.valueOf(15));
        Category saved = categoryRepository.save(category);
        //when
        boolean exists = categoryRepository.existsByNameIgnoreCaseAndIdNot("Diesel", saved.getId());
        //then
        assertThat(exists).isFalse();
    }
}