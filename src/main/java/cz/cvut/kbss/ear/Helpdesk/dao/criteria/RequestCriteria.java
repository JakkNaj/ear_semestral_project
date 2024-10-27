package cz.cvut.kbss.ear.Helpdesk.dao.criteria;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.cvut.kbss.ear.Helpdesk.model.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class RequestCriteria {
    private DepartmentType type;
    private RequestPriority priority;
    private RequestState state;
    private String customerEmail;
    private Employee employee;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime dateCreated;
}

