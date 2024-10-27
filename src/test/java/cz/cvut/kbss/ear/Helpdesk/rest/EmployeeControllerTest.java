package cz.cvut.kbss.ear.Helpdesk.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cz.cvut.kbss.ear.Helpdesk.generator.Generator;
import cz.cvut.kbss.ear.Helpdesk.model.DepartmentType;
import cz.cvut.kbss.ear.Helpdesk.model.Employee;
import cz.cvut.kbss.ear.Helpdesk.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest extends BaseControllerTestRunner{

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private final Generator generator = new Generator();

    @BeforeEach
    public void setUp(){
        super.setUp(employeeController);
    }

    @Test
    public void getAllReturnsAllEmployees() throws Exception {
        final List<Employee> employees = IntStream.range(0, 5).mapToObj(i -> generator.generateEmployee()).toList();
        when(employeeService.getEmployees()).thenReturn(employees);
        final MvcResult mvcResult = mockMvc.perform(get("/employee/all")).andReturn();
        final List<Employee> result = readValue(mvcResult, new TypeReference<>() {
        });
        assertNotNull(result);
        assertEquals(employees.size(), result.size());
        for (int i = 0; i < employees.size(); i++) {
            assertEquals(employees.get(i).getId(), result.get(i).getId());
            assertEquals(employees.get(i).getDepartment(), result.get(i).getDepartment());
            assertEquals(employees.get(i).getRole(), result.get(i).getRole());
            assertEquals(employees.get(i).getEmail(), result.get(i).getEmail());
            assertEquals(employees.get(i).getName(), result.get(i).getName());
            assertEquals(employees.get(i).getSurname(), result.get(i).getSurname());
        }
    }

    @Test
    public void getByDepartmentReturnsEmployeesWithGivenDepartment() throws Exception {
        final List<Employee> employees = IntStream.range(0, 5).mapToObj(i -> generator.generateEmployee()).toList();

        // Selecting employees with IT department
        final List<Employee> employeesIT = new ArrayList<>();
        for (Employee employee : employees) {
            employee.setDepartment(generator.getRandomDepartment());
            if (employee.getDepartment().getDepartmentType() == DepartmentType.IT)
                employeesIT.add(employee);
        }

        when(employeeService.getByDepartmentType(DepartmentType.IT)).thenReturn(employeesIT);
        final MvcResult mvcResult = mockMvc.perform(get("/employee/IT")).andReturn();
        final List<Employee> result = readValue(mvcResult, new TypeReference<>() {
        });

        assertNotNull(result);
        assertEquals(employeesIT.size(), result.size());
        for (int i = 0; i < employeesIT.size(); i++) {
            assertEquals(employeesIT.get(i).getId(), result.get(i).getId());
            assertEquals(employeesIT.get(i).getRole(), result.get(i).getRole());
            assertEquals(employeesIT.get(i).getEmail(), result.get(i).getEmail());
            assertEquals(employeesIT.get(i).getName(), result.get(i).getName());
            assertEquals(employeesIT.get(i).getSurname(), result.get(i).getSurname());
        }
    }
}
