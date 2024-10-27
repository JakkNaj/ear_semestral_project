package cz.cvut.kbss.ear.Helpdesk.security.model;

import cz.cvut.kbss.ear.Helpdesk.model.Customer;
import cz.cvut.kbss.ear.Helpdesk.model.Employee;
import cz.cvut.kbss.ear.Helpdesk.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserDetails extends org.springframework.security.core.userdetails.User {

    @Getter
    private final Customer customer;

    @Getter
    private final Employee employee;

    private final Set<GrantedAuthority> authorities;

    public UserDetails(Customer customer, Collection<? extends GrantedAuthority> authorities) {
        super(customer.getEmail(), customer.getPassword(), authorities);
        this.customer = customer;
        this.employee = null;
        this.authorities = new HashSet<>();
        this.authorities.addAll(authorities);
    }

    public UserDetails(Employee employee, Collection<? extends GrantedAuthority> authorities) {
        super(employee.getEmail(), employee.getPassword(), authorities);
        this.customer = null;
        this.employee = employee;
        this.authorities = new HashSet<>();
        this.authorities.addAll(authorities);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    @Override
    public String getPassword() {
        if (customer == null)
            return employee.getPassword();
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        if (customer == null)
            return employee.getEmail();
        return customer.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}


