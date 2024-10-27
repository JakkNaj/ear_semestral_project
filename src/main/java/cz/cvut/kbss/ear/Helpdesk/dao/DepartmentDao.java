package cz.cvut.kbss.ear.Helpdesk.dao;

import cz.cvut.kbss.ear.Helpdesk.model.Department;
import cz.cvut.kbss.ear.Helpdesk.model.DepartmentType;
import cz.cvut.kbss.ear.Helpdesk.model.Employee;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class DepartmentDao extends BaseDao<Department> {
    protected DepartmentDao() { super(Department.class); }

    public List<Employee> getEmloyees(Integer id) {
        Objects.requireNonNull(id);
        return em.find(Department.class ,id).getEmployees();
    }

    public List<Department> findByDepartmentType(DepartmentType type) {
        try {
            return em.createNamedQuery("Department.findByDepartmentType", Department.class)
                    .setParameter("departmentType", type)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Employee findByDepartmentHead(Employee employee) {
        try {
            return em.createNamedQuery("Department.findByDepartmentHead", Employee.class)
                    .setParameter("departmentHead", employee)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void deleteAll() {
        em.createNativeQuery("DELETE FROM department").executeUpdate();
    }

}
