package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.CarModelDtos;
import io.github.sebkaminski16.carrentaladmin.entity.Brand;
import io.github.sebkaminski16.carrentaladmin.entity.CarModel;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.mapper.CarModelMapper;
import io.github.sebkaminski16.carrentaladmin.repository.CarModelRepository;
import io.github.sebkaminski16.carrentaladmin.repository.CarRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class CarModelService {

    //Initially I wanted to put BrandSservice in the CarModelController, but I found this discussion:
    //https://stackoverflow.com/questions/30262918/can-i-have-multiple-services-in-a-controller-class-spring-mvc
    //That's why the BrandService is here now
    //"...let your EmployeeService create the account using the AccountService,
    // that way everything will also execute in a single transaction."
    @Autowired
    private BrandService brandService;

    @Autowired
    private CarModelRepository carModelRepository;

    @Autowired
    private CarRepository carRepository;

    public List<CarModelDtos.CarModelDto> list() {
        return carModelRepository.findAll().stream().map(CarModelMapper::toDto).toList();
    }

    public CarModel getEntity(Long id) {
        return carModelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Model not found: " + id));
    }

    public CarModelDtos.CarModelDto get(Long id) {
        return CarModelMapper.toDto(getEntity(id));
    }

    public List<CarModelDtos.CarModelDto> listByBrand(Long brandId) {
        return carModelRepository.findByBrandId(brandId).stream().map(CarModelMapper::toDto).toList();
    }

    public CarModelDtos.CarModelDto create(CarModelDtos.CarModelCreateRequest req) {

        if(carModelRepository.existsByNameIgnoreCase(req.name())) {
            throw new BadRequestException("Model already exists: " + req.name());
        }

        Brand brand = brandService.getEntity(req.brandId());

        CarModel model = CarModel.builder()
                .name(req.name())
                .brand(brand)
                .build();

        return CarModelMapper.toDto(carModelRepository.save(model));
    }

    public CarModelDtos.CarModelDto update(Long id, CarModelDtos.CarModelUpdateRequest req) {

        if(carModelRepository.existsByNameIgnoreCaseAndIdNot(req.name(), id)) {
            throw new BadRequestException("Model already exists: " + req.name());
        }

        CarModel model = getEntity(id);
        Brand brand = brandService.getEntity(req.brandId());

        model.setName(req.name());
        model.setBrand(brand);

        return CarModelMapper.toDto(carModelRepository.save(model));
    }

    public void delete(Long id) {

        if(!carModelRepository.existsById(id)) {
            throw new NotFoundException("Model not found: " + id);
        }

        if(carRepository.existsByModelId(id)) {
            throw new BadRequestException("Cannot delete, because a car of that model exists!");
        }

        carModelRepository.deleteById(id);
    }

    public List<CarModelDtos.CarModelDto> search(String query) {
        if (query == null || query.isBlank()) return list();
        return carModelRepository.findByNameContainingIgnoreCase(query).stream().map(CarModelMapper::toDto).toList();
    }
}
