package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.BrandDtos;
import io.github.sebkaminski16.carrentaladmin.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandDtos.BrandDto>> list() {
        return ResponseEntity.ok(brandService.list());
    }

    @PostMapping
    public ResponseEntity<BrandDtos.BrandDto> create(@Valid @RequestBody BrandDtos.BrandCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brandService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDtos.BrandDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDtos.BrandDto> update(@PathVariable Long id, @Valid @RequestBody BrandDtos.BrandUpdateRequest req) {
        return ResponseEntity.ok(brandService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<BrandDtos.BrandDto>> search(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(brandService.search(query));
    }
}
