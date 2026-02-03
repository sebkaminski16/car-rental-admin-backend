package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.CarModelDtos;
import io.github.sebkaminski16.carrentaladmin.service.CarModelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/models")
public class CarModelController {

    @Autowired
    private CarModelService carModelService;

    @GetMapping
    public ResponseEntity<List<CarModelDtos.CarModelDto>> list() {
        return ResponseEntity.ok(carModelService.list());
    }

    @PostMapping
    public ResponseEntity<CarModelDtos.CarModelDto> create(@Valid @RequestBody CarModelDtos.CarModelCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carModelService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarModelDtos.CarModelDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(carModelService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarModelDtos.CarModelDto> update(@PathVariable Long id, @Valid @RequestBody CarModelDtos.CarModelUpdateRequest req) {
        return ResponseEntity.ok(carModelService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carModelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CarModelDtos.CarModelDto>> search(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(carModelService.search(query));
    }

    @GetMapping("/by-brand/{brandId}")
    public ResponseEntity<List<CarModelDtos.CarModelDto>> byBrand(@PathVariable Long brandId) {
        return ResponseEntity.ok(carModelService.listByBrand(brandId));
    }
}
