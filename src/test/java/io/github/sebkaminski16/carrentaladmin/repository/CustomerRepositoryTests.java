package io.github.sebkaminski16.carrentaladmin.repository;

import io.github.sebkaminski16.carrentaladmin.entity.Customer;
import io.github.sebkaminski16.carrentaladmin.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testSaveCustomer() {
        //given
        Customer customer = TestDataFactory.customer("john.doe@example.com");
        //when
        Customer saved = customerRepository.save(customer);
        //then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindById() {
        //given
        Customer customer = TestDataFactory.customer("jane.smith@example.com");
        Customer saved = customerRepository.save(customer);
        //when
        Optional<Customer> found = customerRepository.findById(saved.getId());
        //then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void testFindByIdNotFound() {
        //given
        Long nonExistentId = 999L;
        //when
        Optional<Customer> found = customerRepository.findById(nonExistentId);
        //then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindAll() {
        //given
        Customer customer1 = TestDataFactory.customer("alice@example.com");
        Customer customer2 = TestDataFactory.customer("bob@example.com");
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        //when
        List<Customer> customers = customerRepository.findAll();
        //then
        assertThat(customers).hasSize(2);
        assertThat(customers).extracting(Customer::getEmail)
                .containsExactlyInAnyOrder("alice@example.com", "bob@example.com");
    }

    @Test
    void testUpdateCustomer() {
        //given
        Customer customer = TestDataFactory.customer("update@example.com");
        Customer saved = customerRepository.save(customer);

        saved.setFirstName("Updated");
        saved.setPhone("987654321");
        //when
        Customer updated = customerRepository.save(saved);
        //then
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getFirstName()).isEqualTo("Updated");
        assertThat(updated.getPhone()).isEqualTo("987654321");
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(updated.getCreatedAt());
    }

    @Test
    void testDeleteCustomer() {
        //given
        Customer customer = TestDataFactory.customer("delete@example.com");
        Customer saved = customerRepository.save(customer);
        Long customerId = saved.getId();
        //when
        customerRepository.delete(saved);
        //then
        Optional<Customer> found = customerRepository.findById(customerId);
        assertThat(found).isEmpty();
    }

    @Test
    void testDeleteById() {
        //given
        Customer customer = TestDataFactory.customer("deletebyid@example.com");
        Customer saved = customerRepository.save(customer);
        Long customerId = saved.getId();
        //when
        customerRepository.deleteById(customerId);
        //then
        Optional<Customer> found = customerRepository.findById(customerId);
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseByFirstName() {
        //given
        Customer customer1 = Customer.builder()
                .firstName("Michael")
                .lastName("Johnson")
                .email("michael.johnson@example.com")
                .phone("111222333")
                .address("Address 1")
                .build();
        Customer customer2 = Customer.builder()
                .firstName("Sarah")
                .lastName("Williams")
                .email("sarah.williams@example.com")
                .phone("444555666")
                .address("Address 2")
                .build();
        Customer customer3 = Customer.builder()
                .firstName("Michelle")
                .lastName("Brown")
                .email("michelle.brown@example.com")
                .phone("777888999")
                .address("Address 3")
                .build();
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
        //when
        List<Customer> found = customerRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "mich", "mich", "mich");
        //then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Customer::getFirstName)
                .containsExactlyInAnyOrder("Michael", "Michelle");
    }

    @Test
    void testFindByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseByLastName() {
        // given
        Customer customer1 = Customer.builder()
                .firstName("John")
                .lastName("Anderson")
                .email("john.anderson@example.com")
                .phone("111000111")
                .address("Address 1")
                .build();
        Customer customer2 = Customer.builder()
                .firstName("Emily")
                .lastName("Sanders")
                .email("emily.sanders@example.com")
                .phone("222000222")
                .address("Address 2")
                .build();
        Customer customer3 = Customer.builder()
                .firstName("David")
                .lastName("Taylor")
                .email("david.taylor@example.com")
                .phone("333000333")
                .address("Address 3")
                .build();
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
        //when
        List<Customer> found = customerRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "anders", "anders", "anders");
        //then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Customer::getLastName)
                .containsExactlyInAnyOrder("Anderson", "Sanders");
    }

    @Test
    void testFindByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseByEmail() {
        // given
        Customer customer1 = Customer.builder()
                .firstName("Robert")
                .lastName("Wilson")
                .email("robert.wilson@gmail.com")
                .phone("444000444")
                .address("Address 1")
                .build();
        Customer customer2 = Customer.builder()
                .firstName("Lisa")
                .lastName("Martinez")
                .email("lisa.martinez@yahoo.com")
                .phone("555000555")
                .address("Address 2")
                .build();
        Customer customer3 = Customer.builder()
                .firstName("James")
                .lastName("Garcia")
                .email("james.garcia@gmail.com")
                .phone("666000666")
                .address("Address 3")
                .build();
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);

        //when
        List<Customer> found = customerRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "gmail", "gmail", "gmail");
        //then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Customer::getEmail)
                .containsExactlyInAnyOrder("robert.wilson@gmail.com", "james.garcia@gmail.com");
    }

    @Test
    void testFindByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseCaseInsensitive() {
        //given
        Customer customer = Customer.builder()
                .firstName("Patricia")
                .lastName("Thompson")
                .email("patricia.thompson@example.com")
                .phone("777000777")
                .address("Address 1")
                .build();
        customerRepository.save(customer);
        //when
        List<Customer> found = customerRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "PATRICIA", "PATRICIA", "PATRICIA");
        //then
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getFirstName()).isEqualTo("Patricia");
    }

    @Test
    void testFindByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseNoMatch() {
        //given
        Customer customer = Customer.builder()
                .firstName("Christopher")
                .lastName("Lee")
                .email("christopher.lee@example.com")
                .phone("888000888")
                .address("Address 1")
                .build();
        customerRepository.save(customer);
        //when
        List<Customer> found = customerRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "NotFound", "NotFound", "NotFound");
        //then
        assertThat(found).isEmpty();
    }

    @Test
    void testExistsByEmailTrue() {
        //given
        Customer customer = TestDataFactory.customer("exists@example.com");
        customerRepository.save(customer);
        //when
        boolean exists = customerRepository.existsByEmail("exists@example.com");
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByEmailFalse() {
        //given
        Customer customer = TestDataFactory.customer("another@example.com");
        customerRepository.save(customer);
        //when
        boolean exists = customerRepository.existsByEmail("notfound@example.com");
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByPhoneTrue() {
        //given
        Customer customer = Customer.builder()
                .firstName("Mark")
                .lastName("Davis")
                .email("mark.davis@example.com")
                .phone("123456789")
                .address("Address 1")
                .build();
        customerRepository.save(customer);
        //when
        boolean exists = customerRepository.existsByPhone("123456789");
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByPhoneFalse() {
        //given
        Customer customer = Customer.builder()
                .firstName("Nancy")
                .lastName("Moore")
                .email("nancy.moore@example.com")
                .phone("111222333")
                .address("Address 1")
                .build();
        customerRepository.save(customer);

        //when
        boolean exists = customerRepository.existsByPhone("999888777");
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByPhoneAndIdNotTrue() {
        //given
        Customer customer1 = Customer.builder()
                .firstName("Steven")
                .lastName("Clark")
                .email("steven.clark@example.com")
                .phone("555111222")
                .address("Address 1")
                .build();
        Customer customer2 = Customer.builder()
                .firstName("Karen")
                .lastName("Lewis")
                .email("karen.lewis@example.com")
                .phone("555333444")
                .address("Address 2")
                .build();
        customerRepository.save(customer1);
        Customer saved2 = customerRepository.save(customer2);

        //when
        boolean exists = customerRepository.existsByPhoneAndIdNot("555111222", saved2.getId());

        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByPhoneAndIdNotFalseWhenSameId() {
        //given
        Customer customer = Customer.builder()
                .firstName("Brian")
                .lastName("Walker")
                .email("brian.walker@example.com")
                .phone("777444555")
                .address("Address 1")
                .build();
        Customer saved = customerRepository.save(customer);
        //when
        boolean exists = customerRepository.existsByPhoneAndIdNot("777444555", saved.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByPhoneAndIdNotFalseWhenPhoneDoesNotExist() {
        //given
        Customer customer = Customer.builder()
                .firstName("Betty")
                .lastName("Hall")
                .email("betty.hall@example.com")
                .phone("888555666")
                .address("Address 1")
                .build();
        Customer saved = customerRepository.save(customer);
        //when
        boolean exists = customerRepository.existsByPhoneAndIdNot("999000111", saved.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByEmailAndIdNotTrue() {
        //given
        Customer customer1 = TestDataFactory.customer("first@example.com");
        Customer customer2 = TestDataFactory.customer("second@example.com");
        customerRepository.save(customer1);
        Customer saved2 = customerRepository.save(customer2);
        //when
        boolean exists = customerRepository.existsByEmailAndIdNot("first@example.com", saved2.getId());
        //then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByEmailAndIdNotFalseWhenSameId() {
        //given
        Customer customer = TestDataFactory.customer("sameid@example.com");
        Customer saved = customerRepository.save(customer);
        //when
        boolean exists = customerRepository.existsByEmailAndIdNot("sameid@example.com", saved.getId());
        //then
        assertThat(exists).isFalse();
    }

    @Test
    void testExistsByEmailAndIdNotFalseWhenEmailDoesNotExist() {
        // given
        Customer customer = TestDataFactory.customer("existing@example.com");
        Customer saved = customerRepository.save(customer);
        //when
        boolean exists = customerRepository.existsByEmailAndIdNot("nonexistent@example.com", saved.getId());
        //then
        assertThat(exists).isFalse();
    }
}