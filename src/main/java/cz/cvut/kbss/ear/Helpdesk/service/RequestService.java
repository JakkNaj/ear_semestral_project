package cz.cvut.kbss.ear.Helpdesk.service;

import cz.cvut.kbss.ear.Helpdesk.dao.CustomerDao;
import cz.cvut.kbss.ear.Helpdesk.dao.EmployeeDao;
import cz.cvut.kbss.ear.Helpdesk.dao.RequestDao;
import cz.cvut.kbss.ear.Helpdesk.dao.criteria.RequestCriteria;
import cz.cvut.kbss.ear.Helpdesk.exception.InvalidStateException;
import cz.cvut.kbss.ear.Helpdesk.exception.MyException;
import cz.cvut.kbss.ear.Helpdesk.exception.UnauthorizedException;
import cz.cvut.kbss.ear.Helpdesk.model.*;
import cz.cvut.kbss.ear.Helpdesk.rest.RequestController;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class RequestService {

    final RequestDao requestDao;

    final CustomerDao customerDao;

    final EmployeeDao employeeDao;

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    @Autowired
    public RequestService(RequestDao requestDao, CustomerDao customerDao, EmployeeDao employeeDao) {
        this.requestDao = requestDao;
        this.customerDao = customerDao;
        this.employeeDao = employeeDao;
    }

    @Transactional
    public Request find(Integer id) {
        return requestDao.find(id);
    }

    @Transactional
    public List<Request> findByType(DepartmentType type) {
        return requestDao.findByType(type);
    }

    @Transactional
    public List<Request> findAll() {
        return requestDao.findAll();
    }

    @Transactional
    public List<Request> findByPriority(RequestPriority priority) {
        return requestDao.findByPriority(priority);
    }

    @Transactional
    public List<Request> findByCustomer(Customer customer) {
        return requestDao.findByCustomer(customer);
    }

    @Transactional
    public List<Request> findUntilDeadline(LocalDateTime deadline) {
        return requestDao.findUntilDeadline(deadline);
    }

    @Transactional
    public List<Request> findByState(RequestState state) {
        return requestDao.findByState(state);
    }

    @Transactional
    public List<Request> findByCustomerOpened(Customer customer){
        return requestDao.findByCustomerOpened(customer);
    }

    @Transactional
    public List<Request> findByExactCriteria(RequestCriteria criteria) {
        return requestDao.findByExactCriteria(criteria);
    }

    @Transactional
    public Request createRequest(Customer customer, String text, DepartmentType type) {
        Request req = new Request(customer, text, type);
        req.setState(RequestState.NEW);
        req.setPriority(RequestPriority.LOW);
        req.setDateCreated(LocalDateTime.now());
        requestDao.persist(req);
        return req;
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Transactional
    public Request assignRequest(Request request, Employee assignee, Employee assignedTo) {
        if (request.getState() != RequestState.NEW && request.getState() != RequestState.ASSIGNED )
            throw new InvalidStateException("Cannot assign request that is in state resolved, confirmed or closed");
        //assignee has to be manager
        if (assignee.getRole() != EmployeeRole.MANAGER) {
            throw new UnauthorizedException("Only managers can assign requests");
        }
        if (assignedTo.getDepartment().getDepartmentType() == request.getType()) {
            //assign request
            request.setEmployee(assignedTo);
            request.setState(RequestState.ASSIGNED);
        } else {
            throw new UnauthorizedException("Request is of different type than employee department, can't assign it!");
        }
        requestDao.update(request);
        return request;
    }

    @Transactional
    public Request resolveRequest(Request request, Employee employee){
        if (request.getState() != RequestState.ASSIGNED)
            throw new InvalidStateException("Cannot resolve request that is not in state assigned");
        if (!Objects.equals(request.getEmployee().getId(), employee.getId())) {
            throw new UnauthorizedException("Not authorized to resolve request");
        }
        request.setState(RequestState.RESOLVED);
        requestDao.update(request);
        return request;
    }

    @Transactional
    public Request confirmRequest(Request request, Customer customer) {
        if (request.getState() != RequestState.RESOLVED)
            throw new InvalidStateException("Cannot confirm request that is not in state resolved");
        if (!Objects.equals(request.getCustomer().getId(), customer.getId())) {
            throw new UnauthorizedException("Not authorized to confirm request resolution");
        }
        request.setState(RequestState.CONFIRMED);
        return requestDao.update(request);
    }


    @Transactional
    public Request closeRequest(Request request, Employee employee) {
        if (request.getState() == RequestState.CONFIRMED) {
            request.setState(RequestState.CLOSED);
        } else {
            throw new InvalidStateException("Request can be closed only when in state confirmed");
        }
        return requestDao.update(request);
    }

}
