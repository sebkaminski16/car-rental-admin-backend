package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.CarDtos;
import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.entity.CarStatus;
import io.github.sebkaminski16.carrentaladmin.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping
    public ResponseEntity<List<CarDtos.CarDto>> list() {
        return ResponseEntity.ok(carService.list());
    }

    @PostMapping
    public ResponseEntity<CarDtos.CarDto> create(@Valid @RequestBody CarDtos.CarCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarDtos.CarDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(carService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarDtos.CarDto> update(@PathVariable Long id, @Valid @RequestBody CarDtos.CarUpdateRequest req) {
        return ResponseEntity.ok(carService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CarDtos.CarDto>> search(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(carService.search(query));
    }

    @GetMapping("/available")
    public ResponseEntity<List<CarDtos.CarDto>> available(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to
    ) {
        return ResponseEntity.ok(carService.availableBetween(from, to));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CarDtos.CarDto>> byStatus(@PathVariable CarStatus status) {
        return ResponseEntity.ok(carService.listByStatus(status));
    }

    @GetMapping("/{id}/rentals")
    public ResponseEntity<List<RentalDtos.RentalDto>> rentals(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getRentals(id));
    }
}
