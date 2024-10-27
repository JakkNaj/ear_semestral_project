package cz.cvut.kbss.ear.Helpdesk.dao;

import cz.cvut.kbss.ear.Helpdesk.model.*;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeDao extends BaseDao<Employee>{

    protected EmployeeDao() {
        super(Employee.class);
    }

    public Employee findByEmail(String email) {
        try {
            return em.createNamedQuery("Employee.findByEmail", Employee.class).setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Employee> findByRole(EmployeeRole role) {
        try {
            return em.createNamedQuery("Employee.findByRole", Employee.class).setParameter("role", role)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<TrackedTime> getTrackedTimeOnRequest(Request request, Employee employee) {
        try {
            return em.createNamedQuery("TrackedTime.findByRequestAndEmployee", TrackedTime.class)
                    .setParameter("request", request)
                    .setParameter("employee", employee)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void deleteAll() {
        em.createNativeQuery("DELETE FROM employee").executeUpdate();
    }
}
