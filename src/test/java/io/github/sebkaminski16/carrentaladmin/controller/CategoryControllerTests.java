package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.CategoryDtos;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    public void testListReturnsAllCategories() throws Exception {
        //given
        CategoryDtos.CategoryDto category1 = new CategoryDtos.CategoryDto(
                1L, "Economy", "Budget-friendly cars", new BigDecimal("5.00"), new BigDecimal("10.00")
        );
        CategoryDtos.CategoryDto category2 = new CategoryDtos.CategoryDto(
                2L, "Luxury", "Premium cars", new BigDecimal("10.00"), new BigDecimal("15.00")
        );
        List<CategoryDtos.CategoryDto> categories = Arrays.asList(category1, category2);
        when(categoryService.list()).thenReturn(categories);
        //when&then
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Economy")))
                .andExpect(jsonPath("$[0].dailyDiscountPercent", is(5.00)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Luxury")));

        verify(categoryService, times(1)).list();
    }

    @Test
    public void testListReturnsEmptyListWhenNoCategories() throws Exception {
        //given
        when(categoryService.list()).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(categoryService, times(1)).list();
    }

    @Test
    public void testCreateCategorySuccessfully() throws Exception {
        //given
        CategoryDtos.CategoryDto createdCategory = new CategoryDtos.CategoryDto(
                1L, "SUV", "Sport utility vehicles", new BigDecimal("7.50"), new BigDecimal("12.50")
        );
        when(categoryService.create(any(CategoryDtos.CategoryCreateRequest.class))).thenReturn(createdCategory);

        String requestBody = "{\"name\":\"SUV\",\"description\":\"Sport utility vehicles\",\"dailyDiscountPercent\":7.50,\"weeklyDiscountPercent\":12.50}";
        //when&then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("SUV")))
                .andExpect(jsonPath("$.dailyDiscountPercent", is(7.50)))
                .andExpect(jsonPath("$.weeklyDiscountPercent", is(12.50)));

        verify(categoryService, times(1)).create(any(CategoryDtos.CategoryCreateRequest.class));
    }

    @Test
    public void testCreateCategoryWithBlankNameReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"name\":\"\",\"description\":\"Test\",\"dailyDiscountPercent\":5.00,\"weeklyDiscountPercent\":10.00}";
        //when&then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).create(any(CategoryDtos.CategoryCreateRequest.class));
    }

    @Test
    public void testCreateCategoryWithNullNameReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"name\":null,\"description\":\"Test\",\"dailyDiscountPercent\":5.00,\"weeklyDiscountPercent\":10.00}";
        //when&then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).create(any(CategoryDtos.CategoryCreateRequest.class));
    }

    @Test
    public void testCreateCategoryWithTooLongNameReturnsValidationError() throws Exception {
        //given
        String longName = "A".repeat(121);
        String requestBody = "{\"name\":\"" + longName + "\",\"description\":\"Test\",\"dailyDiscountPercent\":5.00,\"weeklyDiscountPercent\":10.00}";
        //when&then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).create(any(CategoryDtos.CategoryCreateRequest.class));
    }

    @Test
    public void testCreateCategoryWithTooLongDescriptionReturnsValidationError() throws Exception {
        //given
        String longDescription = "A".repeat(256);
        String requestBody = "{\"name\":\"Economy\",\"description\":\"" + longDescription + "\",\"dailyDiscountPercent\":5.00,\"weeklyDiscountPercent\":10.00}";
        //when&then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).create(any(CategoryDtos.CategoryCreateRequest.class));
    }

    @Test
    public void testCreateCategoryWithNegativeDailyDiscountReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"name\":\"Economy\",\"description\":\"Test\",\"dailyDiscountPercent\":-5.00,\"weeklyDiscountPercent\":10.00}";
        //when&then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).create(any(CategoryDtos.CategoryCreateRequest.class));
    }

    @Test
    public void testCreateCategoryWithTooLargeDailyDiscountReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"name\":\"Economy\",\"description\":\"Test\",\"dailyDiscountPercent\":101.00,\"weeklyDiscountPercent\":10.00}";
        //when&then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).create(any(CategoryDtos.CategoryCreateRequest.class));
    }

    @Test
    public void testCreateCategoryWithTooLargeWeeklyDiscountReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"name\":\"Economy\",\"description\":\"Test\",\"dailyDiscountPercent\":5.00,\"weeklyDiscountPercent\":150.00}";
        //when&then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).create(any(CategoryDtos.CategoryCreateRequest.class));
    }

    @Test
    public void testCreateCategoryWhenAlreadyExistsReturnsBadRequest() throws Exception {
        //given
        when(categoryService.create(any(CategoryDtos.CategoryCreateRequest.class)))
                .thenThrow(new BadRequestException("Category exists: Economy"));

        String requestBody = "{\"name\":\"Economy\",\"description\":\"Test\",\"dailyDiscountPercent\":5.00,\"weeklyDiscountPercent\":10.00}";
        //when&then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(categoryService, times(1)).create(any(CategoryDtos.CategoryCreateRequest.class));
    }

    @Test
    public void testGetCategoryByIdSuccessfully() throws Exception {
        //given
        CategoryDtos.CategoryDto category = new CategoryDtos.CategoryDto(
                1L, "Economy", "Budget-friendly cars", new BigDecimal("5.00"), new BigDecimal("10.00")
        );
        when(categoryService.get(1L)).thenReturn(category);
        //when&then
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Economy")))
                .andExpect(jsonPath("$.description", is("Budget-friendly cars")));

        verify(categoryService, times(1)).get(1L);
    }

    @Test
    public void testGetCategoryByIdWhenNotFoundReturnsNotFound() throws Exception {
        //given
        when(categoryService.get(999L))
                .thenThrow(new NotFoundException("Category not found: 999"));
        //when&then
        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).get(999L);
    }

    @Test
    public void testUpdateCategorySuccessfully() throws Exception {
        //given
        CategoryDtos.CategoryDto updatedCategory = new CategoryDtos.CategoryDto(
                1L, "Premium Economy", "Updated description", new BigDecimal("7.00"), new BigDecimal("12.00")
        );
        when(categoryService.update(eq(1L), any(CategoryDtos.CategoryUpdateRequest.class)))
                .thenReturn(updatedCategory);

        String requestBody = "{\"name\":\"Premium Economy\",\"description\":\"Updated description\",\"dailyDiscountPercent\":7.00,\"weeklyDiscountPercent\":12.00}";
        //when&then
        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Premium Economy")))
                .andExpect(jsonPath("$.dailyDiscountPercent", is(7.00)));

        verify(categoryService, times(1)).update(eq(1L), any(CategoryDtos.CategoryUpdateRequest.class));
    }

    @Test
    public void testUpdateCategoryWithBlankNameReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"name\":\"\",\"description\":\"Test\",\"dailyDiscountPercent\":5.00,\"weeklyDiscountPercent\":10.00}";
        //when&then
        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).update(any(Long.class), any(CategoryDtos.CategoryUpdateRequest.class));
    }

    @Test
    public void testUpdateCategoryWhenNotFoundReturnsNotFound() throws Exception {
        //given
        when(categoryService.update(eq(999L), any(CategoryDtos.CategoryUpdateRequest.class)))
                .thenThrow(new NotFoundException("Category not found: 999"));

        String requestBody = "{\"name\":\"Economy\",\"description\":\"Test\",\"dailyDiscountPercent\":5.00,\"weeklyDiscountPercent\":10.00}";
        //when&then
        mockMvc.perform(put("/api/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).update(eq(999L), any(CategoryDtos.CategoryUpdateRequest.class));
    }

    @Test
    public void testUpdateCategoryWhenNameAlreadyExistsReturnsBadRequest() throws Exception {
        //given
        when(categoryService.update(eq(1L), any(CategoryDtos.CategoryUpdateRequest.class)))
                .thenThrow(new BadRequestException("Category exists: Luxury"));

        String requestBody = "{\"name\":\"Luxury\",\"description\":\"Test\",\"dailyDiscountPercent\":5.00,\"weeklyDiscountPercent\":10.00}";
        //when&then
        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(categoryService, times(1)).update(eq(1L), any(CategoryDtos.CategoryUpdateRequest.class));
    }

    @Test
    public void testDeleteCategorySuccessfully() throws Exception {
        //given
        doNothing().when(categoryService).delete(1L);
        //when&then
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).delete(1L);
    }

    @Test
    public void testDeleteCategoryWhenNotFoundReturnsNotFound() throws Exception {
        //given
        doThrow(new NotFoundException("Category not found: 999"))
                .when(categoryService).delete(999L);
        //when&then
        mockMvc.perform(delete("/api/categories/999"))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).delete(999L);
    }

    @Test
    public void testDeleteCategoryWhenHasCarsReturnsBadRequest() throws Exception {
        //given
        doThrow(new BadRequestException("Cannot delete, because a Car of that category exists!"))
                .when(categoryService).delete(1L);
        //when&then
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isBadRequest());

        verify(categoryService, times(1)).delete(1L);
    }

    @Test
    public void testSearchCategoriesWithQueryReturnsMatchingCategories() throws Exception {
        //given
        CategoryDtos.CategoryDto category1 = new CategoryDtos.CategoryDto(
                1L, "Economy", "Budget-friendly cars", new BigDecimal("5.00"), new BigDecimal("10.00")
        );
        CategoryDtos.CategoryDto category2 = new CategoryDtos.CategoryDto(
                2L, "Premium Economy", "Better economy cars", new BigDecimal("7.00"), new BigDecimal("12.00")
        );
        List<CategoryDtos.CategoryDto> categories = Arrays.asList(category1, category2);
        when(categoryService.search("economy")).thenReturn(categories);
        //when&then
        mockMvc.perform(get("/api/categories/search")
                        .param("query", "economy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Economy")))
                .andExpect(jsonPath("$[1].name", is("Premium Economy")));

        verify(categoryService, times(1)).search("economy");
    }

    @Test
    public void testSearchCategoriesWithNullQueryReturnsAllCategories() throws Exception {
        //given
        CategoryDtos.CategoryDto category1 = new CategoryDtos.CategoryDto(
                1L, "Economy", "Budget-friendly cars", new BigDecimal("5.00"), new BigDecimal("10.00")
        );
        List<CategoryDtos.CategoryDto> categories = List.of(category1);
        when(categoryService.search(null)).thenReturn(categories);
        //when&then
        mockMvc.perform(get("/api/categories/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(categoryService, times(1)).search(null);
    }

    @Test
    public void testSearchCategoriesWithEmptyQueryReturnsAllCategories() throws Exception {
        //given
        CategoryDtos.CategoryDto category1 = new CategoryDtos.CategoryDto(
                1L, "Economy", "Budget-friendly cars", new BigDecimal("5.00"), new BigDecimal("10.00")
        );
        List<CategoryDtos.CategoryDto> categories = List.of(category1);
        when(categoryService.search("")).thenReturn(categories);
        //when&then
        mockMvc.perform(get("/api/categories/search")
                        .param("query", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(categoryService, times(1)).search("");
    }

    @Test
    public void testSearchCategoriesWithNoMatchesReturnsEmptyList() throws Exception {
        //given
        when(categoryService.search("xyz")).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/categories/search")
                        .param("query", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(categoryService, times(1)).search("xyz");
    }
}