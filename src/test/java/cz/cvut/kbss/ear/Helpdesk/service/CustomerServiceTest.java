package cz.cvut.kbss.ear.Helpdesk.service;

import cz.cvut.kbss.ear.Helpdesk.model.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import cz.cvut.kbss.ear.Helpdesk.dao.CustomerDao;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    private CustomerService customerService;

    @Mock
    private CustomerDao customerDao;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void init() {
        this.customerService = new CustomerService(this.customerDao, passwordEncoder);
    }

    @Test
    public void createEncodesCustomerPassword() {
        Customer customer = new Customer();
        String rawPassword = "SezameOtevriSe";
        customer.setPassword(rawPassword);

        customerService.create(customer);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).persist(captor.capture());
        Assertions.assertTrue(passwordEncoder.matches(rawPassword, captor.getValue().getPassword()));
    }
}
