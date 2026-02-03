package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.dto.CustomerDtos;
import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.entity.*;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.repository.CustomerRepository;
import io.github.sebkaminski16.carrentaladmin.repository.RentalRepository;
import io.github.sebkaminski16.carrentaladmin.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTests {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RentalRepository rentalRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void testListReturnsAllCustomers() {
        //given
        Customer customer1 = TestDataFactory.customer("john@example.com");
        customer1.setId(1L);
        Customer customer2 = TestDataFactory.customer("jane@example.com");
        customer2.setId(2L);

        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));
        //when
        List<CustomerDtos.CustomerDto> result = customerService.list();
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("john@example.com", result.getFirst().email());
        assertEquals("jane@example.com", result.get(1).email());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testListReturnsEmptyListWhenNoCustomers() {
        //given
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<CustomerDtos.CustomerDto> result = customerService.list();
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetReturnsCustomerDtoWhenExists() {
        //given
        Long customerId = 1L;
        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        //when
        CustomerDtos.CustomerDto result = customerService.get(customerId);
        //then
        assertNotNull(result);
        assertEquals(customerId, result.id());
        assertEquals("john@example.com", result.email());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void testGetThrowsNotFoundExceptionWhenCustomerDoesNotExist() {
        //given
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> customerService.get(customerId));

        assertEquals("Customer not found: 1", exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void testGetEntityReturnsCustomerWhenExists() {
        //given
        Long customerId = 1L;
        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        //when
        Customer result = customerService.getEntity(customerId);
        //then
        assertNotNull(result);
        assertEquals(customerId, result.getId());
        assertEquals("john@example.com", result.getEmail());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void testGetEntityThrowsNotFoundExceptionWhenCustomerDoesNotExist() {
        //given
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> customerService.getEntity(customerId));

        assertEquals("Customer not found: 1", exception.getMessage());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void testCreateSuccessfullyCreatesNewCustomer() {
        //given
        CustomerDtos.CustomerCreateRequest request = new CustomerDtos.CustomerCreateRequest(
                "John",
                "Doe",
                "john@example.com",
                "123456789",
                "123 Main St"
        );

        Customer savedCustomer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("123456789")
                .address("123 Main St")
                .build();
        savedCustomer.setId(1L);

        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(customerRepository.existsByPhone("123456789")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        //when
        CustomerDtos.CustomerDto result = customerService.create(request);
        //then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals("john@example.com", result.email());
        assertEquals("123456789", result.phone());
        assertEquals("123 Main St", result.address());
        verify(customerRepository, times(1)).existsByEmail("john@example.com");
        verify(customerRepository, times(1)).existsByPhone("123456789");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenEmailAlreadyExists() {
        //given
        CustomerDtos.CustomerCreateRequest request = new CustomerDtos.CustomerCreateRequest(
                "John",
                "Doe",
                "john@example.com",
                "123456789",
                "123 Main St"
        );

        when(customerRepository.existsByEmail("john@example.com")).thenReturn(true);
        //when & then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> customerService.create(request));

        assertEquals("Customer with that email already exists: john@example.com", exception.getMessage());
        verify(customerRepository, times(1)).existsByEmail("john@example.com");
        verify(customerRepository, never()).existsByPhone(any());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCreateThrowsBadRequestExceptionWhenPhoneAlreadyExists() {
        //given
        CustomerDtos.CustomerCreateRequest request = new CustomerDtos.CustomerCreateRequest(
                "John",
                "Doe",
                "john@example.com",
                "123456789",
                "123 Main St"
        );

        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(customerRepository.existsByPhone("123456789")).thenReturn(true);
        //when & then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> customerService.create(request));

        assertEquals("Customer with that phone already exists: 123456789", exception.getMessage());
        verify(customerRepository, times(1)).existsByEmail("john@example.com");
        verify(customerRepository, times(1)).existsByPhone("123456789");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdateSuccessfullyUpdatesCustomer() {
        //given
        Long customerId = 1L;
        CustomerDtos.CustomerUpdateRequest request = new CustomerDtos.CustomerUpdateRequest(
                "John Updated",
                "Doe Updated",
                "john.updated@example.com",
                "987654321",
                "456 New St"
        );

        Customer existingCustomer = TestDataFactory.customer("john@example.com");
        existingCustomer.setId(customerId);

        Customer updatedCustomer = Customer.builder()
                .firstName("John Updated")
                .lastName("Doe Updated")
                .email("john.updated@example.com")
                .phone("987654321")
                .address("456 New St")
                .build();
        updatedCustomer.setId(customerId);

        when(customerRepository.existsByEmailAndIdNot("john.updated@example.com", customerId)).thenReturn(false);
        when(customerRepository.existsByPhoneAndIdNot("987654321", customerId)).thenReturn(false);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);
        //when
        CustomerDtos.CustomerDto result = customerService.update(customerId, request);
        //then
        assertNotNull(result);
        assertEquals(customerId, result.id());
        assertEquals("John Updated", result.firstName());
        assertEquals("Doe Updated", result.lastName());
        assertEquals("john.updated@example.com", result.email());
        assertEquals("987654321", result.phone());
        assertEquals("456 New St", result.address());
        verify(customerRepository, times(1)).existsByEmailAndIdNot("john.updated@example.com", customerId);
        verify(customerRepository, times(1)).existsByPhoneAndIdNot("987654321", customerId);
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdateThrowsNotFoundExceptionWhenCustomerDoesNotExist() {
        //given
        Long customerId = 1L;
        CustomerDtos.CustomerUpdateRequest request = new CustomerDtos.CustomerUpdateRequest(
                "John Updated",
                "Doe Updated",
                "john.updated@example.com",
                "987654321",
                "456 New St"
        );

        when(customerRepository.existsByEmailAndIdNot("john.updated@example.com", customerId)).thenReturn(false);
        when(customerRepository.existsByPhoneAndIdNot("987654321", customerId)).thenReturn(false);
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        //when & then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> customerService.update(customerId, request));

        assertEquals("Customer not found: 1", exception.getMessage());
        verify(customerRepository, times(1)).existsByEmailAndIdNot("john.updated@example.com", customerId);
        verify(customerRepository, times(1)).existsByPhoneAndIdNot("987654321", customerId);
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdateThrowsBadRequestExceptionWhenEmailAlreadyExistsForDifferentCustomer() {
        //given
        Long customerId = 1L;
        CustomerDtos.CustomerUpdateRequest request = new CustomerDtos.CustomerUpdateRequest(
                "John Updated",
                "Doe Updated",
                "existing@example.com",
                "987654321",
                "456 New St"
        );

        when(customerRepository.existsByEmailAndIdNot("existing@example.com", customerId)).thenReturn(true);
        //when & then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> customerService.update(customerId, request));

        assertEquals("Customer with that email already exists: existing@example.com", exception.getMessage());
        verify(customerRepository, times(1)).existsByEmailAndIdNot("existing@example.com", customerId);
        verify(customerRepository, never()).existsByPhoneAndIdNot(any(), any());
        verify(customerRepository, never()).findById(any());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdateThrowsBadRequestExceptionWhenPhoneAlreadyExistsForDifferentCustomer() {
        //given
        Long customerId = 1L;
        CustomerDtos.CustomerUpdateRequest request = new CustomerDtos.CustomerUpdateRequest(
                "John Updated",
                "Doe Updated",
                "john.updated@example.com",
                "111222333",
                "456 New St"
        );

        when(customerRepository.existsByEmailAndIdNot("john.updated@example.com", customerId)).thenReturn(false);
        when(customerRepository.existsByPhoneAndIdNot("111222333", customerId)).thenReturn(true);
        //when & then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> customerService.update(customerId, request));

        assertEquals("Customer with that phone already exists: 111222333", exception.getMessage());
        verify(customerRepository, times(1)).existsByEmailAndIdNot("john.updated@example.com", customerId);
        verify(customerRepository, times(1)).existsByPhoneAndIdNot("111222333", customerId);
        verify(customerRepository, never()).findById(any());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testDeleteSuccessfullyDeletesCustomer() {
        //given
        Long customerId = 1L;

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(rentalRepository.existsByCustomerId(customerId)).thenReturn(false);
        //when
        customerService.delete(customerId);
        //then
        verify(customerRepository, times(1)).existsById(customerId);
        verify(rentalRepository, times(1)).existsByCustomerId(customerId);
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    void testDeleteThrowsNotFoundExceptionWhenCustomerDoesNotExist() {
        //given
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(false);
        //when & then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> customerService.delete(customerId));

        assertEquals("Customer not found: 1", exception.getMessage());
        verify(customerRepository, times(1)).existsById(customerId);
        verify(rentalRepository, never()).existsByCustomerId(any());
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void testDeleteThrowsBadRequestExceptionWhenRentalsExist() {
        //given
        Long customerId = 1L;

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(rentalRepository.existsByCustomerId(customerId)).thenReturn(true);
        //when & then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> customerService.delete(customerId));

        assertEquals("Cannot delete, because a Rental with that customer exists!", exception.getMessage());
        verify(customerRepository, times(1)).existsById(customerId);
        verify(rentalRepository, times(1)).existsByCustomerId(customerId);
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void testSearchReturnsMatchingCustomersByFirstName() {
        //given
        String query = "joh";
        Customer customer1 = TestDataFactory.customer("john@example.com");
        customer1.setId(1L);
        customer1.setFirstName("John");

        when(customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, query)).thenReturn(Collections.singletonList(customer1));
        //when
        List<CustomerDtos.CustomerDto> result = customerService.search(query);
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().firstName());
        verify(customerRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, query);
        verify(customerRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsMatchingCustomersByLastName() {
        //given
        String query = "do";
        Customer customer1 = TestDataFactory.customer("john@example.com");
        customer1.setId(1L);

        when(customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, query)).thenReturn(Collections.singletonList(customer1));
        //when
        List<CustomerDtos.CustomerDto> result = customerService.search(query);
        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Doe", result.getFirst().lastName());
        verify(customerRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, query);
        verify(customerRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsMatchingCustomersByEmail() {
        //given
        String query = "example";
        Customer customer1 = TestDataFactory.customer("john@example.com");
        customer1.setId(1L);
        Customer customer2 = TestDataFactory.customer("jane@example.com");
        customer2.setId(2L);

        when(customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, query)).thenReturn(Arrays.asList(customer1, customer2));
        //when
        List<CustomerDtos.CustomerDto> result = customerService.search(query);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("john@example.com", result.getFirst().email());
        assertEquals("jane@example.com", result.get(1).email());
        verify(customerRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, query);
        verify(customerRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsEmptyListWhenNoMatches() {
        //given
        String query = "xyz";
        when(customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, query)).thenReturn(Collections.emptyList());
        //when
        List<CustomerDtos.CustomerDto> result = customerService.search(query);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1))
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, query);
        verify(customerRepository, never()).findAll();
    }

    @Test
    void testSearchReturnsAllCustomersWhenQueryIsNull() {
        //given
        Customer customer1 = TestDataFactory.customer("john@example.com");
        customer1.setId(1L);
        Customer customer2 = TestDataFactory.customer("jane@example.com");
        customer2.setId(2L);

        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));
        //when
        List<CustomerDtos.CustomerDto> result = customerService.search(null);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findAll();
        verify(customerRepository, never())
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(any(), any(), any());
    }

    @Test
    void testSearchReturnsAllCustomersWhenQueryIsBlank() {
        //given
        Customer customer1 = TestDataFactory.customer("john@example.com");
        customer1.setId(1L);
        Customer customer2 = TestDataFactory.customer("jane@example.com");
        customer2.setId(2L);

        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));
        //when
        List<CustomerDtos.CustomerDto> result = customerService.search("   ");
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findAll();
        verify(customerRepository, never())
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(any(), any(), any());
    }

    @Test
    void testSearchReturnsAllCustomersWhenQueryIsEmpty() {
        //given
        Customer customer1 = TestDataFactory.customer("john@example.com");
        customer1.setId(1L);
        Customer customer2 = TestDataFactory.customer("jane@example.com");
        customer2.setId(2L);

        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));
        //when
        List<CustomerDtos.CustomerDto> result = customerService.search("");
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findAll();
        verify(customerRepository, never())
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(any(), any(), any());
    }

    @Test
    void testGetRentalsReturnsRentalsForCustomer() {
        //given
        Long customerId = 1L;

        Customer customer = TestDataFactory.customer("john@example.com");
        customer.setId(customerId);

        Brand toyota = TestDataFactory.brand("Toyota");
        toyota.setId(1L);
        CarModel corolla = TestDataFactory.model("Corolla", toyota);
        corolla.setId(1L);
        Category economy = TestDataFactory.category("Economy", BigDecimal.valueOf(5.0), BigDecimal.valueOf(15.0));
        economy.setId(1L);
        Car car = TestDataFactory.car("VIN123", "ABC123", corolla, economy,
                BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(300.0));
        car.setId(1L);

        Rental rental1 = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 5, 10, 0), RateType.DAILY, RentalStatus.RETURNED);
        rental1.setId(1L);

        Rental rental2 = TestDataFactory.rental(customer, car, LocalDateTime.of(2026, 2, 1, 10, 0),
                LocalDateTime.of(2026, 2, 5, 10, 0), RateType.DAILY, RentalStatus.ACTIVE);
        rental2.setId(2L);

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(rentalRepository.findByCustomerIdOrderByStartAtDesc(customerId))
                .thenReturn(Arrays.asList(rental2, rental1));
        //when
        List<RentalDtos.RentalDto> result = customerService.getRentals(customerId);
        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.getFirst().id());
        assertEquals(RentalStatus.ACTIVE, result.getFirst().status());
        assertEquals(1L, result.get(1).id());
        assertEquals(RentalStatus.RETURNED, result.get(1).status());
        verify(customerRepository, times(1)).existsById(customerId);
        verify(rentalRepository, times(1)).findByCustomerIdOrderByStartAtDesc(customerId);
    }

    @Test
    void testGetRentalsReturnsEmptyListWhenNoRentals() {
        //given
        Long customerId = 1L;

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(rentalRepository.findByCustomerIdOrderByStartAtDesc(customerId))
                .thenReturn(Collections.emptyList());
        //when
        List<RentalDtos.RentalDto> result = customerService.getRentals(customerId);
        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).existsById(customerId);
        verify(rentalRepository, times(1)).findByCustomerIdOrderByStartAtDesc(customerId);
    }

    @Test
    void testGetRentalsThrowsNotFoundExceptionWhenCustomerDoesNotExist() {
        //given
        Long customerId = 1L;
        when(customerRepository.existsById(customerId)).thenReturn(false);
        //when&then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> customerService.getRentals(customerId));

        assertEquals("Customer not found: 1", exception.getMessage());
        verify(customerRepository, times(1)).existsById(customerId);
        verify(rentalRepository, never()).findByCustomerIdOrderByStartAtDesc(any());
    }
}