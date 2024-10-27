package cz.cvut.kbss.ear.Helpdesk.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NamedQuery(name = "Comment.findByCustomer", query = "SELECT c FROM Comment c WHERE c.author = :author")
public class Comment extends AbstractEntity{

    @Basic(optional = false)
    @Column(nullable = false)
    private String text;

    @ManyToOne
    private User author;

    @Basic(optional = false)
    private LocalDateTime dateCreated;

}
