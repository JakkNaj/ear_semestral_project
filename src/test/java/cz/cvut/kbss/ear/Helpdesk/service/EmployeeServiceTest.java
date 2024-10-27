package cz.cvut.kbss.ear.Helpdesk.service;

import cz.cvut.kbss.ear.Helpdesk.dao.DepartmentDao;
import cz.cvut.kbss.ear.Helpdesk.dao.EmployeeDao;
import cz.cvut.kbss.ear.Helpdesk.model.Employee;
import cz.cvut.kbss.ear.Helpdesk.model.EmployeeRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    private EmployeeService employeeService;

    private DepartmentDao departmentDao;

    @Mock
    private EmployeeDao employeeDao;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    public void init() {
        this.employeeService = new EmployeeService(employeeDao, departmentDao, passwordEncoder);
    }

    @Test
    public void createEncodesEmployeePassword() {
        Employee emp = new Employee();
        String rawPassword = "SezameOtevriSe";
        emp.setPassword(rawPassword);

        employeeService.create(emp);

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeDao).persist(captor.capture());
        Assertions.assertTrue(passwordEncoder.matches(rawPassword, captor.getValue().getPassword()));
    }

    @Test
    public void createEmployeeWithRoleGivesHimCommonRole() {
        Employee emp = new Employee();
        String rawPassword = "SezameOtevriSe";
        emp.setPassword(rawPassword);

        employeeService.create(emp);

        Assertions.assertEquals(EmployeeRole.COMMON, emp.getRole());
    }
}
