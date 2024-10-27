package cz.cvut.kbss.ear.Helpdesk.service.security;

import cz.cvut.kbss.ear.Helpdesk.model.Customer;
import cz.cvut.kbss.ear.Helpdesk.model.Employee;
import cz.cvut.kbss.ear.Helpdesk.environment.Environment;
import cz.cvut.kbss.ear.Helpdesk.generator.Generator;
import cz.cvut.kbss.ear.Helpdesk.security.SecurityUtils;
import cz.cvut.kbss.ear.Helpdesk.security.model.UserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityUtilsTest {

    private Customer customer;
    private Employee employee;

    private Generator generator = new Generator();

    @BeforeEach
    public void setUp() {
        this.customer = generator.generateCustomer();
        customer.setId(Generator.randomInt());
        this.employee = generator.generateCommonEmployeeWithoutDepartment();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getCurrentCustomerReturnsCurrentlyLoggedInCustomer() {
        Environment.setCurrentUserCustomer(customer);
        final Customer result = SecurityUtils.getCurrentUserDetails().getCustomer();
        assertEquals(customer, result);
    }

    @Test
    public void getCurrentEmployeeReturnsCurrentlyLoggedInEmployee() {
        Environment.setCurrentUserEmployee(employee);
        final Employee result = SecurityUtils.getCurrentUserDetails().getEmployee();
        assertEquals(employee, result);
    }

    @Test
    public void getCurrentUserDetailsReturnsUserDetailsOfCurrentlyLoggedInUser() {
        Environment.setCurrentUserCustomer(customer);
        final UserDetails result = SecurityUtils.getCurrentUserDetails();
        assertNotNull(result);
        assertTrue(result.isEnabled());
        assertEquals(customer, result.getCustomer());
    }

    @Test
    public void getCurrentUserDetailsReturnsNullIfNoUserIsLoggedIn() {
        assertNull(SecurityUtils.getCurrentUserDetails());
    }
}
