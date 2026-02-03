package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.BrandDtos;
import io.github.sebkaminski16.carrentaladmin.entity.Brand;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.mapper.BrandMapper;
import io.github.sebkaminski16.carrentaladmin.repository.BrandRepository;
import io.github.sebkaminski16.carrentaladmin.repository.CarModelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CarModelRepository carModelRepository;

    public List<BrandDtos.BrandDto> list() {
        return brandRepository.findAll().stream().map(BrandMapper::toDto).toList();
    }

    public Brand getEntity(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found: " + id));
    }

    public BrandDtos.BrandDto get(Long id) {
        return BrandMapper.toDto(getEntity(id));
    }

    public BrandDtos.BrandDto create(BrandDtos.BrandCreateRequest req) {

        if(brandRepository.existsByNameIgnoreCase(req.name())) {
            throw new BadRequestException("Brand already exists: " + req.name());
        }

        Brand brand = Brand.builder()
                .name(req.name())
                .build();

        return BrandMapper.toDto(brandRepository.save(brand));
    }

    public BrandDtos.BrandDto update(Long id, BrandDtos.BrandUpdateRequest req) {

        if(brandRepository.existsByNameIgnoreCaseAndIdNot(req.name(), id)) {
            throw new BadRequestException("Brand already exists: " + req.name());
        }

        Brand brand = getEntity(id);
        brand.setName(req.name());
        return BrandMapper.toDto(brandRepository.save(brand));
    }

    public void delete(Long id) {

        if(!brandRepository.existsById(id)) {
            throw new NotFoundException("Brand not found: " + id);
        }

        if(carModelRepository.existsByBrandId(id))
            throw new BadRequestException("Cannot delete, because a model of that brand exists!");

        brandRepository.deleteById(id);
    }

    public List<BrandDtos.BrandDto> search(String query) {

        if (query == null || query.isBlank()) return list();
        return brandRepository.findByNameContainingIgnoreCase(query).stream().map(BrandMapper::toDto).toList();
    }
}
