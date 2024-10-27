package cz.cvut.kbss.ear.Helpdesk.service;

import cz.cvut.kbss.ear.Helpdesk.dao.DepartmentDao;
import cz.cvut.kbss.ear.Helpdesk.dao.EmployeeDao;
import cz.cvut.kbss.ear.Helpdesk.exception.UnauthorizedException;
import cz.cvut.kbss.ear.Helpdesk.model.Department;
import cz.cvut.kbss.ear.Helpdesk.model.Employee;
import cz.cvut.kbss.ear.Helpdesk.model.EmployeeRole;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DepartmentService {

    final DepartmentDao departmentDao;

    final EmployeeDao employeeDao;

    @Autowired
    public DepartmentService(DepartmentDao departmentDao, EmployeeDao employeeDao) {
        this.departmentDao = departmentDao;
        this.employeeDao = employeeDao;
    }

    @Transactional
    public Department create(Department department){
        Objects.requireNonNull(department);
        departmentDao.persist(department);
        return department;
    }

    @Transactional
    public void setDepartmentHead(Department department, Employee employee) {
        Objects.requireNonNull(employee);
        Objects.requireNonNull(department);
        if (employee.getRole() != EmployeeRole.MANAGER)
            throw new UnauthorizedException("Only managers can become department head");
        department.setDepartmentHead(employee);
        employeeDao.update(employee);
        departmentDao.update(department);
    }

    @Transactional
    public void addEmployees(Department department, List<Employee> employees) {
        Objects.requireNonNull(department);
        Objects.requireNonNull(employees);
        department.addEmployeeList(employees);
        employees.forEach(employeeDao::update);
        departmentDao.update(department);
    }

    @Transactional
    public void addEmployee(Department department, Employee employee) {
        Objects.requireNonNull(department);
        Objects.requireNonNull(employee);
        department.addEmployee(employee);
        employeeDao.update(employee);
        departmentDao.update(department);
    }

    @Transactional
    public List<Department> getAll() {
        return departmentDao.findAll();
    }
}
