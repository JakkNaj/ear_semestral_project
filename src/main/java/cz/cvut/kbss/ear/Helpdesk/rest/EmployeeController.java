package cz.cvut.kbss.ear.Helpdesk.rest;

import cz.cvut.kbss.ear.Helpdesk.model.*;
import cz.cvut.kbss.ear.Helpdesk.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Employee> findAll() {
        return employeeService.getEmployees();
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping(value = "/{departmentType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Employee> getEmployeesByDepartmentType(@PathVariable DepartmentType departmentType) {
        return employeeService.getByDepartmentType(departmentType);
    }

}
