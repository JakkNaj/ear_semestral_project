package cz.cvut.kbss.ear.Helpdesk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Department.findByDepartmentType", query = "SELECT d FROM Department d WHERE d.departmentType = :departmentType"),
        @NamedQuery(name = "Department.findByDepartmentHead", query = "SELECT d FROM Department d WHERE d.departmentHead = :departmentHead"),
})

public class Department extends AbstractEntity{

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DepartmentType departmentType;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    @OneToOne
    @JoinColumn(name = "EMP_ID")
    private Employee departmentHead;

    public void addEmployee(Employee employee){
        if (employees == null)
            this.employees = new ArrayList<>();
        employees.add(employee);
        employee.setDepartment(this);
    }

    public void addEmployeeList(List<Employee> employeeList) {
        if (employees == null)
            this.employees = new ArrayList<>();
        employeeList.forEach(employee -> {
            employees.add(employee);
            employee.setDepartment(this);
        });
    }

    public void setDepartmentHead(Employee manager) {
        this.departmentHead = manager;
        manager.setDepartment(this);
    }

}
