package cz.cvut.kbss.ear.Helpdesk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "Request.findByType", query = "SELECT req FROM Request req WHERE req.type = :type"),
        @NamedQuery(name = "Request.findByPriority", query = "SELECT req FROM Request req WHERE req.priority = :priority"),
        @NamedQuery(name = "Request.findByCustomer", query = "SELECT req FROM Request req WHERE req.customer = :customer"),
        @NamedQuery(name = "Request.findByState", query = "SELECT req FROM Request req WHERE req.state = :state"),
        @NamedQuery(name = "Request.findByCustomerOpened", query = "SELECT req FROM Request req WHERE req.customer = :customer AND req.state != 'CLOSED'"),
})
@Setter
@Getter
public class Request extends AbstractEntity{
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private DepartmentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private RequestPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private RequestState state;

    @Basic(optional = false)
    @Column(nullable = false)
    private String text;

    @Basic(optional = false)
    private LocalDateTime dateCreated;

    @Basic
    private LocalDateTime deadline;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Employee employee;

    @JsonIgnore
    @OneToMany(mappedBy = "request")
    private List<TrackedTime> trackedTimesList;

    @OrderBy("dateCreated ASC")
    @OneToMany
    private List<Comment> comments;

    public Request(Customer creator, String text, DepartmentType type){
        this.customer = creator;
        this.text = text;
        this.type = type;
        this.state = RequestState.NEW;
        this.priority = RequestPriority.LOW;
        trackedTimesList = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public Request() {

    }

    public void addTrackedTime(TrackedTime tt){
        if (trackedTimesList == null)
            this.trackedTimesList = new ArrayList<>();
        trackedTimesList.add(tt);
    }

    public void addComment(Comment comment){
        if (comments == null)
            this.comments = new ArrayList<>();
        comments.add(comment);
    }

}
