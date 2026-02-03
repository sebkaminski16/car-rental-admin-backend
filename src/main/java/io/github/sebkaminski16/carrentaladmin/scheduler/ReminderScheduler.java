package io.github.sebkaminski16.carrentaladmin.scheduler;

import io.github.sebkaminski16.carrentaladmin.entity.Customer;
import io.github.sebkaminski16.carrentaladmin.repository.CustomerRepository;
import io.github.sebkaminski16.carrentaladmin.repository.RentalRepository;
import io.github.sebkaminski16.carrentaladmin.service.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ReminderScheduler {

    private static final int INACTIVE_CUSTOMER_DAYS = 30;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void remindInactiveCustomers() {

        LocalDateTime threshold = LocalDateTime.now().minusDays(INACTIVE_CUSTOMER_DAYS);
        List<Customer> customers = customerRepository.findAll();

        for (Customer c : customers) {
            long count = rentalRepository.countByCustomerId(c.getId());
            Optional<LocalDateTime> lastEnd = rentalRepository.findLastRentalEndForCustomer(c.getId());

            boolean inactive = (count == 0) || lastEnd.map(d -> d.isBefore(threshold)).orElse(true);
            if (inactive) {
                emailService.sendInactiveCustomerReminder(c, INACTIVE_CUSTOMER_DAYS);
            }
        }
    }
}
