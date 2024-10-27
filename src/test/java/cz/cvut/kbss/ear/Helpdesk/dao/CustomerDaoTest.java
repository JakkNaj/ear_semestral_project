package cz.cvut.kbss.ear.Helpdesk.dao;

import cz.cvut.kbss.ear.Helpdesk.Application;
import cz.cvut.kbss.ear.Helpdesk.generator.Generator;
import cz.cvut.kbss.ear.Helpdesk.model.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext
@ComponentScan(basePackages = "cz.cvut.kbss.ear.Helpdesk.dao")
public class CustomerDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CustomerDao customerDao;

    private final Generator generator = new Generator();

    @Test
    public void findAllReturnsAllCustomers() {
        Customer customer;
        for(int i = 0; i < 5; i++) {
            customer = generator.generateCustomer();
            em.persist(customer);
        }

        final List<Customer> result = customerDao.findAll();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(5, result.size());
    }

    @Test
    public void findByEmailReturnsPersonWithMatchingEmail() {
        final Customer customer = generator.generateCustomer();
        em.persist(customer);

        final Customer result = customerDao.findByEmail(customer.getEmail());
        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
    }

    @Test
    public void findByEmailReturnsNullForUnknownEmail() {
        assertNull(customerDao.findByEmail("unknownUsername"));
    }
}
