package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.BrandDtos;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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

@WebMvcTest(BrandController.class)
public class BrandControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrandService brandService;

    @Test
    public void testListReturnsAllBrands() throws Exception {
        List<BrandDtos.BrandDto> brands = Arrays.asList(new BrandDtos.BrandDto(1L, "Toyota"), new BrandDtos.BrandDto(2L, "Honda"));
        when(brandService.list()).thenReturn(brands);
        mockMvc.perform(get("/api/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Toyota")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Honda")));

        verify(brandService, times(1)).list();
    }

    @Test
    public void testListReturnsEmptyListWhenNoBrands() throws Exception {
        when(brandService.list()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(brandService, times(1)).list();
    }

    @Test
    public void testCreateBrandSuccessfully() throws Exception {
        BrandDtos.BrandDto createdBrand = new BrandDtos.BrandDto(1L, "Tesla");
        when(brandService.create(any(BrandDtos.BrandCreateRequest.class))).thenReturn(createdBrand);
        String requestBody = "{\"name\":\"Tesla\"}";
        mockMvc.perform(post("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Tesla")));

        verify(brandService, times(1)).create(any(BrandDtos.BrandCreateRequest.class));
    }

    @Test
    public void testCreateBrandWithBlankNameReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(brandService, never()).create(any(BrandDtos.BrandCreateRequest.class));
    }

    @Test
    public void testCreateBrandWithNullNameReturnsValidationError() throws Exception {
        String requestBody = "{\"name\":null}";
        mockMvc.perform(post("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(brandService, never()).create(any(BrandDtos.BrandCreateRequest.class));
    }

    @Test
    public void testCreateBrandWithTooLongNameReturnsValidationError() throws Exception {
        String longName = "A".repeat(121);
        String requestBody = "{\"name\":\"" + longName + "\"}";
        mockMvc.perform(post("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(brandService, never()).create(any(BrandDtos.BrandCreateRequest.class));
    }

    @Test
    public void testCreateBrandWhenAlreadyExistsReturnsBadRequest() throws Exception {
        when(brandService.create(any(BrandDtos.BrandCreateRequest.class)))
                .thenThrow(new BadRequestException("Brand already exists: Toyota"));
        mockMvc.perform(post("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Toyota\"}"))
                .andExpect(status().isBadRequest());

        verify(brandService, times(1)).create(any(BrandDtos.BrandCreateRequest.class));
    }

    @Test
    public void testGetBrandByIdSuccessfully() throws Exception {
        BrandDtos.BrandDto brand = new BrandDtos.BrandDto(1L, "BMW");
        when(brandService.get(1L)).thenReturn(brand);
        mockMvc.perform(get("/api/brands/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("BMW")));

        verify(brandService, times(1)).get(1L);
    }

    @Test
    public void testGetBrandByIdWhenNotFoundReturnsNotFound() throws Exception {
        when(brandService.get(999L))
                .thenThrow(new NotFoundException("Brand not found: 999"));
        mockMvc.perform(get("/api/brands/999"))
                .andExpect(status().isNotFound());

        verify(brandService, times(1)).get(999L);
    }

    @Test
    public void testUpdateBrandSuccessfully() throws Exception {
        BrandDtos.BrandDto updatedBrand = new BrandDtos.BrandDto(1L, "Mercedes");
        when(brandService.update(eq(1L), any(BrandDtos.BrandUpdateRequest.class)))
                .thenReturn(updatedBrand);
        String requestBody = "{\"name\":\"Mercedes\"}";
        mockMvc.perform(put("/api/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Mercedes")));

        verify(brandService, times(1)).update(eq(1L), any(BrandDtos.BrandUpdateRequest.class));
    }

    @Test
    public void testUpdateBrandWithBlankNameReturnsValidationError() throws Exception {
        mockMvc.perform(put("/api/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(brandService, never()).update(any(Long.class), any(BrandDtos.BrandUpdateRequest.class));
    }

    @Test
    public void testUpdateBrandWhenNotFoundReturnsNotFound() throws Exception {
        when(brandService.update(eq(999L), any(BrandDtos.BrandUpdateRequest.class)))
                .thenThrow(new NotFoundException("Brand not found: 999"));
        String requestBody = "{\"name\":\"Audi\"}";
        mockMvc.perform(put("/api/brands/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(brandService, times(1)).update(eq(999L), any(BrandDtos.BrandUpdateRequest.class));
    }

    @Test
    public void testUpdateBrandWhenNameAlreadyExistsReturnsBadRequest() throws Exception {
        when(brandService.update(eq(1L), any(BrandDtos.BrandUpdateRequest.class)))
                .thenThrow(new BadRequestException("Brand already exists: Toyota"));
        mockMvc.perform(put("/api/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Toyota\"}"))
                .andExpect(status().isBadRequest());

        verify(brandService, times(1)).update(eq(1L), any(BrandDtos.BrandUpdateRequest.class));
    }

    @Test
    public void testDeleteBrandSuccessfully() throws Exception {
        doNothing().when(brandService).delete(1L);
        mockMvc.perform(delete("/api/brands/1"))
                .andExpect(status().isNoContent());

        verify(brandService, times(1)).delete(1L);
    }

    @Test
    public void testDeleteBrandWhenNotFoundReturnsNotFound() throws Exception {
        doThrow(new NotFoundException("Brand not found: 999"))
                .when(brandService).delete(999L);
        mockMvc.perform(delete("/api/brands/999"))
                .andExpect(status().isNotFound());

        verify(brandService, times(1)).delete(999L);
    }

    @Test
    public void testDeleteBrandWhenHasModelsReturnsBadRequest() throws Exception {
        doThrow(new BadRequestException("Cannot delete, because a model of that brand exists!"))
                .when(brandService).delete(1L);
        mockMvc.perform(delete("/api/brands/1"))
                .andExpect(status().isBadRequest());

        verify(brandService, times(1)).delete(1L);
    }

    @Test
    public void testSearchBrandsWithQueryReturnsMatchingBrands() throws Exception {
        List<BrandDtos.BrandDto> brands = Arrays.asList(new BrandDtos.BrandDto(1L, "Toyota"), new BrandDtos.BrandDto(2L, "Honda"));
        when(brandService.search("ota")).thenReturn(brands);
        mockMvc.perform(get("/api/brands/search")
                        .param("query", "ota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Toyota")))
                .andExpect(jsonPath("$[1].name", is("Honda")));

        verify(brandService, times(1)).search("ota");
    }

    @Test
    public void testSearchBrandsWithNullQueryReturnsAllBrands() throws Exception {
        List<BrandDtos.BrandDto> brands = Arrays.asList(new BrandDtos.BrandDto(1L, "Toyota"), new BrandDtos.BrandDto(2L, "Honda"));
        when(brandService.search(null)).thenReturn(brands);
        mockMvc.perform(get("/api/brands/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(brandService, times(1)).search(null);
    }

    @Test
    public void testSearchBrandsWithEmptyQueryReturnsAllBrands() throws Exception {
        List<BrandDtos.BrandDto> brands = List.of(new BrandDtos.BrandDto(1L, "Toyota"));
        when(brandService.search("")).thenReturn(brands);
        mockMvc.perform(get("/api/brands/search")
                        .param("query", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(brandService, times(1)).search("");
    }

    @Test
    public void testSearchBrandsWithNoMatchesReturnsEmptyList() throws Exception {
        when(brandService.search("xyz")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/brands/search")
                        .param("query", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(brandService, times(1)).search("xyz");
    }
}