package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.CustomerDtos;
import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerDtos.CustomerDto>> list() {
        return ResponseEntity.ok(customerService.list());
    }

    @PostMapping
    public ResponseEntity<CustomerDtos.CustomerDto> create(@Valid @RequestBody CustomerDtos.CustomerCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDtos.CustomerDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDtos.CustomerDto> update(@PathVariable Long id, @Valid @RequestBody CustomerDtos.CustomerUpdateRequest req) {
        return ResponseEntity.ok(customerService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDtos.CustomerDto>> search(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(customerService.search(query));
    }

    @GetMapping("/{id}/rentals")
    public ResponseEntity<List<RentalDtos.RentalDto>> rentals(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getRentals(id));
    }
}
