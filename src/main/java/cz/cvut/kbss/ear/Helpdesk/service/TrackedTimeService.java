package cz.cvut.kbss.ear.Helpdesk.service;


import cz.cvut.kbss.ear.Helpdesk.dao.RequestDao;
import cz.cvut.kbss.ear.Helpdesk.exception.MyException;
import cz.cvut.kbss.ear.Helpdesk.exception.UnauthorizedException;
import cz.cvut.kbss.ear.Helpdesk.model.Employee;
import cz.cvut.kbss.ear.Helpdesk.model.Request;
import cz.cvut.kbss.ear.Helpdesk.model.TrackedTime;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class TrackedTimeService {

    final RequestDao reqDao;

    @Autowired
    public TrackedTimeService(RequestDao reqDao) {
        this.reqDao = reqDao;
    }

    @Transactional
    public TrackedTime create(Employee employee, Request request, LocalDateTime start, LocalDateTime end) {
        Objects.requireNonNull(employee);
        Objects.requireNonNull(request);
        if (request.getEmployee() != employee) {
            throw new UnauthorizedException("only assigned employee allowed to track time on specific request");
        }
        if (start.isAfter(end)) {
            throw new MyException("Starting date bigger than ending date");
        }
        if (end.isAfter(LocalDateTime.now())){
            throw new MyException("Cannot track time in the future");
        }
        TrackedTime tt = new TrackedTime(start, end, request, employee);
        request.addTrackedTime(tt);
        reqDao.persistTrackedTime(tt);
        reqDao.update(request);
        return tt;
    }
}
