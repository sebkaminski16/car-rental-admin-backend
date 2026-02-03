package io.github.sebkaminski16.carrentaladmin.service;

import io.github.sebkaminski16.carrentaladmin.entity.Customer;
import io.github.sebkaminski16.carrentaladmin.integration.mailtrap.MailtrapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.email.fromEmail}")
    private String fromEmail;;

    @Autowired
    private MailtrapClient mailtrapClient;

    public boolean sendTestEmail(String toEmail, String subject, String text) {
        return mailtrapClient.sendTextEmail(fromEmail, toEmail, subject, text);
    }

    public boolean sendInactiveCustomerReminder(Customer customer, int inactiveDays) {
        String subject = "Are you there? You haven't rented anything for long time!";
        String text = "Hey" + customer.getFirstName() + ",\n\n"
                + "You haven't rented any car for at least " + inactiveDays + " days. Consider coming back!\n\n"
                + "Cheers,\nCar Rental Team";

        return mailtrapClient.sendTextEmail(fromEmail, customer.getEmail(), subject, text);
    }
}
