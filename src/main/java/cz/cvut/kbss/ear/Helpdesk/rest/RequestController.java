package cz.cvut.kbss.ear.Helpdesk.rest;

import cz.cvut.kbss.ear.Helpdesk.dao.criteria.RequestCriteria;
import cz.cvut.kbss.ear.Helpdesk.dto.CreateCommentDTO;
import cz.cvut.kbss.ear.Helpdesk.dto.CreateRequestDTO;
import cz.cvut.kbss.ear.Helpdesk.model.*;
import cz.cvut.kbss.ear.Helpdesk.rest.util.RestUtils;
import cz.cvut.kbss.ear.Helpdesk.security.SecurityUtils;
import cz.cvut.kbss.ear.Helpdesk.security.model.UserDetails;
import cz.cvut.kbss.ear.Helpdesk.service.CommentService;
import cz.cvut.kbss.ear.Helpdesk.service.EmployeeService;
import cz.cvut.kbss.ear.Helpdesk.service.RequestService;
import cz.cvut.kbss.ear.Helpdesk.service.TrackedTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/request")
public class RequestController {

    private final RequestService requestService;

    private final EmployeeService employeeService;

    private final CommentService commentService;

    private final TrackedTimeService trackedTimeService;

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    @Autowired
    public RequestController(RequestService requestService, EmployeeService employeeService,
                             CommentService commentService, TrackedTimeService trackedTimeService) {
        this.requestService = requestService;
        this.employeeService = employeeService;
        this.commentService = commentService;
        this.trackedTimeService = trackedTimeService;
    }


    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_COMMON')")
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Request> getRequests() {
        List<Request> requests = requestService.findAll();
        logger.info("Received a request to get all requests. Number of requests found: {}", requests.size());
        return requests;
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping(value = "/opened", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Request> getOpenedRequests(){
        Customer customer = Objects.requireNonNull(SecurityUtils.getCurrentUserDetails()).getCustomer();
        List<Request> requests = requestService.findByCustomerOpened(customer);
        logger.info("Received a request to get opened requests from customer with ID {}", customer.getId());
        return requests;
    }

    @PreAuthorize("hasAnyRole('ROLE_COMMON', 'ROLE_MANAGER')")
    @GetMapping(value = "/{id}")
    public Request getRequest(@PathVariable Integer id){
        Request request = requestService.find(id);
        logger.info("Received a request to get request with ID {}", request.getId());
        return request;
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Request> createRequest(@RequestBody CreateRequestDTO createRequestDTO) {
        Customer customer = Objects.requireNonNull(SecurityUtils.getCurrentUserDetails()).getCustomer();
        logger.info("Creating a new request from customer with ID: {}", customer.getId());
        Request request = requestService.createRequest(customer, createRequestDTO.getText(), createRequestDTO.getType());
        logger.info("Created a new request with ID: {} and type: {}", request.getId(), request.getType());
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", request.getId());
        return new ResponseEntity<>(request, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_COMMON', 'ROLE_MANAGER')")
    @PostMapping(value = "/{id}/tracked-time", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<TrackedTime> createTrackedTimeOnRequest(@PathVariable Integer id, @RequestBody LocalDateTime endTime){
        Employee employee = Objects.requireNonNull(SecurityUtils.getCurrentUserDetails()).getEmployee();
        Request request = requestService.find(id);
        trackedTimeService.create(employee, request, LocalDateTime.now(), endTime);
        return request.getTrackedTimesList();
    }

    @PreAuthorize("hasAnyRole('ROLE_COMMON', 'ROLE_MANAGER')")
    @GetMapping(value = "/criteria")
    public List<Request> getCriteriaRequests(@RequestBody RequestCriteria criteria) {
        Employee employee = Objects.requireNonNull(SecurityUtils.getCurrentUserDetails()).getEmployee();
        criteria.setEmployee(employee);
        List<Request> requests = requestService.findByExactCriteria(criteria);
        logger.info("Received a request to get requests with specific criteria. Number of requests found: {}", requests.size());
        return requests;
    }


    @PreAuthorize("hasAnyRole('ROLE_COMMON', 'ROLE_MANAGER')")
    @GetMapping(value = "/{id}/tracked-time")
    public List<TrackedTime> getTrackedTimeOnRequest(@PathVariable Integer id){
        List<TrackedTime> trackedTimes = requestService.find(id).getTrackedTimesList();
        logger.info("Received a request to get tracked times on specific request with ID {}", id);
        return trackedTimes;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping(value = "/{id_req}/assignment/employee/{id_emp}")
    public Request assignRequest(@PathVariable Integer id_req, @PathVariable Integer id_emp){
        Employee manager = Objects.requireNonNull(SecurityUtils.getCurrentUserDetails()).getEmployee();
        Request request = requestService.find(id_req);
        Employee assignedTo = employeeService.find(id_emp);
        Request assignedRequest = requestService.assignRequest(request, manager, assignedTo);
        logger.info("Request with ID {} assigned to employee with ID {} by manager with ID {}", request.getId(), assignedTo.getId(), manager.getId());
        return assignedRequest;
    }

    @PreAuthorize("hasRole('ROLE_COMMON')")
    @PutMapping(value = "/{id_req}/resolution")
    public Request resolveRequest(@PathVariable Integer id_req){
        Employee employee = Objects.requireNonNull(SecurityUtils.getCurrentUserDetails()).getEmployee();
        Request request = requestService.resolveRequest(requestService.find(id_req), employee);
        logger.info("Request with ID {} resolved by employee with ID {}", request.getId(), employee.getId());
        return request;
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PutMapping(value = "/{id_req}/confirmation")
    public Request confirmRequest(@PathVariable Integer id_req) {
        UserDetails customerDetails = Objects.requireNonNull(SecurityUtils.getCurrentUserDetails());
        Request request = requestService.confirmRequest(requestService.find(id_req), customerDetails.getCustomer());
        logger.info("Request with ID {} confirmed by customer with ID {}", request.getId(), customerDetails.getCustomer().getId());
        return request;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping(value = "/{id_req}/closure")
    public Request closeRequest(@PathVariable Integer id_req) {
        Employee employee = Objects.requireNonNull(SecurityUtils.getCurrentUserDetails()).getEmployee();
        Request request = requestService.closeRequest(requestService.find(id_req), employee);
        logger.info("Request with ID {} closed by employee {} with ID {}", request.getId(), employee.getRole(), employee.getId());
        return request;
    }

    @GetMapping(value = "/{id_req}/comment")
    @PreAuthorize("hasAnyRole('ROLE_COMMON', 'ROLE_MANAGER', 'ROLE_CUSTOMER')")
    public List<Comment> getCommentsForRequest(@PathVariable Integer id_req) {
        final Request request = requestService.find(id_req);
        logger.info("Received a request to get all comments on request with ID {}", request.getId());
        return request.getComments();
    }


    @PostMapping(value = "/{id_req}/comment", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_COMMON', 'ROLE_CUSTOMER', 'ROLE_MANAGER')")
    public ResponseEntity<Comment> addComment(@PathVariable Integer id_req, @RequestBody CreateCommentDTO commentDTO) {
        Request request = requestService.find(id_req);
        User author = Objects.requireNonNull(SecurityUtils.getCurrentUser());
        logger.info("Creating a comment on request with ID {}", request.getId());
        Comment comment = commentService.create(author, commentDTO.getText(), request);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri();
        return new ResponseEntity<>(comment, headers, HttpStatus.CREATED);
    }
}
