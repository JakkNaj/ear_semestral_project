package cz.cvut.kbss.ear.Helpdesk.service;

import cz.cvut.kbss.ear.Helpdesk.dao.CustomerDao;
import cz.cvut.kbss.ear.Helpdesk.model.Customer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Objects;

@Service
public class CustomerService {

    final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerDao customerDao, PasswordEncoder passwordEncoder) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Customer create(Customer customer) {
        Objects.requireNonNull(customer);
        customer.encodePassword(passwordEncoder);
        customerDao.persist(customer);
        return customer;
    }

    @Transactional
    public Customer findByEmail(String email) {
        return customerDao.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean exists(String email) {
        return customerDao.findByEmail(email) != null;
    }

    @Transactional
    public List<Customer> getCustomers(){
        return customerDao.findAll();
    }

}
