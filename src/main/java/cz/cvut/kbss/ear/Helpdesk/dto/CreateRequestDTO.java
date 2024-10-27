package cz.cvut.kbss.ear.Helpdesk.dto;

import cz.cvut.kbss.ear.Helpdesk.model.DepartmentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRequestDTO {
    private DepartmentType type;
    private String text;
}
