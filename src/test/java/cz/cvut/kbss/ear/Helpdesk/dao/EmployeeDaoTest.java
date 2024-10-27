package cz.cvut.kbss.ear.Helpdesk.dao;

import cz.cvut.kbss.ear.Helpdesk.generator.Generator;
import cz.cvut.kbss.ear.Helpdesk.model.Department;
import cz.cvut.kbss.ear.Helpdesk.model.DepartmentType;
import cz.cvut.kbss.ear.Helpdesk.model.Employee;
import cz.cvut.kbss.ear.Helpdesk.model.EmployeeRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext
@ComponentScan(basePackages = "cz.cvut.kbss.ear.Helpdesk.dao")
public class EmployeeDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private DepartmentDao departmentDao;

    private final Generator generator = new Generator();


    private Department prepareDepartment(DepartmentType departmentType) {
        Employee manager = generator.generateManager();
        employeeDao.persist(manager);
        Department department = new Department(departmentType, new ArrayList<>(), manager);
        departmentDao.persist(department);
        return department;
    }

    @Test
    public void findAllReturnsAllEmployees() {
        Department department = prepareDepartment(DepartmentType.IT);
        Employee employee;
        for(int i = 0; i < 5; i++) {
            employee = generator.generateCommonEmployeeWithoutDepartment();
            department.addEmployee(employee);
            departmentDao.update(department);
            employeeDao.persist(employee);
        }

        final List<Employee> result = employeeDao.findAll();

        Assertions.assertNotNull(result);
        // 1 manager, 5 employees - IT department
        Assertions.assertEquals(6, result.size());
    }

    @Test
    public void findByEmailReturnsPersonWithMatchingEmail() {
        Department department = prepareDepartment(DepartmentType.BILLING);
        final Employee employee = generator.generateCommonEmployeeWithoutDepartment();
        department.addEmployee(employee);
        departmentDao.update(department);
        em.persist(employee);

        final Employee result = employeeDao.findByEmail(employee.getEmail());
        assertNotNull(result);
        assertEquals(employee.getId(), result.getId());
    }

    @Test
    public void findByEmailReturnsNullForUnknownEmail() {
        assertNull(employeeDao.findByEmail("unknownUsername"));
    }

    @Test
    public void findByEmployeeRoleReturnsPersonWithMatchingRole() {
        Integer numOfEmployessInDepartment = 5;
        //create Account department with employees
        Department department_acc = prepareDepartment(DepartmentType.ACCOUNT);
        Employee employee = generator.generateCommonEmployeeWithoutDepartment();
        for(int i = 0; i < numOfEmployessInDepartment; i++) {
            department_acc.addEmployee(employee);
            departmentDao.update(department_acc);
            employeeDao.persist(employee);
            employee = generator.generateCommonEmployeeWithoutDepartment();
        }

        final List<Employee> result = employeeDao.findByRole(employee.getRole());
        assertNotNull(result);
        //1 manager - doesn't count, 5 common employees
        Assertions.assertEquals(5, result.size());
    }

    @Test
    public void findByEmployeeRoleReturnsNullForNobodyWithThatRole() {
        Department department = prepareDepartment(DepartmentType.IT);

        //no common employees
        assertEquals(0, employeeDao.findByRole(EmployeeRole.COMMON).size());
        //1 manager
        assertEquals(1, employeeDao.findByRole(EmployeeRole.MANAGER).size());
    }
}

