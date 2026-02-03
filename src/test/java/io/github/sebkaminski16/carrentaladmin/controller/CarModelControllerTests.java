package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.CarModelDtos;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.service.CarModelService;
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

@WebMvcTest(CarModelController.class)
public class CarModelControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarModelService carModelService;

    @Test
    public void testListReturnsAllModels() throws Exception {
        //given
        CarModelDtos.CarModelDto model1 = new CarModelDtos.CarModelDto(1L, "Corolla", 1L, "Toyota");
        CarModelDtos.CarModelDto model2 = new CarModelDtos.CarModelDto(2L, "Civic", 2L, "Honda");
        List<CarModelDtos.CarModelDto> models = Arrays.asList(model1, model2);
        when(carModelService.list()).thenReturn(models);
        //when&then
        mockMvc.perform(get("/api/models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Corolla")))
                .andExpect(jsonPath("$[0].brandId", is(1)))
                .andExpect(jsonPath("$[0].brandName", is("Toyota")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Civic")))
                .andExpect(jsonPath("$[1].brandId", is(2)))
                .andExpect(jsonPath("$[1].brandName", is("Honda")));

        verify(carModelService, times(1)).list();
    }

    @Test
    public void testListReturnsEmptyListWhenNoModels() throws Exception {
        //given
        when(carModelService.list()).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(carModelService, times(1)).list();
    }

    @Test
    public void testCreateModelSuccessfully() throws Exception {
        //given
        CarModelDtos.CarModelDto createdModel = new CarModelDtos.CarModelDto(1L, "Camry", 1L, "Toyota");
        when(carModelService.create(any(CarModelDtos.CarModelCreateRequest.class))).thenReturn(createdModel);

        String requestBody = "{\"name\":\"Camry\",\"brandId\":1}";
        //when&then
        mockMvc.perform(post("/api/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Camry")))
                .andExpect(jsonPath("$.brandId", is(1)))
                .andExpect(jsonPath("$.brandName", is("Toyota")));

        verify(carModelService, times(1)).create(any(CarModelDtos.CarModelCreateRequest.class));
    }

    @Test
    public void testCreateModelWithBlankNameReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"name\":\"\",\"brandId\":1}";
        //when&then
        mockMvc.perform(post("/api/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carModelService, never()).create(any(CarModelDtos.CarModelCreateRequest.class));
    }

    @Test
    public void testCreateModelWithNullNameReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"name\":null,\"brandId\":1}";
        //when&then
        mockMvc.perform(post("/api/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carModelService, never()).create(any(CarModelDtos.CarModelCreateRequest.class));
    }

    @Test
    public void testCreateModelWithNullBrandIdReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"name\":\"Accord\",\"brandId\":null}";
        //when&then
        mockMvc.perform(post("/api/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carModelService, never()).create(any(CarModelDtos.CarModelCreateRequest.class));
    }

    @Test
    public void testCreateModelWithTooLongNameReturnsValidationError() throws Exception {
        //given
        String longName = "A".repeat(121);
        String requestBody = "{\"name\":\"" + longName + "\",\"brandId\":1}";
        //when&then
        mockMvc.perform(post("/api/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carModelService, never()).create(any(CarModelDtos.CarModelCreateRequest.class));
    }

    @Test
    public void testCreateModelWhenAlreadyExistsReturnsBadRequest() throws Exception {
        //given
        when(carModelService.create(any(CarModelDtos.CarModelCreateRequest.class)))
                .thenThrow(new BadRequestException("Model already exists: Corolla"));

        String requestBody = "{\"name\":\"Corolla\",\"brandId\":1}";
        //when&then
        mockMvc.perform(post("/api/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carModelService, times(1)).create(any(CarModelDtos.CarModelCreateRequest.class));
    }

    @Test
    public void testCreateModelWhenBrandNotFoundReturnsNotFound() throws Exception {
        //given
        when(carModelService.create(any(CarModelDtos.CarModelCreateRequest.class)))
                .thenThrow(new NotFoundException("Brand not found: 999"));

        String requestBody = "{\"name\":\"Model X\",\"brandId\":999}";
        //when&then
        mockMvc.perform(post("/api/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(carModelService, times(1)).create(any(CarModelDtos.CarModelCreateRequest.class));
    }

    @Test
    public void testGetModelByIdSuccessfully() throws Exception {
        //given
        CarModelDtos.CarModelDto model = new CarModelDtos.CarModelDto(1L, "Accord", 2L, "Honda");
        when(carModelService.get(1L)).thenReturn(model);
        //when&then
        mockMvc.perform(get("/api/models/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Accord")))
                .andExpect(jsonPath("$.brandId", is(2)))
                .andExpect(jsonPath("$.brandName", is("Honda")));

        verify(carModelService, times(1)).get(1L);
    }

    @Test
    public void testGetModelByIdWhenNotFoundReturnsNotFound() throws Exception {
        //given
        when(carModelService.get(999L))
                .thenThrow(new NotFoundException("Model not found: 999"));
        //when&then
        mockMvc.perform(get("/api/models/999"))
                .andExpect(status().isNotFound());

        verify(carModelService, times(1)).get(999L);
    }

    @Test
    public void testUpdateModelSuccessfully() throws Exception {
        //given
        CarModelDtos.CarModelDto updatedModel = new CarModelDtos.CarModelDto(1L, "Camry V6", 1L, "Toyota");
        when(carModelService.update(eq(1L), any(CarModelDtos.CarModelUpdateRequest.class)))
                .thenReturn(updatedModel);

        String requestBody = "{\"name\":\"Camry V6\",\"brandId\":1}";
        //when&then
        mockMvc.perform(put("/api/models/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Camry V6")))
                .andExpect(jsonPath("$.brandId", is(1)))
                .andExpect(jsonPath("$.brandName", is("Toyota")));

        verify(carModelService, times(1)).update(eq(1L), any(CarModelDtos.CarModelUpdateRequest.class));
    }

    @Test
    public void testUpdateModelWithBlankNameReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"name\":\"\",\"brandId\":1}";

        //when&then
        mockMvc.perform(put("/api/models/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carModelService, never()).update(any(Long.class), any(CarModelDtos.CarModelUpdateRequest.class));
    }

    @Test
    public void testUpdateModelWithNullBrandIdReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"name\":\"Civic\",\"brandId\":null}";
        //when&then
        mockMvc.perform(put("/api/models/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carModelService, never()).update(any(Long.class), any(CarModelDtos.CarModelUpdateRequest.class));
    }

    @Test
    public void testUpdateModelWhenNotFoundReturnsNotFound() throws Exception {
        //given
        when(carModelService.update(eq(999L), any(CarModelDtos.CarModelUpdateRequest.class)))
                .thenThrow(new NotFoundException("Model not found: 999"));

        String requestBody = "{\"name\":\"Accord\",\"brandId\":2}";
        //when&then
        mockMvc.perform(put("/api/models/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(carModelService, times(1)).update(eq(999L), any(CarModelDtos.CarModelUpdateRequest.class));
    }

    @Test
    public void testUpdateModelWhenNameAlreadyExistsReturnsBadRequest() throws Exception {
        //given
        when(carModelService.update(eq(1L), any(CarModelDtos.CarModelUpdateRequest.class)))
                .thenThrow(new BadRequestException("Model already exists: Corolla"));

        String requestBody = "{\"name\":\"Corolla\",\"brandId\":1}";
        //when&then
        mockMvc.perform(put("/api/models/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(carModelService, times(1)).update(eq(1L), any(CarModelDtos.CarModelUpdateRequest.class));
    }

    @Test
    public void testUpdateModelWhenBrandNotFoundReturnsNotFound() throws Exception {
        //given
        when(carModelService.update(eq(1L), any(CarModelDtos.CarModelUpdateRequest.class)))
                .thenThrow(new NotFoundException("Brand not found: 999"));

        String requestBody = "{\"name\":\"Model Y\",\"brandId\":999}";
        //when&then
        mockMvc.perform(put("/api/models/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(carModelService, times(1)).update(eq(1L), any(CarModelDtos.CarModelUpdateRequest.class));
    }

    @Test
    public void testDeleteModelSuccessfully() throws Exception {
        //given
        doNothing().when(carModelService).delete(1L);
        //when&then
        mockMvc.perform(delete("/api/models/1"))
                .andExpect(status().isNoContent());

        verify(carModelService, times(1)).delete(1L);
    }

    @Test
    public void testDeleteModelWhenNotFoundReturnsNotFound() throws Exception {
        //given
        doThrow(new NotFoundException("Model not found: 999"))
                .when(carModelService).delete(999L);
        //when&then
        mockMvc.perform(delete("/api/models/999"))
                .andExpect(status().isNotFound());

        verify(carModelService, times(1)).delete(999L);
    }

    @Test
    public void testDeleteModelWhenHasCarsReturnsBadRequest() throws Exception {
        //given
        doThrow(new BadRequestException("Cannot delete, because a car of that model exists!"))
                .when(carModelService).delete(1L);
        //when&then
        mockMvc.perform(delete("/api/models/1"))
                .andExpect(status().isBadRequest());

        verify(carModelService, times(1)).delete(1L);
    }

    @Test
    public void testSearchModelsWithQueryReturnsMatchingModels() throws Exception {
        //given
        CarModelDtos.CarModelDto model1 = new CarModelDtos.CarModelDto(1L, "Corolla", 1L, "Toyota");
        CarModelDtos.CarModelDto model2 = new CarModelDtos.CarModelDto(2L, "Camry", 1L, "Toyota");
        List<CarModelDtos.CarModelDto> models = Arrays.asList(model1, model2);
        when(carModelService.search("cam")).thenReturn(models);
        //when&then
        mockMvc.perform(get("/api/models/search")
                        .param("query", "cam"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Corolla")))
                .andExpect(jsonPath("$[1].name", is("Camry")));

        verify(carModelService, times(1)).search("cam");
    }

    @Test
    public void testSearchModelsWithNullQueryReturnsAllModels() throws Exception {
        //given
        CarModelDtos.CarModelDto model1 = new CarModelDtos.CarModelDto(1L, "Corolla", 1L, "Toyota");
        List<CarModelDtos.CarModelDto> models = List.of(model1);
        when(carModelService.search(null)).thenReturn(models);
        //when&then
        mockMvc.perform(get("/api/models/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(carModelService, times(1)).search(null);
    }

    @Test
    public void testSearchModelsWithEmptyQueryReturnsAllModels() throws Exception {
        //given
        CarModelDtos.CarModelDto model1 = new CarModelDtos.CarModelDto(1L, "Accord", 2L, "Honda");
        List<CarModelDtos.CarModelDto> models = List.of(model1);
        when(carModelService.search("")).thenReturn(models);
        //when&then
        mockMvc.perform(get("/api/models/search")
                        .param("query", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(carModelService, times(1)).search("");
    }

    @Test
    public void testSearchModelsWithNoMatchesReturnsEmptyList() throws Exception {
        //given
        when(carModelService.search("xyz")).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/models/search")
                        .param("query", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(carModelService, times(1)).search("xyz");
    }

    @Test
    public void testByBrandReturnsModelsForBrand() throws Exception {
        //given
        CarModelDtos.CarModelDto model1 = new CarModelDtos.CarModelDto(1L, "Corolla", 1L, "Toyota");
        CarModelDtos.CarModelDto model2 = new CarModelDtos.CarModelDto(2L, "Camry", 1L, "Toyota");
        List<CarModelDtos.CarModelDto> models = Arrays.asList(model1, model2);
        when(carModelService.listByBrand(1L)).thenReturn(models);

        //when&then
        mockMvc.perform(get("/api/models/by-brand/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Corolla")))
                .andExpect(jsonPath("$[0].brandId", is(1)))
                .andExpect(jsonPath("$[1].name", is("Camry")))
                .andExpect(jsonPath("$[1].brandId", is(1)));

        verify(carModelService, times(1)).listByBrand(1L);
    }

    @Test
    public void testByBrandReturnsEmptyListWhenNoModels() throws Exception {
        //given
        when(carModelService.listByBrand(999L)).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/models/by-brand/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(carModelService, times(1)).listByBrand(999L);
    }
}