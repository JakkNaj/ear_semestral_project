package cz.cvut.kbss.ear.Helpdesk.generator;

import cz.cvut.kbss.ear.Helpdesk.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Generator {

    private static final Random RANDOM = new Random();

    public static int randomInt() {
        return RANDOM.nextInt();
    }
    public Generator(){
    }


    public Customer generateCustomer() {
        Customer customer = new Customer();
        customer.setName("name" + randomInt());
        customer.setSurname("surname" + randomInt());
        customer.setPassword("passwd" + randomInt());
        customer.setEmail("name.surname" + randomInt() + "@zkouska.com");
        return customer;
    }

    public Employee generateEmployee(){
        Employee emp = new Employee();
        emp.setName("name" + randomInt());
        emp.setSurname("surname" + randomInt());
        emp.setPassword("passwd" + randomInt());
        emp.setEmail("name.surname" + randomInt() + "@zkouska.com");
        return emp;
    }

    public Employee generateManager(){
        Employee emp = generateEmployee();
        emp.setRole(EmployeeRole.MANAGER);
        return emp;
    }

    public Employee generateCommonEmployeeWithoutDepartment(){
        Employee emp = generateEmployee();
        emp.setRole(EmployeeRole.COMMON);
        return emp;
    }

    public static Request generateRequest() {
        final Request req = new Request();
        req.setPriority(getRandomPrio());
        req.setText("problem num. " + randomInt());
        req.setDateCreated(LocalDateTime.now().minusDays(randomInt()));
        req.setType(getRandomType());
        req.setDeadline(LocalDateTime.now().plusDays(randomInt()));
        return req;
    }


    public static RequestPriority getRandomPrio(){
        final RequestPriority[] priorities = RequestPriority.values();
        return priorities[RANDOM.nextInt(priorities.length)];
    }

    public static DepartmentType getRandomType(){
        final DepartmentType[] types = DepartmentType.values();
        return types[RANDOM.nextInt(types.length)];
    }

    public Department getRandomDepartment() {
        Department department =  new Department();
        department.setDepartmentHead(generateManager());
        department.setEmployees(IntStream.range(0, 5).mapToObj(i -> generateEmployee()).toList());
        department.setDepartmentType(getRandomType());
        return department;
    }
}
