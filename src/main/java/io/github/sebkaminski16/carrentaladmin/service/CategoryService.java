package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.CategoryDtos;
import io.github.sebkaminski16.carrentaladmin.entity.Category;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.mapper.CategoryMapper;
import io.github.sebkaminski16.carrentaladmin.repository.CarRepository;
import io.github.sebkaminski16.carrentaladmin.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CarRepository carRepository;

    public List<CategoryDtos.CategoryDto> list() {
        return categoryRepository.findAll().stream().map(CategoryMapper::toDto).toList();
    }

    public Category getEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }

    public CategoryDtos.CategoryDto get(Long id) {
        return CategoryMapper.toDto(getEntity(id));
    }

    public CategoryDtos.CategoryDto create(CategoryDtos.CategoryCreateRequest req) {

        if(categoryRepository.existsByNameIgnoreCase(req.name())) {
            throw new BadRequestException("Category exists: " + req.name());
        }

        Category category = Category.builder()
                .name(req.name())
                .description(req.description())
                .dailyDiscountPercent(req.dailyDiscountPercent())
                .weeklyDiscountPercent(req.weeklyDiscountPercent())
                .build();

        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    public CategoryDtos.CategoryDto update(Long id, CategoryDtos.CategoryUpdateRequest req) {

        if(categoryRepository.existsByNameIgnoreCaseAndIdNot(req.name(), id)) {
            throw new BadRequestException("Category exists: " + req.name());
        }

        Category category = getEntity(id);

        category.setName(req.name());
        category.setDescription(req.description());
        category.setDailyDiscountPercent(req.dailyDiscountPercent());
        category.setWeeklyDiscountPercent(req.weeklyDiscountPercent());

        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    public void delete(Long id) {

        if(!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category not found: " + id);
        }

        if(carRepository.existsByCategoryId(id)) {
            throw new BadRequestException("Cannot delete, because a Car of that category exists!");
        }

        categoryRepository.deleteById(id);
    }

    public List<CategoryDtos.CategoryDto> search(String query) {
        if (query == null || query.isBlank()) return list();
        return categoryRepository.findByNameContainingIgnoreCase(query).stream().map(CategoryMapper::toDto).toList();
    }
}
