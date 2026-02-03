package io.github.sebkaminski16.carrentaladmin.controller;

import io.github.sebkaminski16.carrentaladmin.dto.CustomerDtos;
import io.github.sebkaminski16.carrentaladmin.dto.RentalDtos;
import io.github.sebkaminski16.carrentaladmin.entity.RateType;
import io.github.sebkaminski16.carrentaladmin.entity.RentalStatus;
import io.github.sebkaminski16.carrentaladmin.exception.BadRequestException;
import io.github.sebkaminski16.carrentaladmin.exception.NotFoundException;
import io.github.sebkaminski16.carrentaladmin.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    public void testListReturnsAllCustomers() throws Exception {
        //given
        CustomerDtos.CustomerDto customer1 = new CustomerDtos.CustomerDto(
                1L, "John", "Doe", "john@example.com", "123456789", "Test 1",
                LocalDateTime.now(), LocalDateTime.now()
        );
        CustomerDtos.CustomerDto customer2 = new CustomerDtos.CustomerDto(
                2L, "Jane", "Nowak", "jane@example.com", "987654321", "Test 2",
                LocalDateTime.now(), LocalDateTime.now()
        );
        List<CustomerDtos.CustomerDto> customers = Arrays.asList(customer1, customer2);
        when(customerService.list()).thenReturn(customers);
        //when&then
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Doe")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].firstName", is("Jane")));

        verify(customerService, times(1)).list();
    }

    @Test
    public void testListReturnsEmptyListWhenNoCustomers() throws Exception {
        //given
        when(customerService.list()).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(customerService, times(1)).list();
    }

    @Test
    public void testCreateCustomerSuccessfully() throws Exception {
        //given
        CustomerDtos.CustomerDto createdCustomer = new CustomerDtos.CustomerDto(
                1L, "John", "Doe", "john@example.com", "123456789", "Test 1",
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(customerService.create(any(CustomerDtos.CustomerCreateRequest.class))).thenReturn(createdCustomer);

        String requestBody = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"phone\":\"123456789\",\"address\":\"Test 1\"}";
        //when&then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.phone", is("123456789")));

        verify(customerService, times(1)).create(any(CustomerDtos.CustomerCreateRequest.class));
    }

    @Test
    public void testCreateCustomerWithBlankFirstNameReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"firstName\":\"\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"phone\":\"123456789\",\"address\":\"Test 1\"}";
        //when&then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).create(any(CustomerDtos.CustomerCreateRequest.class));
    }

    @Test
    public void testCreateCustomerWithBlankLastNameReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"firstName\":\"John\",\"lastName\":\"\",\"email\":\"john@example.com\",\"phone\":\"123456789\",\"address\":\"Test 1\"}";
        //when&then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).create(any(CustomerDtos.CustomerCreateRequest.class));
    }

    @Test
    public void testCreateCustomerWithInvalidEmailReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"invalid-email\",\"phone\":\"123456789\",\"address\":\"Test 1\"}";
        //when&then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).create(any(CustomerDtos.CustomerCreateRequest.class));
    }

    @Test
    public void testCreateCustomerWithBlankPhoneReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"phone\":\"\",\"address\":\"Test 1\"}";
        //when&then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).create(any(CustomerDtos.CustomerCreateRequest.class));
    }

    @Test
    public void testCreateCustomerWithTooLongFirstNameReturnsValidationError() throws Exception {
        //given
        String longName = "A".repeat(81);
        String requestBody = "{\"firstName\":\"" + longName + "\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"phone\":\"123456789\",\"address\":\"Test 1\"}";
        //when&then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).create(any(CustomerDtos.CustomerCreateRequest.class));
    }

    @Test
    public void testCreateCustomerWithTooLongAddressReturnsValidationError() throws Exception {
        //given
        String longAddress = "A".repeat(256);
        String requestBody = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"phone\":\"123456789\",\"address\":\"" + longAddress + "\"}";
        //when&then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).create(any(CustomerDtos.CustomerCreateRequest.class));
    }

    @Test
    public void testCreateCustomerWhenEmailAlreadyExistsReturnsBadRequest() throws Exception {
        //given
        when(customerService.create(any(CustomerDtos.CustomerCreateRequest.class)))
                .thenThrow(new BadRequestException("Customer with that email already exists: john@example.com"));

        String requestBody = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"phone\":\"123456789\",\"address\":\"Test 1\"}";
        //when&then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).create(any(CustomerDtos.CustomerCreateRequest.class));
    }

    @Test
    public void testCreateCustomerWhenPhoneAlreadyExistsReturnsBadRequest() throws Exception {
        //given
        when(customerService.create(any(CustomerDtos.CustomerCreateRequest.class)))
                .thenThrow(new BadRequestException("Customer with that phone already exists: 123456789"));

        String requestBody = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"phone\":\"123456789\",\"address\":\"Test 1\"}";
        //when&then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).create(any(CustomerDtos.CustomerCreateRequest.class));
    }

    @Test
    public void testGetCustomerByIdSuccessfully() throws Exception {
        //given
        CustomerDtos.CustomerDto customer = new CustomerDtos.CustomerDto(
                1L, "John", "Doe", "john@example.com", "123456789", "Test 1",
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(customerService.get(1L)).thenReturn(customer);
        //when&then
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(customerService, times(1)).get(1L);
    }

    @Test
    public void testGetCustomerByIdWhenNotFoundReturnsNotFound() throws Exception {
        //given
        when(customerService.get(999L))
                .thenThrow(new NotFoundException("Customer not found: 999"));
        //when&then
        mockMvc.perform(get("/api/customers/999"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).get(999L);
    }

    @Test
    public void testUpdateCustomerSuccessfully() throws Exception {
        //given
        CustomerDtos.CustomerDto updatedCustomer = new CustomerDtos.CustomerDto(
                1L, "Jane", "Nowak", "jane@example.com", "987654321", "Test 2",
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(customerService.update(eq(1L), any(CustomerDtos.CustomerUpdateRequest.class)))
                .thenReturn(updatedCustomer);

        String requestBody = "{\"firstName\":\"Jane\",\"lastName\":\"Nowak\",\"email\":\"jane@example.com\",\"phone\":\"987654321\",\"address\":\"Test 2\"}";
        //when&then
        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Jane")))
                .andExpect(jsonPath("$.lastName", is("Nowak")))
                .andExpect(jsonPath("$.email", is("jane@example.com")));

        verify(customerService, times(1)).update(eq(1L), any(CustomerDtos.CustomerUpdateRequest.class));
    }

    @Test
    public void testUpdateCustomerWithBlankFirstNameReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"firstName\":\"\",\"lastName\":\"Nowak\",\"email\":\"jane@example.com\",\"phone\":\"987654321\",\"address\":\"Test 2\"}";
        //when&then
        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).update(any(Long.class), any(CustomerDtos.CustomerUpdateRequest.class));
    }

    @Test
    public void testUpdateCustomerWithInvalidEmailReturnsValidationError() throws Exception {
        //given
        String requestBody = "{\"firstName\":\"Jane\",\"lastName\":\"Nowak\",\"email\":\"invalid-email\",\"phone\":\"987654321\",\"address\":\"Test 2\"}";
        //when&then
        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).update(any(Long.class), any(CustomerDtos.CustomerUpdateRequest.class));
    }

    @Test
    public void testUpdateCustomerWhenNotFoundReturnsNotFound() throws Exception {
        //given
        when(customerService.update(eq(999L), any(CustomerDtos.CustomerUpdateRequest.class)))
                .thenThrow(new NotFoundException("Customer not found: 999"));

        String requestBody = "{\"firstName\":\"Jane\",\"lastName\":\"Nowak\",\"email\":\"jane@example.com\",\"phone\":\"987654321\",\"address\":\"Test 2\"}";
        //when&then
        mockMvc.perform(put("/api/customers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).update(eq(999L), any(CustomerDtos.CustomerUpdateRequest.class));
    }

    @Test
    public void testUpdateCustomerWhenEmailAlreadyExistsReturnsBadRequest() throws Exception {
        //given
        when(customerService.update(eq(1L), any(CustomerDtos.CustomerUpdateRequest.class)))
                .thenThrow(new BadRequestException("Customer with that email already exists: jane@example.com"));

        String requestBody = "{\"firstName\":\"Jane\",\"lastName\":\"Nowak\",\"email\":\"jane@example.com\",\"phone\":\"987654321\",\"address\":\"Test 2\"}";
        //when&then
        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).update(eq(1L), any(CustomerDtos.CustomerUpdateRequest.class));
    }

    @Test
    public void testUpdateCustomerWhenPhoneAlreadyExistsReturnsBadRequest() throws Exception {
        //given
        when(customerService.update(eq(1L), any(CustomerDtos.CustomerUpdateRequest.class)))
                .thenThrow(new BadRequestException("Customer with that phone already exists: 987654321"));

        String requestBody = "{\"firstName\":\"Jane\",\"lastName\":\"Nowak\",\"email\":\"jane@example.com\",\"phone\":\"987654321\",\"address\":\"Test 2\"}";
        //when&then
        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).update(eq(1L), any(CustomerDtos.CustomerUpdateRequest.class));
    }

    @Test
    public void testDeleteCustomerSuccessfully() throws Exception {
        //given
        doNothing().when(customerService).delete(1L);
        //when&then
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).delete(1L);
    }

    @Test
    public void testDeleteCustomerWhenNotFoundReturnsNotFound() throws Exception {
        //given
        doThrow(new NotFoundException("Customer not found: 999"))
                .when(customerService).delete(999L);
        //when&then
        mockMvc.perform(delete("/api/customers/999"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).delete(999L);
    }

    @Test
    public void testDeleteCustomerWhenHasRentalsReturnsBadRequest() throws Exception {
        //given
        doThrow(new BadRequestException("Cannot delete, because a Rental with that customer exists!"))
                .when(customerService).delete(1L);
        //when&then
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).delete(1L);
    }

    @Test
    public void testSearchCustomersWithQueryReturnsMatchingCustomers() throws Exception {
        //given
        CustomerDtos.CustomerDto customer1 = new CustomerDtos.CustomerDto(
                1L, "John", "Doe", "john@example.com", "123456789", "Test 1",
                LocalDateTime.now(), LocalDateTime.now()
        );
        CustomerDtos.CustomerDto customer2 = new CustomerDtos.CustomerDto(
                2L, "Jane", "Doe", "jane@example.com", "987654321", "Test 2",
                LocalDateTime.now(), LocalDateTime.now()
        );
        List<CustomerDtos.CustomerDto> customers = Arrays.asList(customer1, customer2);
        when(customerService.search("kowal")).thenReturn(customers);
        //when&then
        mockMvc.perform(get("/api/customers/search")
                        .param("query", "kowal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].lastName", is("Doe")))
                .andExpect(jsonPath("$[1].lastName", is("Doe")));

        verify(customerService, times(1)).search("kowal");
    }

    @Test
    public void testSearchCustomersWithNullQueryReturnsAllCustomers() throws Exception {
        //given
        CustomerDtos.CustomerDto customer1 = new CustomerDtos.CustomerDto(
                1L, "John", "Doe", "john@example.com", "123456789", "Test 1",
                LocalDateTime.now(), LocalDateTime.now()
        );
        List<CustomerDtos.CustomerDto> customers = List.of(customer1);
        when(customerService.search(null)).thenReturn(customers);
        //when&then
        mockMvc.perform(get("/api/customers/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(customerService, times(1)).search(null);
    }

    @Test
    public void testSearchCustomersWithEmptyQueryReturnsAllCustomers() throws Exception {
        //given
        CustomerDtos.CustomerDto customer1 = new CustomerDtos.CustomerDto(
                1L, "John", "Doe", "john@example.com", "123456789", "Test 1",
                LocalDateTime.now(), LocalDateTime.now()
        );
        List<CustomerDtos.CustomerDto> customers = List.of(customer1);
        when(customerService.search("")).thenReturn(customers);
        //when&then
        mockMvc.perform(get("/api/customers/search")
                        .param("query", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(customerService, times(1)).search("");
    }

    @Test
    public void testSearchCustomersWithNoMatchesReturnsEmptyList() throws Exception {
        //given
        when(customerService.search("xyz")).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/customers/search")
                        .param("query", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(customerService, times(1)).search("xyz");
    }

    @Test
    public void testRentalsReturnsCustomerRentals() throws Exception {
        //given
        RentalDtos.RentalDto rental1 = new RentalDtos.RentalDto(
                1L, 1L, "John Doe", 1L, "Toyota Corolla",
                LocalDateTime.now(), LocalDateTime.now().plusDays(3), null,
                RateType.DAILY, RentalStatus.ACTIVE, new BigDecimal("300.00"), BigDecimal.ZERO,
                new BigDecimal("300.00"), "Test rental"
        );
        RentalDtos.RentalDto rental2 = new RentalDtos.RentalDto(
                2L, 1L, "John Doe", 2L, "Honda Civic",
                LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(7),
                RateType.DAILY, RentalStatus.RETURNED, new BigDecimal("200.00"), BigDecimal.ZERO,
                new BigDecimal("200.00"), null
        );
        List<RentalDtos.RentalDto> rentals = Arrays.asList(rental1, rental2);
        when(customerService.getRentals(1L)).thenReturn(rentals);
        //when&then
        mockMvc.perform(get("/api/customers/1/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].customerId", is(1)))
                .andExpect(jsonPath("$[0].customerName", is("John Doe")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].status", is("RETURNED")));

        verify(customerService, times(1)).getRentals(1L);
    }

    @Test
    public void testRentalsReturnsEmptyListWhenNoRentals() throws Exception {
        //given
        when(customerService.getRentals(1L)).thenReturn(Collections.emptyList());
        //when&then
        mockMvc.perform(get("/api/customers/1/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(customerService, times(1)).getRentals(1L);
    }

    @Test
    public void testRentalsWhenCustomerNotFoundReturnsNotFound() throws Exception {
        //given
        when(customerService.getRentals(999L))
                .thenThrow(new NotFoundException("Customer not found: 999"));
        //when&then
        mockMvc.perform(get("/api/customers/999/rentals"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getRentals(999L);
    }
}