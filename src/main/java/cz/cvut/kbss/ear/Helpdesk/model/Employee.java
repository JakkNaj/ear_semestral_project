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
@NamedQueries({
    @NamedQuery(name = "Employee.findByEmail", query = "SELECT e FROM Employee e WHERE e.email = :email"),
    @NamedQuery(name = "Employee.findByRole", query = "SELECT e FROM Employee e WHERE e.role = :role")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee extends User{

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmployeeRole role;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "DEPT_ID")
    private Department department;
}
