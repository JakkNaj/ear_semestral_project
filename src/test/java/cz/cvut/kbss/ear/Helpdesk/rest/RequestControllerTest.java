package cz.cvut.kbss.ear.Helpdesk.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.security.auth.UserPrincipal;
import cz.cvut.kbss.ear.Helpdesk.dto.CreateCommentDTO;
import cz.cvut.kbss.ear.Helpdesk.dto.CreateRequestDTO;
import cz.cvut.kbss.ear.Helpdesk.generator.Generator;
import cz.cvut.kbss.ear.Helpdesk.model.*;
import cz.cvut.kbss.ear.Helpdesk.security.model.UserDetails;
import cz.cvut.kbss.ear.Helpdesk.service.CommentService;
import cz.cvut.kbss.ear.Helpdesk.service.EmployeeService;
import cz.cvut.kbss.ear.Helpdesk.service.RequestService;
import cz.cvut.kbss.ear.Helpdesk.security.SecurityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@ExtendWith(MockitoExtension.class)
public class RequestControllerTest extends BaseControllerTestRunner{

    @Mock
    private RequestService requestServiceMock;

    @Mock
    private CommentService commentServiceMock;

    @Mock
    private EmployeeService employeeServiceMock;

    @InjectMocks
    private RequestController requestController;

    private final Generator generator = new Generator();

    @BeforeEach
    public void setUp(){
        super.setUp(requestController);
    }


    @Test
    public void getAllReturnsAllRequests() throws Exception {
        final List<Request> requests = IntStream.range(0, 5).mapToObj(i -> Generator.generateRequest()).toList();
        when(requestServiceMock.findAll()).thenReturn(requests);
        final MvcResult mvcResult = mockMvc.perform(get("/request/all")).andReturn();
        final List<Request> result = readValue(mvcResult, new TypeReference<List<Request>>() {
        });
        assertNotNull(result);
        assertEquals(requests.size(), result.size());
        for (int i = 0; i < requests.size(); i++) {
            assertEquals(requests.get(i).getType(), result.get(i).getType());
            assertEquals(requests.get(i).getPriority(), result.get(i).getPriority());
            assertEquals(requests.get(i).getText(), result.get(i).getText());
            assertEquals(requests.get(i).getDateCreated(), result.get(i).getDateCreated());
            assertEquals(requests.get(i).getDeadline(), result.get(i).getDeadline());
        }
    }

