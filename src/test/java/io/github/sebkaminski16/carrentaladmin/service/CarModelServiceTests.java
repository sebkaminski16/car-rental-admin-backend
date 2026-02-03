package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.CarModelDtos;
import io.github.sebkaminski16.carrentaladmin.entity.Brand;
import io.github.sebkaminski16.carrentaladmin.entity.CarModel;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.repository.CarModelRepository;
import io.github.sebkaminski16.carrentaladmin.repository.CarRepository;
import io.github.sebkaminski16.carrentaladmin.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarModelServiceTests {

    @Mock
    private BrandService brandService;

    @Mock
    private CarModelRepository carModelRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarModelService carModelService;

    @Test
    void testListReturnsAllCarModels() {
        //given
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        Brand honda = TestDataFactory.brand("Honda");
        honda.setId(2L);

        CarModel model1 = TestDataFactory.model("Corolla", toyota);
        model1.setId(1L);
        CarModel model2 = TestDataFactory.model("Civic", honda);
        model2.setId(2L);

        when(carModelRepository.findAll()).thenReturn(Arrays.asList(model1, model2));
        //when
        List<CarModelDtos.CarModelDto> result = carModelService.list();
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Corolla", result.get(0).name());
        assertEquals("Toyota", result.get(0).brandName());
        assertEquals("Civic", result.get(1).name());
        assertEquals("Honda", result.get(1).brandName());
        verify(carModelRepository, times(1)).findAll();
    }

    @Test
    void testListReturnsEmptyListWhenNoModels() {
        //given
        when(carModelRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<CarModelDtos.CarModelDto> result = carModelService.list();
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carModelRepository, times(1)).findAll();
    }

    @Test
    void testGetEntityReturnsCarModelWhenExists() {
        //given
        Long modelId = 1L;
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel model = TestDataFactory.model("Corolla", toyota);
        model.setId(modelId);

        when(carModelRepository.findById(modelId)).thenReturn(Optional.of(model));
        //when
        CarModel result = carModelService.getEntity(modelId);
        //then
        assertNotNull(result);
        assertEquals(modelId, result.getId());
        assertEquals("Corolla", result.getName());
        assertEquals("Toyota", result.getBrand().getName());
        verify(carModelRepository, times(1)).findById(modelId);
    }

    @Test
    void testGetEntityThrowsNotFoundExceptionWhenModelDoesNotExist() {
        //given
        Long modelId = 1L;
        when(carModelRepository.findById(modelId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carModelService.getEntity(modelId));

        assertEquals("Model not found: 1", exception.getMessage());
        verify(carModelRepository, times(1)).findById(modelId);
    }

    @Test
    void testGetReturnsCarModelDtoWhenExists() {
        //given
        Long modelId = 1L;
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel model = TestDataFactory.model("Corolla", toyota);
        model.setId(modelId);

        when(carModelRepository.findById(modelId)).thenReturn(Optional.of(model));
        //when
        CarModelDtos.CarModelDto result = carModelService.get(modelId);
        //then
        assertNotNull(result);
        assertEquals(modelId, result.id());
        assertEquals("Corolla", result.name());
        assertEquals(Long.valueOf(1), result.brandId());
        assertEquals("Toyota", result.brandName());
        verify(carModelRepository, times(1)).findById(modelId);
    }

    @Test
    void testGetThrowsNotFoundExceptionWhenModelDoesNotExist() {
        //given
        Long modelId = 1L;
        when(carModelRepository.findById(modelId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carModelService.get(modelId));

        assertEquals("Model not found: 1", exception.getMessage());
        verify(carModelRepository, times(1)).findById(modelId);
    }

    @Test
    void testListByBrandReturnsModelsForSpecificBrand() {
        //given
        Long brandId = 1L;
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(brandId);

        CarModel model1 = TestDataFactory.model("Corolla", toyota);
        model1.setId(1L);
        CarModel model2 = TestDataFactory.model("Camry", toyota);
        model2.setId(2L);

        when(carModelRepository.findByBrandId(brandId)).thenReturn(Arrays.asList(model1, model2));
        //when
        List<CarModelDtos.CarModelDto> result = carModelService.listByBrand(brandId);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Corolla", result.get(0).name());
        assertEquals("Camry", result.get(1).name());
        assertEquals(brandId, result.get(0).brandId());
        assertEquals(brandId, result.get(1).brandId());
        verify(carModelRepository, times(1)).findByBrandId(brandId);
    }

    @Test
    void testListByBrandReturnsEmptyListWhenNoModelsForBrand() {
        //given
        Long brandId = 1L;
        when(carModelRepository.findByBrandId(brandId)).thenReturn(Collections.emptyList());
        //when
        List<CarModelDtos.CarModelDto> result = carModelService.listByBrand(brandId);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carModelRepository, times(1)).findByBrandId(brandId);
    }

    @Test
    void testCreateSuccessfullyCreatesNewCarModel() {
        //given
        Long brandId = 1L;
        CarModelDtos.CarModelCreateRequest request = new CarModelDtos.CarModelCreateRequest("Corolla", brandId);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(brandId);

        CarModel savedModel = TestDataFactory.model("Corolla", toyota);
        savedModel.setId(1L);

        when(carModelRepository.existsByNameIgnoreCase("Corolla")).thenReturn(false);
        when(brandService.getEntity(brandId)).thenReturn(toyota);
        when(carModelRepository.save(any(CarModel.class))).thenReturn(savedModel);
        //when
        CarModelDtos.CarModelDto result = carModelService.create(request);
        //then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Corolla", result.name());
        assertEquals(brandId, result.brandId());
        assertEquals("Toyota", result.brandName());
        verify(carModelRepository, times(1)).existsByNameIgnoreCase("Corolla");
        verify(brandService, times(1)).getEntity(brandId);
        verify(carModelRepository, times(1)).save(any(CarModel.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenModelAlreadyExists() {
        //given
        Long brandId = 1L;
        CarModelDtos.CarModelCreateRequest request = new CarModelDtos.CarModelCreateRequest("Corolla", brandId);

        when(carModelRepository.existsByNameIgnoreCase("Corolla")).thenReturn(true);
        //whn&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carModelService.create(request));

        assertEquals("Model already exists: Corolla", exception.getMessage());
        verify(carModelRepository, times(1)).existsByNameIgnoreCase("Corolla");
        verify(brandService, never()).getEntity(any());
        verify(carModelRepository, never()).save(any(CarModel.class));
    }

    @Test
    void testCreateThrowsNotFoundExceptionWhenBrandDoesNotExist() {
        //given
        Long brandId = 1L;
        CarModelDtos.CarModelCreateRequest request = new CarModelDtos.CarModelCreateRequest("Corolla", brandId);

        when(carModelRepository.existsByNameIgnoreCase("Corolla")).thenReturn(false);
        when(brandService.getEntity(brandId)).thenThrow(new NotFoundException("Brand not found: 1"));
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carModelService.create(request));

        assertEquals("Brand not found: 1", exception.getMessage());
        verify(carModelRepository, times(1)).existsByNameIgnoreCase("Corolla");
        verify(brandService, times(1)).getEntity(brandId);
        verify(carModelRepository, never()).save(any(CarModel.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenModelExistsCaseInsensitive() {
        //given
        Long brandId = 1L;
        CarModelDtos.CarModelCreateRequest request = new CarModelDtos.CarModelCreateRequest("COROLLA", brandId);

        when(carModelRepository.existsByNameIgnoreCase("COROLLA")).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carModelService.create(request));

        assertEquals("Model already exists: COROLLA", exception.getMessage());
        verify(carModelRepository, times(1)).existsByNameIgnoreCase("COROLLA");
        verify(brandService, never()).getEntity(any());
        verify(carModelRepository, never()).save(any(CarModel.class));
    }

    @Test
    void testUpdateSuccessfullyUpdatesCarModel() {
        //given
        Long modelId = 1L;
        Long oldBrandId = 1L;
        Long newBrandId = 2L;
        CarModelDtos.CarModelUpdateRequest request = new CarModelDtos.CarModelUpdateRequest("Corolla Updated", newBrandId);

        Brand oldBrand = TestDataFactory.brand("Toyota");
        oldBrand.setId(oldBrandId);
        Brand newBrand = TestDataFactory.brand("Honda");
        newBrand.setId(newBrandId);

        CarModel existingModel = TestDataFactory.model("Corolla", oldBrand);
        existingModel.setId(modelId);

        CarModel updatedModel = TestDataFactory.model("Corolla Updated", newBrand);
        updatedModel.setId(modelId);

        when(carModelRepository.existsByNameIgnoreCaseAndIdNot("Corolla Updated", modelId)).thenReturn(false);
        when(carModelRepository.findById(modelId)).thenReturn(Optional.of(existingModel));
        when(brandService.getEntity(newBrandId)).thenReturn(newBrand);
        when(carModelRepository.save(any(CarModel.class))).thenReturn(updatedModel);
        //when
        CarModelDtos.CarModelDto result = carModelService.update(modelId, request);
        //then
        assertNotNull(result);
        assertEquals(modelId, result.id());
        assertEquals("Corolla Updated", result.name());
        assertEquals(newBrandId, result.brandId());
        assertEquals("Honda", result.brandName());
        verify(carModelRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Corolla Updated", modelId);
        verify(carModelRepository, times(1)).findById(modelId);
        verify(brandService, times(1)).getEntity(newBrandId);
        verify(carModelRepository, times(1)).save(any(CarModel.class));
    }

    @Test
    void testUpdateThrowsNotFoundExceptionWhenModelDoesNotExist() {
        //given
        Long modelId = 1L;
        Long brandId = 1L;
        CarModelDtos.CarModelUpdateRequest request = new CarModelDtos.CarModelUpdateRequest("Corolla Updated", brandId);

        when(carModelRepository.existsByNameIgnoreCaseAndIdNot("Corolla Updated", modelId)).thenReturn(false);
        when(carModelRepository.findById(modelId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carModelService.update(modelId, request));

        assertEquals("Model not found: 1", exception.getMessage());
        verify(carModelRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Corolla Updated", modelId);
        verify(carModelRepository, times(1)).findById(modelId);
        verify(brandService, never()).getEntity(any());
        verify(carModelRepository, never()).save(any(CarModel.class));
    }

    @Test
    void testUpdateThrowsBadRequestExceptionWhenNameAlreadyExistsForDifferentModel() {
        //given
        Long modelId = 1L;
        Long brandId = 1L;
        CarModelDtos.CarModelUpdateRequest request = new CarModelDtos.CarModelUpdateRequest("Civic", brandId);

        when(carModelRepository.existsByNameIgnoreCaseAndIdNot("Civic", modelId)).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carModelService.update(modelId, request));

        assertEquals("Model already exists: Civic", exception.getMessage());
        verify(carModelRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Civic", modelId);
        verify(carModelRepository, never()).findById(any());
        verify(brandService, never()).getEntity(any());
        verify(carModelRepository, never()).save(any(CarModel.class));
    }

    @Test
    void testUpdateThrowsNotFoundExceptionWhenBrandDoesNotExist() {
        //given
        Long modelId = 1L;
        Long oldBrandId = 1L;
        Long newBrandId = 2L;
        CarModelDtos.CarModelUpdateRequest request = new CarModelDtos.CarModelUpdateRequest("Corolla Updated", newBrandId);

        Brand oldBrand = TestDataFactory.brand("Toyota");
        oldBrand.setId(oldBrandId);
        CarModel existingModel = TestDataFactory.model("Corolla", oldBrand);
        existingModel.setId(modelId);

        when(carModelRepository.existsByNameIgnoreCaseAndIdNot("Corolla Updated", modelId)).thenReturn(false);
        when(carModelRepository.findById(modelId)).thenReturn(Optional.of(existingModel));
        when(brandService.getEntity(newBrandId)).thenThrow(new NotFoundException("Brand not found: 2"));
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carModelService.update(modelId, request));

        assertEquals("Brand not found: 2", exception.getMessage());
        verify(carModelRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Corolla Updated", modelId);
        verify(carModelRepository, times(1)).findById(modelId);
        verify(brandService, times(1)).getEntity(newBrandId);
        verify(carModelRepository, never()).save(any(CarModel.class));
    }

    @Test
    void testDeleteSuccessfullyDeletesCarModel() {
        //given
        Long modelId = 1L;

        when(carModelRepository.existsById(modelId)).thenReturn(true);
        when(carRepository.existsByModelId(modelId)).thenReturn(false);
        //when
        carModelService.delete(modelId);
        //then
        verify(carModelRepository, times(1)).existsById(modelId);
        verify(carRepository, times(1)).existsByModelId(modelId);
        verify(carModelRepository, times(1)).deleteById(modelId);
    }

    @Test
    void testDeleteThrowsNotFoundExceptionWhenModelDoesNotExist() {
        //given
        Long modelId = 1L;
        when(carModelRepository.existsById(modelId)).thenReturn(false);
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carModelService.delete(modelId));

        assertEquals("Model not found: 1", exception.getMessage());
        verify(carModelRepository, times(1)).existsById(modelId);
        verify(carRepository, never()).existsByModelId(any());
        verify(carModelRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteThrowsBadRequestExceptionWhenCarsExist() {
        //given
        Long modelId = 1L;

        when(carModelRepository.existsById(modelId)).thenReturn(true);
        when(carRepository.existsByModelId(modelId)).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> carModelService.delete(modelId));

        assertEquals("Cannot delete, because a car of that model exists!", exception.getMessage());
        verify(carModelRepository, times(1)).existsById(modelId);
        verify(carRepository, times(1)).existsByModelId(modelId);
        verify(carModelRepository, never()).deleteById(any());
    }

    @Test
    void testSearchReturnsMatchingCarModels() {
        //given
        String query = "cor";
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        Brand honda = TestDataFactory.brand("Honda");
        honda.setId(2L);

        CarModel model1 = TestDataFactory.model("Corolla", toyota);
        model1.setId(1L);
        CarModel model2 = TestDataFactory.model("Accord", honda);
        model2.setId(2L);

        when(carModelRepository.findByNameContainingIgnoreCase(query))
                .thenReturn(Arrays.asList(model1, model2));
        //when
        List<CarModelDtos.CarModelDto> result = carModelService.search(query);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Corolla", result.get(0).name());
        assertEquals("Accord", result.get(1).name());
        verify(carModelRepository, times(1)).findByNameContainingIgnoreCase(query);
        verify(carModelRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsEmptyListWhenNoMatches() {
        //given
        String query = "xyz";
        when(carModelRepository.findByNameContainingIgnoreCase(query))
                .thenReturn(Collections.emptyList());
        //when
        List<CarModelDtos.CarModelDto> result = carModelService.search(query);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(carModelRepository, times(1)).findByNameContainingIgnoreCase(query);
        verify(carModelRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsAllModelsWhenQueryIsNull() {
        //given
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        Brand honda = TestDataFactory.brand("Honda");
        honda.setId(2L);

        CarModel model1 = TestDataFactory.model("Corolla", toyota);
        model1.setId(1L);
        CarModel model2 = TestDataFactory.model("Civic", honda);
        model2.setId(2L);

        when(carModelRepository.findAll()).thenReturn(Arrays.asList(model1, model2));
        //when
        List<CarModelDtos.CarModelDto> result = carModelService.search(null);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(carModelRepository, times(1)).findAll();
        verify(carModelRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    void testSearchReturnsAllModelsWhenQueryIsBlank() {
        //given
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        Brand honda = TestDataFactory.brand("Honda");
        honda.setId(2L);

        CarModel model1 = TestDataFactory.model("Corolla", toyota);
        model1.setId(1L);
        CarModel model2 = TestDataFactory.model("Civic", honda);
        model2.setId(2L);

        when(carModelRepository.findAll()).thenReturn(Arrays.asList(model1, model2));
        //when
        List<CarModelDtos.CarModelDto> result = carModelService.search("   ");
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(carModelRepository, times(1)).findAll();
        verify(carModelRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    void testSearchReturnsAllModelsWhenQueryIsEmpty() {
        //given
        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        Brand honda = TestDataFactory.brand("Honda");
        honda.setId(2L);

        CarModel model1 = TestDataFactory.model("Corolla", toyota);
        model1.setId(1L);
        CarModel model2 = TestDataFactory.model("Civic", honda);
        model2.setId(2L);

        when(carModelRepository.findAll()).thenReturn(Arrays.asList(model1, model2));
        //when
        List<CarModelDtos.CarModelDto> result = carModelService.search("");
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(carModelRepository, times(1)).findAll();
        verify(carModelRepository, never()).findByNameContainingIgnoreCase(any());
    }
}