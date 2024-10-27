package cz.cvut.kbss.ear.Helpdesk.service;


import cz.cvut.kbss.ear.Helpdesk.dao.RequestDao;
import cz.cvut.kbss.ear.Helpdesk.exception.InvalidStateException;
import cz.cvut.kbss.ear.Helpdesk.exception.UnauthorizedException;
import cz.cvut.kbss.ear.Helpdesk.generator.Generator;
import cz.cvut.kbss.ear.Helpdesk.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RequestServiceTest {

    @InjectMocks
    private RequestService requestService;

    @Mock
    private RequestDao requestDao;

    private AutoCloseable closeable;

    private Generator generator = new Generator();


    @BeforeEach
    public void init() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void clean() {
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //----------- CREATE REQUEST TEST ------------------------------
    @Test
    public void testCreateRequest() {
        //create mock customer
        Customer customer = new Customer();
        //create the request
        String text = "testRequest";
        Request request = requestService.createRequest(customer, text, DepartmentType.IT);

        //verify that request dao persisted the newly created request
        verify(requestDao).persist(request);
        //assert the insides of newly created request
        Assertions.assertEquals(DepartmentType.IT, request.getType());
        Assertions.assertEquals(text, request.getText());
        Assertions.assertEquals(RequestState.NEW, request.getState());
        Assertions.assertNull(request.getEmployee());
        Assertions.assertNull(request.getDeadline());
        Assertions.assertEquals(0, request.getComments().size());
        Assertions.assertEquals(0, request.getTrackedTimesList().size());

    }

    //----------- ASSIGN REQUEST TEST ------------------------------

    private Department prepareDepartment(DepartmentType departmentType) {
        Employee manager = generator.generateManager();
        Department department = new Department(departmentType, new ArrayList<>(), manager);
        return department;
    }

    @Test
    public void assignRequestToDifferentDepartmentThanRequestType() {
        //create BILLING request
        Customer customer = generator.generateCustomer();
        DepartmentType requestType = DepartmentType.BILLING;
        Request request = new Request(customer, "testRequest", requestType);
        //create IT department
        Department department = prepareDepartment(DepartmentType.IT);
        //create employee and assign it to different department
        Employee employee = generator.generateCommonEmployeeWithoutDepartment();
        //assign employee to the IT department
        department.addEmployee(employee);

        //try to assign employee the request
        Assertions.assertThrows(UnauthorizedException.class,
                () -> requestService.assignRequest(request, department.getDepartmentHead(), employee));
    }


    @Test
    public void assignRequestNotFromManagerPosition() {
        //create BILLING request
        Customer customer = generator.generateCustomer();
        DepartmentType requestType = DepartmentType.BILLING;
        Request request = new Request(customer, "testRequest", requestType);
        //create IT department
        Department department = prepareDepartment(DepartmentType.BILLING);
        //create employees
        Employee employeeOne = generator.generateCommonEmployeeWithoutDepartment();
        Employee employeeTwo = generator.generateCommonEmployeeWithoutDepartment();
        //assign employees to the BILLING department
        department.addEmployee(employeeOne);
        department.addEmployee(employeeTwo);

        //try to assign request as being common employee
        Assertions.assertThrows(UnauthorizedException.class,
                () -> requestService.assignRequest(request, employeeOne, employeeTwo));
    }


    @Test
    public void correctAssignOfRequestFromManagerToEmployee() {
        //create IT request
        Customer customer = generator.generateCustomer();
        DepartmentType requestType = DepartmentType.IT;
        Request request = new Request(customer, "testRequest", requestType);
        //create IT department
        Department department = prepareDepartment(DepartmentType.IT);
        //create employee and assign it to different department
        Employee employee = generator.generateCommonEmployeeWithoutDepartment();
        //assign employee to the IT department
        department.addEmployee(employee);

        //try to assign employee the request
        requestService.assignRequest(request, department.getDepartmentHead(), employee);

        //assert update was called
        verify(requestDao).update(request);
        //assert that employee is correctly assigned to request
        Assertions.assertEquals(employee, request.getEmployee());
        //assert the state of request
        Assertions.assertEquals(RequestState.ASSIGNED, request.getState());
    }

    //----------- RESOLVE REQUEST TEST ------------------------------

    @Test
    public void resolveNotFromAssignedEmployee(){
        //create BILLING request
        Customer customer = generator.generateCustomer();
        DepartmentType requestType = DepartmentType.BILLING;
        Request request = new Request(customer, "testRequest", requestType);
        //create IT department
        Department department = prepareDepartment(DepartmentType.BILLING);
        //create employees
        Employee employeeOne = generator.generateCommonEmployeeWithoutDepartment();
        employeeOne.setId(1);
        Employee employeeTwo = generator.generateCommonEmployeeWithoutDepartment();
        employeeTwo.setId(2);
        //assign employees to the BILLING department
        department.addEmployee(employeeOne);
        department.addEmployee(employeeTwo);

        //assign the request to the employee
        requestService.assignRequest(request, department.getDepartmentHead(), employeeOne);

        //employee can't resolve request not assigned to him
        Assertions.assertThrows(UnauthorizedException.class,
                () -> requestService.resolveRequest(request, employeeTwo));
    }


    @Test
    public void correctResolveRequest() {
        //create IT request
        Customer customer = generator.generateCustomer();
        DepartmentType requestType = DepartmentType.IT;
        Request request = new Request(customer, "testRequest", requestType);
        //create IT department
        Department department = prepareDepartment(DepartmentType.IT);
        //create employee and assign it to different department
        Employee employee = generator.generateCommonEmployeeWithoutDepartment();
        //assign employee to the IT department
        department.addEmployee(employee);

        //try to assign employee the request
        requestService.assignRequest(request, department.getDepartmentHead(), employee);



        //try to resolve the request as employee
        requestService.resolveRequest(request, employee);

        //assert that update was called 2 times (assign, resolve)
        verify(requestDao, times(2)).update(request);
        //assert the state or request is resolved
        Assertions.assertEquals(RequestState.RESOLVED, request.getState());
    }

    //----------- Confirm REQUEST TEST ------------------------------
    @Test
    public void confirmNotFromCustomerWhoCreatedRequest(){
        //create request
        Customer customer = generator.generateCustomer();
        customer.setId(1);
        DepartmentType requestType = DepartmentType.BILLING;
        Request request = new Request(customer, "testRequest", requestType);
        //resolve request
        request.setState(RequestState.RESOLVED);
        //create another customer
        Customer anotherCustomer = generator.generateCustomer();
        anotherCustomer.setId(2);

        //other customers can't confirm request not created by them
        Assertions.assertThrows(UnauthorizedException.class,
                () -> requestService.confirmRequest(request, anotherCustomer));
    }

    @Test
    public void correctConfirmRequest() {
        //create request
        Customer customer = generator.generateCustomer();
        DepartmentType requestType = DepartmentType.BILLING;
        Request request = new Request(customer, "testRequest", requestType);
        request.setState(RequestState.RESOLVED);
        requestService.confirmRequest(request, customer);

        //assert the state or request is resolved
        verify(requestDao).update(request);
        Assertions.assertEquals(RequestState.CONFIRMED, request.getState());
    }

    //----------- CLOSE REQUEST TEST -------------------------------
    @Test
    public void closeRequestNotApprovedByCustomer() {
        //create request
        Customer customer = generator.generateCustomer();
        DepartmentType requestType = DepartmentType.BILLING;
        Request request = new Request(customer, "testRequest", requestType);
        request.setState(RequestState.RESOLVED);
        //create employee
        Employee manager = generator.generateManager();
        //request not confirmed
        Assertions.assertNotSame(request.getState(), RequestState.CONFIRMED);

        //request not approved by customer yet
        Assertions.assertThrows(InvalidStateException.class,
                () -> requestService.closeRequest(request, manager));
    }

    @Test
    public void correctCloseRequest() {
        //create request
        Customer customer = generator.generateCustomer();
        DepartmentType requestType = DepartmentType.BILLING;
        Request request = new Request(customer, "testRequest", requestType);
        //create employee
        Employee manager = generator.generateManager();
        //confirm resolution
        request.setState(RequestState.CONFIRMED);

        //close request
        requestService.closeRequest(request, manager);

        //assert update was called
        verify(requestDao).update(request);
        //assert that request is marked resolved
        Assertions.assertEquals(RequestState.CLOSED, request.getState());
    }

}
