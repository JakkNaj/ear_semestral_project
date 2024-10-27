package cz.cvut.kbss.ear.Helpdesk.initializer;

import cz.cvut.kbss.ear.Helpdesk.dao.DepartmentDao;
import cz.cvut.kbss.ear.Helpdesk.dao.EmployeeDao;
import cz.cvut.kbss.ear.Helpdesk.dao.RequestDao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
//class used to clean bad entities in database
public class DatabaseCleaner implements CommandLineRunner {

    private final EmployeeDao employeeDao;

    private final DepartmentDao departmentDao;

    private final RequestDao requestDao;

    @Autowired
    public DatabaseCleaner(EmployeeDao employeeDao, DepartmentDao departmentDao, RequestDao requestDao) {
        this.employeeDao = employeeDao;
        this.departmentDao = departmentDao;
        this.requestDao = requestDao;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        //method to clear something
        //clearDepartmentsAndEmployees();
        //clearRequests();
    }

    @Transactional
    public void clearDepartmentsAndEmployees(){
        employeeDao.findAll().forEach(emp -> {
            emp.setDepartment(null);
            employeeDao.update(emp);
        });
        departmentDao.deleteAll();
        employeeDao.deleteAll();
    }

    public void clearRequests() {
        requestDao.deleteAll();
    }
}

