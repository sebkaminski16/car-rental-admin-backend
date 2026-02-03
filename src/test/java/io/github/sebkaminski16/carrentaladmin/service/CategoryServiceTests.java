package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.CategoryDtos;
import io.github.sebkaminski16.carrentaladmin.entity.Category;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.repository.CarRepository;
import io.github.sebkaminski16.carrentaladmin.repository.CategoryRepository;
import io.github.sebkaminski16.carrentaladmin.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTests {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void testListReturnsAllCategories() {
        //given
        Category category1 = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        category1.setId(1L);
        Category category2 = TestDataFactory.category("Luxury", BigDecimal.valueOf(10.0), BigDecimal.valueOf(20.0));
        category2.setId(2L);

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));
        //when
        List<CategoryDtos.CategoryDto> result = categoryService.list();
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Economy", result.get(0).name());
        assertEquals(BigDecimal.valueOf(5.0), result.get(0).dailyDiscountPercent());
        assertEquals("Luxury", result.get(1).name());
        assertEquals(BigDecimal.valueOf(10.0), result.get(1).dailyDiscountPercent());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testListReturnsEmptyListWhenNoCategories() {
        //given
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<CategoryDtos.CategoryDto> result = categoryService.list();
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetEntityReturnsCategoryWhenExists() {
        //given
        Long categoryId = 1L;
        Category category = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        //when
        Category result = categoryService.getEntity(categoryId);
        //then
        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Economy", result.getName());
        assertEquals(BigDecimal.valueOf(5.0), result.getDailyDiscountPercent());
        assertEquals(BigDecimal.valueOf(15.0), result.getWeeklyDiscountPercent());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetEntityThrowsNotFoundExceptionWhenCategoryDoesNotExist() {
        //given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> categoryService.getEntity(categoryId));

        assertEquals("Category not found: 1", exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetReturnsCategoryDtoWhenExists() {
        //given
        Long categoryId = 1L;
        Category category = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        //when
        CategoryDtos.CategoryDto result = categoryService.get(categoryId);
        //then
        assertNotNull(result);
        assertEquals(categoryId, result.id());
        assertEquals("Economy", result.name());
        assertEquals("Test category", result.description());
        assertEquals(BigDecimal.valueOf(5.0), result.dailyDiscountPercent());
        assertEquals(BigDecimal.valueOf(15.0), result.weeklyDiscountPercent());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetThrowsNotFoundExceptionWhenCategoryDoesNotExist() {
        //given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> categoryService.get(categoryId));

        assertEquals("Category not found: 1", exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testCreateSuccessfullyCreatesNewCategory() {
        //given
        CategoryDtos.CategoryCreateRequest request = new CategoryDtos.CategoryCreateRequest(
                "Economy",
                "Budget-friendly cars",
                BigDecimal.valueOf(5.0),
                BigDecimal.valueOf(15.0)
        );

        Category savedCategory = Category.builder()
                .name("Economy")
                .description("Budget-friendly cars")
                .dailyDiscountPercent(BigDecimal.valueOf(5.0))
                .weeklyDiscountPercent(BigDecimal.valueOf(15.0))
                .build();
        savedCategory.setId(1L);

        when(categoryRepository.existsByNameIgnoreCase("Economy")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        //when
        CategoryDtos.CategoryDto result = categoryService.create(request);
        //when
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Economy", result.name());
        assertEquals("Budget-friendly cars", result.description());
        assertEquals(BigDecimal.valueOf(5.0), result.dailyDiscountPercent());
        assertEquals(BigDecimal.valueOf(15.0), result.weeklyDiscountPercent());
        verify(categoryRepository, times(1)).existsByNameIgnoreCase("Economy");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testCreateSuccessfullyCreatesNewCategoryWithNullDiscounts() {
        //given
        CategoryDtos.CategoryCreateRequest request = new CategoryDtos.CategoryCreateRequest(
                "Economy",
                "Budget-friendly cars",
                null,
                null
        );

        Category savedCategory = Category.builder()
                .name("Economy")
                .description("Budget-friendly cars")
                .dailyDiscountPercent(null)
                .weeklyDiscountPercent(null)
                .build();
        savedCategory.setId(1L);

        when(categoryRepository.existsByNameIgnoreCase("Economy")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        //when
        CategoryDtos.CategoryDto result = categoryService.create(request);
        //then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Economy", result.name());
        assertEquals(BigDecimal.ZERO, result.dailyDiscountPercent());
        assertEquals(BigDecimal.ZERO, result.weeklyDiscountPercent());
        verify(categoryRepository, times(1)).existsByNameIgnoreCase("Economy");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenCategoryAlreadyExists() {
        //given
        CategoryDtos.CategoryCreateRequest request = new CategoryDtos.CategoryCreateRequest(
                "Economy",
                "Budget-friendly cars",
                BigDecimal.valueOf(5.0),
                BigDecimal.valueOf(15.0)
        );

        when(categoryRepository.existsByNameIgnoreCase("Economy")).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> categoryService.create(request));

        assertEquals("Category exists: Economy", exception.getMessage());
        verify(categoryRepository, times(1)).existsByNameIgnoreCase("Economy");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenCategoryExistsCaseInsensitive() {
        //given
        CategoryDtos.CategoryCreateRequest request = new CategoryDtos.CategoryCreateRequest(
                "ECONOMY",
                "Budget-friendly cars",
                BigDecimal.valueOf(5.0),
                BigDecimal.valueOf(15.0)
        );

        when(categoryRepository.existsByNameIgnoreCase("ECONOMY")).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> categoryService.create(request));

        assertEquals("Category exists: ECONOMY", exception.getMessage());
        verify(categoryRepository, times(1)).existsByNameIgnoreCase("ECONOMY");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testUpdateSuccessfullyUpdatesCategory() {
        //given
        Long categoryId = 1L;
        CategoryDtos.CategoryUpdateRequest request = new CategoryDtos.CategoryUpdateRequest(
                "Economy Updated",
                "Updated description",
                BigDecimal.valueOf(7.5),
                BigDecimal.valueOf(20.0)
        );

        Category existingCategory = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        existingCategory.setId(categoryId);

        Category updatedCategory = Category.builder()
                .name("Economy Updated")
                .description("Updated description")
                .dailyDiscountPercent(BigDecimal.valueOf(7.5))
                .weeklyDiscountPercent(BigDecimal.valueOf(20.0))
                .build();
        updatedCategory.setId(categoryId);

        when(categoryRepository.existsByNameIgnoreCaseAndIdNot("Economy Updated", categoryId)).thenReturn(false);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        //when
        CategoryDtos.CategoryDto result = categoryService.update(categoryId, request);
        //then
        assertNotNull(result);
        assertEquals(categoryId, result.id());
        assertEquals("Economy Updated", result.name());
        assertEquals("Updated description", result.description());
        assertEquals(BigDecimal.valueOf(7.5), result.dailyDiscountPercent());
        assertEquals(BigDecimal.valueOf(20.0), result.weeklyDiscountPercent());
        verify(categoryRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Economy Updated", categoryId);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateThrowsNotFoundExceptionWhenCategoryDoesNotExist() {
        //given
        Long categoryId = 1L;
        CategoryDtos.CategoryUpdateRequest request = new CategoryDtos.CategoryUpdateRequest(
                "Economy Updated",
                "Updated description",
                BigDecimal.valueOf(7.5),
                BigDecimal.valueOf(20.0)
        );

        when(categoryRepository.existsByNameIgnoreCaseAndIdNot("Economy Updated", categoryId)).thenReturn(false);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> categoryService.update(categoryId, request));

        assertEquals("Category not found: 1", exception.getMessage());
        verify(categoryRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Economy Updated", categoryId);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testUpdateThrowsBadRequestExceptionWhenNameAlreadyExistsForDifferentCategory() {
        //given
        Long categoryId = 1L;
        CategoryDtos.CategoryUpdateRequest request = new CategoryDtos.CategoryUpdateRequest(
                "Luxury",
                "Updated description",
                BigDecimal.valueOf(7.5),
                BigDecimal.valueOf(20.0)
        );

        when(categoryRepository.existsByNameIgnoreCaseAndIdNot("Luxury", categoryId)).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> categoryService.update(categoryId, request));

        assertEquals("Category exists: Luxury", exception.getMessage());
        verify(categoryRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Luxury", categoryId);
        verify(categoryRepository, never()).findById(any());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testDeleteSuccessfullyDeletesCategory() {
        //given
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(carRepository.existsByCategoryId(categoryId)).thenReturn(false);
        //when
        categoryService.delete(categoryId);
        //then
        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(carRepository, times(1)).existsByCategoryId(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void testDeleteThrowsNotFoundExceptionWhenCategoryDoesNotExist() {
        //given
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> categoryService.delete(categoryId));

        assertEquals("Category not found: 1", exception.getMessage());
        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(carRepository, never()).existsByCategoryId(any());
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteThrowsBadRequestExceptionWhenCarsExist() {
        //given
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(carRepository.existsByCategoryId(categoryId)).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> categoryService.delete(categoryId));

        assertEquals("Cannot delete, because a Car of that category exists!", exception.getMessage());
        verify(categoryRepository, times(1)).existsById(categoryId);
        verify(carRepository, times(1)).existsByCategoryId(categoryId);
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void testSearchReturnsMatchingCategories() {
        //given
        String query = "eco";
        Category category1 = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        category1.setId(1L);
        Category category2 = TestDataFactory.category("Eco-friendly", BigDecimal.valueOf(3.0), BigDecimal.valueOf(10.0));
        category2.setId(2L);

        when(categoryRepository.findByNameContainingIgnoreCase(query))
                .thenReturn(Arrays.asList(category1, category2));
        //when
        List<CategoryDtos.CategoryDto> result = categoryService.search(query);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Economy", result.get(0).name());
        assertEquals("Eco-friendly", result.get(1).name());
        verify(categoryRepository, times(1)).findByNameContainingIgnoreCase(query);
        verify(categoryRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsEmptyListWhenNoMatches() {
        //given
        String query = "xyz";
        when(categoryRepository.findByNameContainingIgnoreCase(query))
                .thenReturn(Collections.emptyList());
        //when
        List<CategoryDtos.CategoryDto> result = categoryService.search(query);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findByNameContainingIgnoreCase(query);
        verify(categoryRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsAllCategoriesWhenQueryIsNull() {
        //given
        Category category1 = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        category1.setId(1L);
        Category category2 = TestDataFactory.category("Luxury", BigDecimal.valueOf(10.0), BigDecimal.valueOf(20.0));
        category2.setId(2L);

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));
        //when
        List<CategoryDtos.CategoryDto> result = categoryService.search(null);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
        verify(categoryRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    void testSearchReturnsAllCategoriesWhenQueryIsBlank() {
        //given
        Category category1 = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        category1.setId(1L);
        Category category2 = TestDataFactory.category("Luxury", BigDecimal.valueOf(10.0), BigDecimal.valueOf(20.0));
        category2.setId(2L);

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));
        //when
        List<CategoryDtos.CategoryDto> result = categoryService.search("   ");
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
        verify(categoryRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    void testSearchReturnsAllCategoriesWhenQueryIsEmpty() {
        //given
        Category category1 = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        category1.setId(1L);
        Category category2 = TestDataFactory.category("Luxury", BigDecimal.valueOf(10.0), BigDecimal.valueOf(20.0));
        category2.setId(2L);

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));
        //when
        List<CategoryDtos.CategoryDto> result = categoryService.search("");
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
        verify(categoryRepository, never()).findByNameContainingIgnoreCase(any());
    }
}