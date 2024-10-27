package cz.cvut.kbss.ear.Helpdesk.service.security;

import cz.cvut.kbss.ear.Helpdesk.dao.CustomerDao;
import cz.cvut.kbss.ear.Helpdesk.dao.EmployeeDao;
import cz.cvut.kbss.ear.Helpdesk.model.Customer;
import cz.cvut.kbss.ear.Helpdesk.model.Employee;

import cz.cvut.kbss.ear.Helpdesk.security.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final CustomerDao customerDao;
    private final EmployeeDao employeeDao;

    @Autowired
    public UserDetailsService(CustomerDao customerDao, EmployeeDao employeeDao) {
        this.customerDao = customerDao;
        this.employeeDao = employeeDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerDao.findByEmail(email);
        if (customer != null) {
            return new UserDetails(customer, getAuthorities("ROLE_CUSTOMER"));
        }

        Employee employee = employeeDao.findByEmail(email);
        if (employee != null) {
            return new UserDetails(employee, getAuthorities(employee.getRole().toString()));
        }

        throw new UsernameNotFoundException("User with email: " + email + " not found.");
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}







