package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.CategoryDtos;
import io.github.sebkaminski16.carrentaladmin.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDtos.CategoryDto>> list() {
        return ResponseEntity.ok(categoryService.list());
    }

    @PostMapping
    public ResponseEntity<CategoryDtos.CategoryDto> create(@Valid @RequestBody CategoryDtos.CategoryCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDtos.CategoryDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDtos.CategoryDto> update(@PathVariable Long id, @Valid @RequestBody CategoryDtos.CategoryUpdateRequest req) {
        return ResponseEntity.ok(categoryService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CategoryDtos.CategoryDto>> search(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(categoryService.search(query));
    }
}
