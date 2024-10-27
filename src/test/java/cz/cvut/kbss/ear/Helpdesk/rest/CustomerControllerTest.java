package cz.cvut.kbss.ear.Helpdesk.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cz.cvut.kbss.ear.Helpdesk.generator.Generator;
import cz.cvut.kbss.ear.Helpdesk.model.Customer;
import cz.cvut.kbss.ear.Helpdesk.service.CustomerService;
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
public class CustomerControllerTest extends BaseControllerTestRunner{

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private final Generator generator = new Generator();

    @BeforeEach
    public void setUp(){
        super.setUp(customerController);
    }

    @Test
    public void getAllReturnsAllCustomers() throws Exception {
        final List<Customer> customers = IntStream.range(0, 5).mapToObj(i -> generator.generateCustomer()).toList();
        when(customerService.getCustomers()).thenReturn(customers);
        final MvcResult mvcResult = mockMvc.perform(get("/customer/all")).andReturn();
        final List<Customer> result = readValue(mvcResult, new TypeReference<>() {
        });
        assertNotNull(result);
        assertEquals(customers.size(), result.size());
        for (int i = 0; i < customers.size(); i++) {
            assertEquals(customers.get(i).getId(), result.get(i).getId());
            assertEquals(customers.get(i).getEmail(), result.get(i).getEmail());
            assertEquals(customers.get(i).getName(), result.get(i).getName());
            assertEquals(customers.get(i).getSurname(), result.get(i).getSurname());
        }
    }
}
