package cz.cvut.kbss.ear.Helpdesk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NamedQueries({
        @NamedQuery(name = "TrackedTime.findByLocalDate",
                query = "SELECT tt FROM TrackedTime tt WHERE CAST(tt.start AS DATE) = CAST(:date AS DATE)"),
        @NamedQuery(name = "TrackedTime.findByRequestAndEmployee",
                query = "SELECT tt FROM TrackedTime tt WHERE tt.request = :request AND tt.employee = :employee")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrackedTime extends AbstractEntity {

    @Basic(optional = false)
    @Column(name = "start_date",nullable = false)
    private LocalDateTime start;

    @Basic
    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne
    private Request request;

    @ManyToOne
    private Employee employee;
}
