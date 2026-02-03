package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.BrandDtos;
import io.github.sebkaminski16.carrentaladmin.entity.Brand;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.repository.BrandRepository;
import io.github.sebkaminski16.carrentaladmin.repository.CarModelRepository;
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
class BrandServiceTests {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CarModelRepository carModelRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    void testListReturnsAllBrands() {
        //given
        Brand brand1 = TestDataFactory.brand("Toyota");
        brand1.setId(1L);
        Brand brand2 = TestDataFactory.brand("Honda");
        brand2.setId(2L);

        when(brandRepository.findAll()).thenReturn(Arrays.asList(brand1, brand2));
        //when
        List<BrandDtos.BrandDto> result = brandService.list();
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Toyota", result.get(0).name());
        assertEquals("Honda", result.get(1).name());
        verify(brandRepository, times(1)).findAll();
    }

    @Test
    void testListReturnsEmptyListWhenNoBrands() {
        //given
        when(brandRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<BrandDtos.BrandDto> result = brandService.list();
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(brandRepository, times(1)).findAll();
    }

    @Test
    void testGetEntityReturnsBrandWhenExists() {
        //given
        Long brandId = 1L;
        Brand brand = TestDataFactory.brand("Toyota");
        brand.setId(brandId);

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        //when
        Brand result = brandService.getEntity(brandId);
        //then
        assertNotNull(result);
        assertEquals(brandId, result.getId());
        assertEquals("Toyota", result.getName());
        verify(brandRepository, times(1)).findById(brandId);
    }

    @Test
    void testGetEntityThrowsNotFoundExceptionWhenBrandDoesNotExist() {
        //given
        Long brandId = 1L;
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());
        //when &then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> brandService.getEntity(brandId));

        assertEquals("Brand not found: 1", exception.getMessage());
        verify(brandRepository, times(1)).findById(brandId);
    }

    @Test
    void testGetReturnsBrandDtoWhenExists() {
        //given
        Long brandId = 1L;
        Brand brand = TestDataFactory.brand("Toyota");
        brand.setId(brandId);

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        //when
        BrandDtos.BrandDto result = brandService.get(brandId);
        //then
        assertNotNull(result);
        assertEquals(brandId, result.id());
        assertEquals("Toyota", result.name());
        verify(brandRepository, times(1)).findById(brandId);
    }

    @Test
    void testGetThrowsNotFoundExceptionWhenBrandDoesNotExist() {
        //given
        Long brandId = 1L;
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());
        //when& then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> brandService.get(brandId));

