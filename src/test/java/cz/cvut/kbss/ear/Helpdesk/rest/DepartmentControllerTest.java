package cz.cvut.kbss.ear.Helpdesk.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cz.cvut.kbss.ear.Helpdesk.generator.Generator;
import cz.cvut.kbss.ear.Helpdesk.model.AbstractEntity;
import cz.cvut.kbss.ear.Helpdesk.model.Department;
import cz.cvut.kbss.ear.Helpdesk.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class DepartmentControllerTest extends BaseControllerTestRunner {

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    private final Generator generator = new Generator();

    @BeforeEach
    public void setUp(){
        super.setUp(departmentController);
    }

    @Test
    public void getAllReturnsAllDepartments() throws Exception {
        final List<Department> departments = IntStream.range(0, 5).mapToObj(i -> generator.getRandomDepartment()).toList();
        when(departmentService.getAll()).thenReturn(departments);
        final MvcResult mvcResult = mockMvc.perform(get("/department/all")).andReturn();
        final List<Department> result = readValue(mvcResult, new TypeReference<>() {
        });
        assertNotNull(result);
        assertEquals(departments.size(), result.size());
        for (int i = 0; i < departments.size(); i++) {
            assertEquals(departments.get(i).getId(), result.get(i).getId());
            assertEquals(departments.get(i).getDepartmentType(), result.get(i).getDepartmentType());
            assertEquals(departments.get(i).getEmployees().stream().map(AbstractEntity::getId).toList(),
                    result.get(i).getEmployees().stream().map(AbstractEntity::getId).toList());
            assertEquals(departments.get(i).getDepartmentHead().getId(), result.get(i).getDepartmentHead().getId());
        }
    }
}