    @Test
    public void getRequestTest() throws Exception {
        final Request request = Generator.generateRequest();
        request.setId(1);
        when(requestServiceMock.find(1)).thenReturn(request);
        final MvcResult mvcResult = mockMvc.perform(get("/request/1")).andReturn();
        final Request result = readValue(mvcResult, new TypeReference<Request>() {
        });
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getType(), result.getType());
        assertEquals(request.getPriority(), result.getPriority());
        assertEquals(request.getText(), result.getText());
        assertEquals(request.getDateCreated(), result.getDateCreated());
        assertEquals(request.getDeadline(), result.getDeadline());
    }

    @Test
    public void createRequestTest() throws Exception {
        // CreateRequestDTO for the request body
        CreateRequestDTO createRequestDTO = new CreateRequestDTO();
        createRequestDTO.setText("Sample text");
        DepartmentType requestType = Generator.getRandomType();
        createRequestDTO.setType(requestType);

        Request createdRequest = Generator.generateRequest();
        when(requestServiceMock.createRequest(any(), anyString(), any())).thenReturn(createdRequest);

        // mock authentication for a customer
        UserDetails userDetails = createUserDetailsWithCustomer();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final MvcResult mvcResult = mockMvc.perform(post("/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(createRequestDTO))).andReturn();

        Request result = readValue(mvcResult, Request.class);
        verify(requestServiceMock).createRequest(any(), eq("Sample text"), eq(requestType));
        assertEquals(createdRequest.getId(), result.getId());
        assertEquals(createdRequest.getType(), result.getType());
        assertEquals(createdRequest.getPriority(), result.getPriority());
        assertEquals(createdRequest.getText(), result.getText());

    }

    // method to create logged in customer
    private UserDetails createUserDetailsWithCustomer() {
        Customer customer = generator.generateCustomer();
        return new UserDetails(customer, Collections.singleton(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
    }

    @Test
    public void getTrackedTimeOnRequestTest() throws Exception{
        final Request request = Generator.generateRequest();
        request.setId(1);
        int numberOfTrackedTimes = 4;
        addTrackedTimesToRequest(request, numberOfTrackedTimes);
        when(requestServiceMock.find(1)).thenReturn(request);
        final MvcResult mvcResult = mockMvc.perform(get("/request/1/tracked-time")).andReturn();
        final List<TrackedTime> result = readValue(mvcResult, new TypeReference<List<TrackedTime>>() {
        });
        for(int i = 0; i < numberOfTrackedTimes; i++) {
            assertEquals(request.getTrackedTimesList().get(i).getStart(), result.get(i).getStart());
            assertEquals(request.getTrackedTimesList().get(i).getEnd(), result.get(i).getEnd());
        }

    }

    private void addTrackedTimesToRequest(Request request, Integer numberOfTrackedTimes){
        for (int i = 0; i < numberOfTrackedTimes; i++){
            Employee employee = generator.generateCommonEmployeeWithoutDepartment();
            TrackedTime tt = new TrackedTime(LocalDateTime.now(), LocalDateTime.now().plusMinutes(Generator.randomInt()), request, employee);
            request.addTrackedTime(tt);
        }
    }

    private void prepareEnvironment(Request request, Employee manager, Employee employee){
        // choose type of request and department who solves it
        DepartmentType type = Generator.getRandomType();
        // prepare request
        request.setId(1);
        request.setType(type);
        request.setState(RequestState.NEW);
        lenient().when(requestServiceMock.find(1)).thenReturn(request);
        // prepare department with same type
        Department department = new Department();
        department.setDepartmentType(type);
        // in the department prepare Manager and Employee

        department.setDepartmentHead(manager);
        department.addEmployee(employee);

        employee.setId(1);
        lenient().when(employeeServiceMock.find(1)).thenReturn(employee);
    }

    @Test
    public void assignRequestTest() throws Exception {
        // prepare background
        Employee manager = generator.generateManager();
        Employee employee = generator.generateCommonEmployeeWithoutDepartment();

        prepareEnvironment(Generator.generateRequest(), manager, employee);

        // log in manager
        UserDetails userDetails = createUserDetailsWithManager(manager);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // prepare requestDaoMock assign request
        when(requestServiceMock.assignRequest(any(Request.class), any(Employee.class), any(Employee.class)))
                .thenAnswer(invocation -> {
                    Request assignedRequest = invocation.getArgument(0);
                    Employee assignedTo = invocation.getArgument(2);

                    assignedRequest.setEmployee(assignedTo);
                    assignedRequest.setState(RequestState.ASSIGNED);

                    return assignedRequest;
                });


        final MvcResult mvcResult = mockMvc.perform(put("/request/1/assignment/employee/1"))
                .andReturn();
        final Request result = readValue(mvcResult, new TypeReference<Request>() {
        });

        //check if the result request is assign to the employee
        assertEquals(result.getEmployee().getId(), employee.getId());
        assertEquals(result.getState(), RequestState.ASSIGNED);

    }

    private UserDetails createUserDetailsWithManager(Employee manager){
        return new UserDetails(manager, Collections.singleton(new SimpleGrantedAuthority("ROLE_MANAGER")));
    }

    @Test
    public void resolveRequestTest() throws Exception{
        // prepare background
        Employee manager = generator.generateManager();
        Employee employee = generator.generateCommonEmployeeWithoutDepartment();

        prepareEnvironment(Generator.generateRequest(), manager, employee);

        // log in as employee
        UserDetails userDetails = createUserDetailsWithCommonEmployee(employee);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // prepare requestDaoMock resolve request
        when(requestServiceMock.resolveRequest(any(Request.class), any(Employee.class)))
                .thenAnswer(invocation -> {
                    Request resolveRequest = invocation.getArgument(0);
                    resolveRequest.setState(RequestState.RESOLVED);
                    return resolveRequest;
                });

        final MvcResult mvcResult = mockMvc.perform(put("/request/1/resolution"))
                .andReturn();
        final Request result = readValue(mvcResult, new TypeReference<Request>() {
        });

        assertEquals(result.getState(), RequestState.RESOLVED);
    }

    private UserDetails createUserDetailsWithCommonEmployee(Employee employee){
        return new UserDetails(employee, Collections.singleton(new SimpleGrantedAuthority("ROLE_COMMON")));
    }

    @Test
    public void confirmRequestTest() throws Exception{
        // prepare background
        Employee manager = generator.generateManager();
        Employee employee = generator.generateCommonEmployeeWithoutDepartment();

        prepareEnvironment(Generator.generateRequest(), manager, employee);

        // mock authentication for a customer
        UserDetails userDetails = createUserDetailsWithCustomer();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // prepare requestDaoMock confirm request
        when(requestServiceMock.confirmRequest(any(Request.class), any(Customer.class)))
                .thenAnswer(invocation -> {
                    Request confirmRequest = invocation.getArgument(0);
                    confirmRequest.setState(RequestState.CONFIRMED);
                    return confirmRequest;
                });

        final MvcResult mvcResult = mockMvc.perform(put("/request/1/confirmation"))
                .andReturn();
        final Request result = readValue(mvcResult, new TypeReference<Request>() {
        });

        assertEquals(result.getState(), RequestState.CONFIRMED);
    }

    @Test
    public void closeRequestTest() throws Exception{
        // prepare background
        Employee manager = generator.generateManager();
        Employee employee = generator.generateCommonEmployeeWithoutDepartment();

        prepareEnvironment(Generator.generateRequest(), manager, employee);

        // log in manager
        UserDetails userDetails = createUserDetailsWithManager(manager);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // prepare requestDaoMock close request
        when(requestServiceMock.closeRequest(any(Request.class), any(Employee.class)))
                .thenAnswer(invocation -> {
                    Request closeRequest = invocation.getArgument(0);
                    closeRequest.setState(RequestState.CLOSED);
                    return closeRequest;
                });

        final MvcResult mvcResult = mockMvc.perform(put("/request/1/closure"))
                .andReturn();
        final Request result = readValue(mvcResult, new TypeReference<Request>() {
        });

        assertEquals(result.getState(), RequestState.CLOSED);
    }

    @Test
    public void addCommentTest() throws Exception {
        // CreateCommentDTO for the request body
        CreateCommentDTO createCommentDTO = new CreateCommentDTO();
        createCommentDTO.setText("Sample text");

        Request createdRequest = Generator.generateRequest();
        when(requestServiceMock.find(any())).thenReturn(createdRequest);

        Comment createdComment = new Comment();
        createdComment.setText(createCommentDTO.getText());
        createdRequest.addComment(createdComment);
        lenient().when(commentServiceMock.create(any(), anyString(), any())).thenReturn(createdComment);

        // mock authentication for a customer
        UserDetails userDetails = createUserDetailsWithCustomer();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final MvcResult mvcResult = mockMvc.perform(post("/request/1/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(createCommentDTO))).andReturn();

        Comment result = readValue(mvcResult, Comment.class);
        verify(commentServiceMock).create(any(), eq("Sample text"), any());
        assertEquals(createdComment.getId(), result.getId());
        assertEquals(createdComment.getAuthor(), result.getAuthor());
        assertEquals(createdComment.getText(), result.getText());
        assertEquals(createdRequest.getComments().size(), 1);
    }
}
