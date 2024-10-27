package cz.cvut.kbss.ear.Helpdesk.dao;

import cz.cvut.kbss.ear.Helpdesk.generator.Generator;
import cz.cvut.kbss.ear.Helpdesk.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext
@ComponentScan(basePackages = "cz.cvut.kbss.ear.Helpdesk.dao")
@ActiveProfiles("test")
public class RequestDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private RequestDao requestDao;

    private Generator generator = new Generator();

    @Test
    public void testFindByType() {
        //generate data
        Integer requestTypeCount = 5;
        for (int i = 0; i < requestTypeCount; i++) {
            Customer customer = generator.generateCustomer();
            em.persist(customer);
            Request request = new Request(customer, "test" + i, DepartmentType.IT);
            request.setDateCreated(LocalDateTime.now());
            requestDao.persist(request);
        }
        for (int i = 0; i < 3; i++) {
            Customer customer = generator.generateCustomer();
            em.persist(customer);
            Request request = new Request(customer, "test" + i, DepartmentType.BILLING);
            request.setDateCreated(LocalDateTime.now());
            requestDao.persist(request);
        }

        //filter the persisted requests
        List<Request> requests = requestDao.findByType(DepartmentType.IT);

        // Verify the results
        assertEquals(requestTypeCount, requests.size());
        requests.forEach(request -> Assertions.assertSame(request.getType(), DepartmentType.IT));
    }

    @Test
    public void testFindByPriority() {
        //generate data
        Integer requestTypeCount = 5;
        for (int i = 0; i < requestTypeCount; i++) {
            Customer customer = generator.generateCustomer();
            em.persist(customer);
            Request request = new Request(customer, "test" + i, DepartmentType.IT);
            request.setDateCreated(LocalDateTime.now());
            request.setPriority(RequestPriority.HIGH);
            requestDao.persist(request);
        }

        for (int i = 0; i < requestTypeCount; i++) {
            Customer customer = generator.generateCustomer();
            em.persist(customer);
            Request request = new Request(customer, "test" + i, DepartmentType.IT);
            request.setDateCreated(LocalDateTime.now());
            request.setPriority(RequestPriority.MODERATE);
            requestDao.persist(request);
        }

        //filter the persisted requests
        List<Request> requests = requestDao.findByPriority(RequestPriority.HIGH);

        // Verify the results
        assertEquals(requestTypeCount, requests.size());
        requests.forEach(request -> Assertions.assertSame(request.getPriority(), RequestPriority.HIGH));
    }

    @Test
    public void testFindByCustomer() {
        //generate data
        Customer customer = generator.generateCustomer();
        Integer requestTypeCount = 5;
        for (int i = 0; i < requestTypeCount; i++) {
            em.persist(customer);
            Request request = new Request(customer, "test" + i, DepartmentType.IT);
            request.setDateCreated(LocalDateTime.now());
            requestDao.persist(request);
        }

        for (int i = 0; i < requestTypeCount; i++) {
            Customer customer2 = generator.generateCustomer();
            em.persist(customer2);
            Request request = new Request(customer2, "test" + i, DepartmentType.IT);
            request.setDateCreated(LocalDateTime.now());
            requestDao.persist(request);
        }

        //filter the persisted requests
        List<Request> requests = requestDao.findByCustomer(customer);

        // Verify the results
        assertEquals(requestTypeCount, requests.size());
        requests.forEach(request -> Assertions.assertSame(request.getCustomer(), customer));
    }

    @Test
    public void testFindUntilDeadline() {
        //generate data
        Integer requestTypeCount = 5;
        for (int i = 0; i < requestTypeCount; i++) {
            Customer customer = generator.generateCustomer();
            em.persist(customer);
            Request request = new Request(customer, "test" + i, DepartmentType.IT);
            request.setDateCreated(LocalDateTime.now());
            request.setDeadline(LocalDateTime.now());
            requestDao.persist(request);
        }

        for (int i = 0; i < requestTypeCount; i++) {
            Customer customer2 = generator.generateCustomer();
            em.persist(customer2);
            Request request = new Request(customer2, "test" + i, DepartmentType.IT);
            request.setDateCreated(LocalDateTime.now());
            request.setDeadline(LocalDateTime.now().plusMonths(3));
            requestDao.persist(request);
        }

        //filter the persisted requests
        List<Request> requests = requestDao.findUntilDeadline(LocalDateTime.now());

        // Verify the results
        assertEquals(requestTypeCount, requests.size());
        requests.forEach(request ->
                Assertions.assertEquals(request.getDeadline().toLocalDate(), LocalDateTime.now().toLocalDate()));
    }

    @Test
    public void testFindByState() {
        //generate data
        Customer customer = generator.generateCustomer();
        Integer requestTypeCount = 5;
        for (int i = 0; i < requestTypeCount; i++) {
            em.persist(customer);
            Request request = new Request(customer, "test" + i, DepartmentType.IT);
            request.setDateCreated(LocalDateTime.now());
            if ( i == 0 ) request.setState(RequestState.CLOSED);
            requestDao.persist(request);
        }



        //filter the persisted requests
        List<Request> requests = requestDao.findByState(RequestState.CLOSED);

        // Verify the results
        assertEquals(1, requests.size());
        requests.forEach(request -> Assertions.assertSame(request.getState(), RequestState.CLOSED));
    }


}
