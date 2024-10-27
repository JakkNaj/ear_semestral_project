package cz.cvut.kbss.ear.Helpdesk.dao;

import cz.cvut.kbss.ear.Helpdesk.model.Customer;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerDao extends BaseDao<Customer> {

    protected CustomerDao() {
        super(Customer.class);
    }

    public Customer findByEmail(String email) {
        try {
            return em.createNamedQuery("Customer.findByEmail", Customer.class).setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
