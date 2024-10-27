package cz.cvut.kbss.ear.Helpdesk.service;

import cz.cvut.kbss.ear.Helpdesk.dao.EmployeeDao;
import cz.cvut.kbss.ear.Helpdesk.dao.RequestDao;
import cz.cvut.kbss.ear.Helpdesk.exception.MyException;
import cz.cvut.kbss.ear.Helpdesk.exception.UnauthorizedException;
import cz.cvut.kbss.ear.Helpdesk.model.Employee;
import cz.cvut.kbss.ear.Helpdesk.model.Request;
import cz.cvut.kbss.ear.Helpdesk.model.TrackedTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

public class TrackedTimeServiceTest {

    @InjectMocks
    private TrackedTimeService trackedTimeService;

    @Mock
    private EmployeeDao employeeDao;

    @Mock
    private RequestDao requestDao;

    private AutoCloseable closeable;

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

    //----------- CREATE TRACKED TIME TEST -----------------
    @Test
    public void correctCreateTrackedTime() {
        Request req = new Request();
        Employee emp = new Employee();
        req.setEmployee(emp);

        LocalDateTime start = LocalDateTime.now().minusYears(1).minusDays(1);
        LocalDateTime end = LocalDateTime.now().minusYears(1);


        TrackedTime tt =  trackedTimeService.create(emp, req, start, end);

        verify(requestDao).persistTrackedTime(tt);
        verify(requestDao).update(req);

        Assertions.assertEquals(tt.getEmployee(), emp);
        Assertions.assertEquals(tt.getRequest(), req);
        Assertions.assertEquals(tt.getStart(), start);
        Assertions.assertEquals(tt.getEnd(), end);
    }

    @Test
    public void createTrackedTimeWithStartTimeAfterEnd() {
        Request req = new Request();
        Employee emp = new Employee();
        req.setEmployee(emp);

        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = start.minusDays(1);

        Assertions.assertThrows(MyException.class,
                () -> trackedTimeService.create(emp, req, start, end));
    }

    @Test
    public void createTrackedTimeWithEndTimeInTheFuture() {
        Request req = new Request();
        Employee emp = new Employee();
        req.setEmployee(emp);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(2);

        Assertions.assertThrows(MyException.class,
                () -> trackedTimeService.create(emp, req, start, end));
    }

    @Test
    public void createTrackedTimeWithNotAssignedEmployee(){
        Request req = new Request();
        Employee emp = new Employee();
        req.setEmployee(emp);

        Employee anotherEmp = new Employee();

        LocalDateTime start = LocalDateTime.now().minusYears(1).minusDays(1);
        LocalDateTime end = LocalDateTime.now().minusYears(1);

        //create trackedTime with another employee
        Assertions.assertThrows(UnauthorizedException.class,
                () -> trackedTimeService.create(anotherEmp, req, start, end));

    }

}
