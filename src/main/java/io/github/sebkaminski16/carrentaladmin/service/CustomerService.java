package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.CustomerDtos;
import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.entity.Customer;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.mapper.CustomerMapper;
import io.github.sebkaminski16.carrentaladmin.mapper.RentalMapper;
import io.github.sebkaminski16.carrentaladmin.repository.CustomerRepository;
import io.github.sebkaminski16.carrentaladmin.repository.RentalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    public List<CustomerDtos.CustomerDto> list() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toDto)
                .toList();
    }

    public CustomerDtos.CustomerDto get(Long id) {
        return CustomerMapper.toDto(getEntity(id));
    }

    public Customer getEntity(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }

    public CustomerDtos.CustomerDto create(CustomerDtos.CustomerCreateRequest req) {

        if(customerRepository.existsByEmail(req.email())) {
            throw new BadRequestException("Customer with that email already exists: " + req.email());
        }

        if(customerRepository.existsByPhone(req.phone())) {
            throw new BadRequestException("Customer with that phone already exists: " + req.phone());
        }

        Customer customer = Customer.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
                .phone(req.phone())
                .address(req.address())
                .build();

        return CustomerMapper.toDto(customerRepository.save(customer));
    }

    public CustomerDtos.CustomerDto update(Long id, CustomerDtos.CustomerUpdateRequest req) {

        if(customerRepository.existsByEmailAndIdNot(req.email(), id)) {
            throw new BadRequestException("Customer with that email already exists: " + req.email());
        }

        if(customerRepository.existsByPhoneAndIdNot(req.phone(), id)) {
            throw new BadRequestException("Customer with that phone already exists: " + req.phone());
        }
        Customer customer = getEntity(id);

        customer.setFirstName(req.firstName());
        customer.setLastName(req.lastName());
        customer.setEmail(req.email());
        customer.setPhone(req.phone());
        customer.setAddress(req.address());

        return CustomerMapper.toDto(customerRepository.save(customer));
    }

    public void delete(Long id) {

        if(!customerRepository.existsById(id)) {
            throw new NotFoundException("Customer not found: " + id);
        }

        if(rentalRepository.existsByCustomerId(id)) throw new BadRequestException("Cannot delete, because a Rental with that customer exists!");

        customerRepository.deleteById(id);
    }

    public List<CustomerDtos.CustomerDto> search(String query) {
        if (query == null || query.isBlank()) {
            return list();
        }
        return customerRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, query)
                .stream()
                .map(CustomerMapper::toDto)
                .toList();
    }

    public List<RentalDtos.RentalDto> getRentals(Long customerId) {

        if(!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Customer not found: " + customerId);
        }

        return rentalRepository.findByCustomerIdOrderByStartAtDesc(customerId)
                .stream()
                .map(RentalMapper::toDto)
                .toList();
    }
}
