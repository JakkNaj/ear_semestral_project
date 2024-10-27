package cz.cvut.kbss.ear.Helpdesk.service;

import cz.cvut.kbss.ear.Helpdesk.dao.DepartmentDao;
import cz.cvut.kbss.ear.Helpdesk.dao.EmployeeDao;
import cz.cvut.kbss.ear.Helpdesk.model.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class EmployeeService {

    final EmployeeDao employeeDao;

    final DepartmentDao departmentDao;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeService(EmployeeDao employeeDao, DepartmentDao departmentDao, PasswordEncoder passwordEncoder) {
        this.employeeDao = employeeDao;
        this.departmentDao = departmentDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Employee find(Integer id){
        return employeeDao.find(id);
    }

    @Transactional
    public Employee create(Employee employee) {
        Objects.requireNonNull(employee);
        employee.encodePassword(passwordEncoder);
        if (employee.getRole() == null) {
            employee.setRole(EmployeeRole.COMMON);
        }
        employeeDao.persist(employee);
        return employee;
    }

    //TODO test that employee is created in right department
    @Transactional
    public Employee create(Employee employee, Department department) {
        Objects.requireNonNull(employee);
        Objects.requireNonNull(department);
        employee.encodePassword(passwordEncoder);
        if (employee.getRole() == null) {
            employee.setRole(EmployeeRole.COMMON);
        }
        department.addEmployee(employee);
        employeeDao.persist(employee);
        return employee;
    }

    @Transactional
    public List<TrackedTime> getTrackedTimeOnRequest(Request request, Employee employee) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(employee);
        return employeeDao.getTrackedTimeOnRequest(request, employee);
    }

    @Transactional
    public List<Employee> getEmployees() {
        return employeeDao.findAll();
    }

    @Transactional
    public List<Employee> getByDepartmentType(DepartmentType departmentType) {
        return departmentDao.findByDepartmentType(departmentType).stream()
                .flatMap(department -> department.getEmployees().stream())
                .collect(Collectors.toList());
    }
}
