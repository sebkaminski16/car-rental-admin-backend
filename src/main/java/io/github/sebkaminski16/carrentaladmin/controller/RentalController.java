package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.entity.RateType;
import io.github.sebkaminski16.carrentaladmin.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @GetMapping
    public ResponseEntity<List<RentalDtos.RentalDto>> list() {
        return ResponseEntity.ok(rentalService.list());
    }

    @PostMapping
    public ResponseEntity<RentalDtos.RentalDto> create(@Valid @RequestBody RentalDtos.RentalCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalDtos.RentalDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RentalDtos.RentalDto> update(@PathVariable Long id, @Valid @RequestBody RentalDtos.RentalUpdateRequest req) {
        return ResponseEntity.ok(rentalService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rentalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<List<RentalDtos.RentalDto>> active() {
        return ResponseEntity.ok(rentalService.listActive());
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<RentalDtos.RentalDto>> overdue() {
        return ResponseEntity.ok(rentalService.listOverdue());
    }

    @GetMapping("/preview")
    public ResponseEntity<RentalDtos.RentalPricePreviewResponse> preview(
            @RequestParam Long carId,
            @RequestParam RateType rateType,
            @RequestParam LocalDateTime startAt,
            @RequestParam LocalDateTime plannedEndAt
    ) {
        return ResponseEntity.ok(rentalService.previewPrice(carId, rateType, startAt, plannedEndAt));
    }

    @PutMapping("/{id}/extend")
    public ResponseEntity<RentalDtos.RentalDto> extend(@PathVariable Long id, @Valid @RequestBody RentalDtos.RentalExtendRequest req) {
        return ResponseEntity.ok(rentalService.extend(id, req));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<RentalDtos.RentalDto> returnRental(@PathVariable Long id, @Valid @RequestBody RentalDtos.RentalReturnRequest req) {
        return ResponseEntity.ok(rentalService.returnRental(id, req));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<RentalDtos.RentalDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.cancel(id));
    }
}