        assertEquals("Brand not found: 1", exception.getMessage());
        verify(brandRepository, times(1)).findById(brandId);
    }

    @Test
    void testCreateSuccessfullyCreatesNewBrand() {
        //given
        BrandDtos.BrandCreateRequest request = new BrandDtos.BrandCreateRequest("Toyota");
        Brand savedBrand = TestDataFactory.brand("Toyota");
        savedBrand.setId(1L);

        when(brandRepository.existsByNameIgnoreCase("Toyota")).thenReturn(false);
        when(brandRepository.save(any(Brand.class))).thenReturn(savedBrand);
        //when
        BrandDtos.BrandDto result = brandService.create(request);
        //then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Toyota", result.name());
        verify(brandRepository, times(1)).existsByNameIgnoreCase("Toyota");
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenBrandAlreadyExists() {
        //given
        BrandDtos.BrandCreateRequest request = new BrandDtos.BrandCreateRequest("Toyota");
        when(brandRepository.existsByNameIgnoreCase("Toyota")).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> brandService.create(request));

        assertEquals("Brand already exists: Toyota", exception.getMessage());
        verify(brandRepository, times(1)).existsByNameIgnoreCase("Toyota");
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenBrandExistsCaseInsensitive() {
        //given
        BrandDtos.BrandCreateRequest request = new BrandDtos.BrandCreateRequest("TOYOTA");
        when(brandRepository.existsByNameIgnoreCase("TOYOTA")).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> brandService.create(request));

        assertEquals("Brand already exists: TOYOTA", exception.getMessage());
        verify(brandRepository, times(1)).existsByNameIgnoreCase("TOYOTA");
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void testUpdateSuccessfullyUpdatesBrand() {
        //given
        Long brandId = 1L;
        BrandDtos.BrandUpdateRequest request = new BrandDtos.BrandUpdateRequest("Toyota Updated");
        Brand existingBrand = TestDataFactory.brand("Toyota");
        existingBrand.setId(brandId);
        Brand updatedBrand = TestDataFactory.brand("Toyota Updated");
        updatedBrand.setId(brandId);

        when(brandRepository.existsByNameIgnoreCaseAndIdNot("Toyota Updated", brandId)).thenReturn(false);
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(existingBrand));
        when(brandRepository.save(any(Brand.class))).thenReturn(updatedBrand);
        //when
        BrandDtos.BrandDto result = brandService.update(brandId, request);
        //then
        assertNotNull(result);
        assertEquals(brandId, result.id());
        assertEquals("Toyota Updated", result.name());
        verify(brandRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Toyota Updated", brandId);
        verify(brandRepository, times(1)).findById(brandId);
        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void testUpdateThrowsNotFoundExceptionWhenBrandDoesNotExist() {
        //given
        Long brandId = 1L;
        BrandDtos.BrandUpdateRequest request = new BrandDtos.BrandUpdateRequest("Toyota Updated");

        when(brandRepository.existsByNameIgnoreCaseAndIdNot("Toyota Updated", brandId)).thenReturn(false);
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());
        //whe&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> brandService.update(brandId, request));

        assertEquals("Brand not found: 1", exception.getMessage());
        verify(brandRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Toyota Updated", brandId);
        verify(brandRepository, times(1)).findById(brandId);
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void testUpdateThrowsBadRequestExceptionWhenNameAlreadyExistsForDifferentBrand() {
        //given
        Long brandId = 1L;
        BrandDtos.BrandUpdateRequest request = new BrandDtos.BrandUpdateRequest("Honda");

        when(brandRepository.existsByNameIgnoreCaseAndIdNot("Honda", brandId)).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> brandService.update(brandId, request));

        assertEquals("Brand already exists: Honda", exception.getMessage());
        verify(brandRepository, times(1)).existsByNameIgnoreCaseAndIdNot("Honda", brandId);
        verify(brandRepository, never()).findById(any());
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void testDeleteSuccessfullyDeletesBrand() {
        //given
        Long brandId = 1L;

        when(brandRepository.existsById(brandId)).thenReturn(true);
        when(carModelRepository.existsByBrandId(brandId)).thenReturn(false);
        //when
        brandService.delete(brandId);
        //then
        verify(brandRepository, times(1)).existsById(brandId);
        verify(carModelRepository, times(1)).existsByBrandId(brandId);
        verify(brandRepository, times(1)).deleteById(brandId);
    }

    @Test
    void testDeleteThrowsNotFoundExceptionWhenBrandDoesNotExist() {
        //given
        Long brandId = 1L;
        when(brandRepository.existsById(brandId)).thenReturn(false);
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> brandService.delete(brandId));

        assertEquals("Brand not found: 1", exception.getMessage());
        verify(brandRepository, times(1)).existsById(brandId);
        verify(carModelRepository, never()).existsByBrandId(any());
        verify(brandRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteThrowsBadRequestExceptionWhenCarModelsExist() {
        //given
        Long brandId = 1L;

        when(brandRepository.existsById(brandId)).thenReturn(true);
        when(carModelRepository.existsByBrandId(brandId)).thenReturn(true);
        //when&then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> brandService.delete(brandId));

        assertEquals("Cannot delete, because a model of that brand exists!", exception.getMessage());
        verify(brandRepository, times(1)).existsById(brandId);
        verify(carModelRepository, times(1)).existsByBrandId(brandId);
        verify(brandRepository, never()).deleteById(any());
    }

    @Test
    void testSearchReturnsMatchingBrands() {
        //given
        String query = "toy";
        Brand brand1 = TestDataFactory.brand("Toyota");
        brand1.setId(1L);
        Brand brand2 = TestDataFactory.brand("NotToyota");
        brand2.setId(2L);

        when(brandRepository.findByNameContainingIgnoreCase(query))
                .thenReturn(Arrays.asList(brand1, brand2));
        //when
        List<BrandDtos.BrandDto> result = brandService.search(query);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Toyota", result.get(0).name());
        assertEquals("NotToyota", result.get(1).name());
        verify(brandRepository, times(1)).findByNameContainingIgnoreCase(query);
        verify(brandRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsEmptyListWhenNoMatches() {
        //given
        String query = "xyz";
        when(brandRepository.findByNameContainingIgnoreCase(query))
                .thenReturn(Collections.emptyList());
        //when
        List<BrandDtos.BrandDto> result = brandService.search(query);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(brandRepository, times(1)).findByNameContainingIgnoreCase(query);
        verify(brandRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsAllBrandsWhenQueryIsNull() {
        //given
        Brand brand1 = TestDataFactory.brand("Toyota");
        brand1.setId(1L);
        Brand brand2 = TestDataFactory.brand("Honda");
        brand2.setId(2L);

        when(brandRepository.findAll()).thenReturn(Arrays.asList(brand1, brand2));
        //when
        List<BrandDtos.BrandDto> result = brandService.search(null);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(brandRepository, times(1)).findAll();
        verify(brandRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    void testSearchReturnsAllBrandsWhenQueryIsBlank() {
        // given
        Brand brand1 = TestDataFactory.brand("Toyota");
        brand1.setId(1L);
        Brand brand2 = TestDataFactory.brand("Honda");
        brand2.setId(2L);
        when(brandRepository.findAll()).thenReturn(Arrays.asList(brand1, brand2));
        //when
        List<BrandDtos.BrandDto> result = brandService.search("   ");
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(brandRepository, times(1)).findAll();
        verify(brandRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    void testSearchReturnsAllBrandsWhenQueryIsEmpty() {
        //given
        Brand brand1 = TestDataFactory.brand("Toyota");
        brand1.setId(1L);
        Brand brand2 = TestDataFactory.brand("Honda");
        brand2.setId(2L);
        when(brandRepository.findAll()).thenReturn(Arrays.asList(brand1, brand2));
        //when
        List<BrandDtos.BrandDto> result = brandService.search("");
        //hen
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(brandRepository, times(1)).findAll();
        verify(brandRepository, never()).findByNameContainingIgnoreCase(any());
    }
}