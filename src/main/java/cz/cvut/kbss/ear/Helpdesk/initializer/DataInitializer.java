package cz.cvut.kbss.ear.Helpdesk.initializer;

import cz.cvut.kbss.ear.Helpdesk.dao.DepartmentDao;
import cz.cvut.kbss.ear.Helpdesk.dao.EmployeeDao;
import cz.cvut.kbss.ear.Helpdesk.model.*;
import cz.cvut.kbss.ear.Helpdesk.service.CustomerService;
import cz.cvut.kbss.ear.Helpdesk.service.DepartmentService;
import cz.cvut.kbss.ear.Helpdesk.service.EmployeeService;
import cz.cvut.kbss.ear.Helpdesk.service.RequestService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Random;
import java.util.List;


@Component
public class DataInitializer {

    private final EmployeeService employeeService;

    private final DepartmentService departmentService;

    private final CustomerService customerService;

    private final RequestService requestService;

    private static final Random RANDOM = new Random();
    private final PlatformTransactionManager txManager;

    public static int randomInt() {
        return RANDOM.nextInt();
    }

    @Autowired
    public DataInitializer(EmployeeService employeeService, DepartmentService departmentService,
                           CustomerService customerService, RequestService requestService,
                           PlatformTransactionManager txManager) {
        this.txManager = txManager;
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.customerService = customerService;
        this.requestService = requestService;
    }

    @PostConstruct
    public void init() {
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        txTemplate.execute((status) -> {
            //initFirm();
            //initAdmin();
            //initCustomers();
            return null;
        });
    }

    public void initFirm() {
        //create IT department
        Department departmentIT = departmentService.create(generateDepartment(DepartmentType.BILLING));
        //create employees and manager
        Employee itManager = employeeService.create(generateManager());
        departmentService.setDepartmentHead(departmentIT, itManager);

        Employee employee1 = employeeService.create(generateCommonEmployeeWithoutDepartment());
        Employee employee2 = employeeService.create(generateCommonEmployeeWithoutDepartment());
        departmentService.addEmployees(departmentIT, List.of(employee1, employee2));
    }

    public void initCustomers() {
        int customer_count = 4;
        for (int i = 0; i < customer_count; i++)
            customerService.create(generateCustomer(i));
    }


    public void initAdmin() {
        Employee kuba = new Employee();
        kuba.setRole(EmployeeRole.MANAGER);
        kuba.setName("Jakub");
        kuba.setSurname("Najman");
        kuba.setPassword("123");
        kuba.setEmail("kuba@kuba.cz");
        //init admin
        employeeService.create(kuba);
    }

    private Department generateDepartment(DepartmentType departmentType) {
        Department dep = new Department();
        dep.setDepartmentType(departmentType);
        return dep;
    }

    private Employee generateEmployee(){
        Employee emp = new Employee();
        emp.setName("name" + randomInt());
        emp.setSurname("surname" + randomInt());
        emp.setPassword("employee");
        emp.setEmail("employee01" + randomInt() + "@zk.com");
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

    public Customer generateCustomer() {
        Customer customer = new Customer();
        customer.setName("cust" + randomInt());
        customer.setSurname("omer" + randomInt());
        customer.setPassword("customer");
        customer.setEmail("cust.omer" + randomInt() + "@customer.com");
        return customer;
    }

    public Customer generateCustomer(int emailNum) {
        Customer customer = new Customer();
        customer.setName("cust" + randomInt());
        customer.setSurname("omer" + randomInt());
        customer.setPassword("customer");
        customer.setEmail("cust.omer" + emailNum + "@customer.com");
        return customer;
    }
}

