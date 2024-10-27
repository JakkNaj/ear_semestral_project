package cz.cvut.kbss.ear.Helpdesk.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.cvut.kbss.ear.Helpdesk.environment.Environment;
import cz.cvut.kbss.ear.Helpdesk.rest.handler.RestExceptionHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static cz.cvut.kbss.ear.Helpdesk.environment.Environment.createDefaultMessageConverter;
import static cz.cvut.kbss.ear.Helpdesk.environment.Environment.createStringEncodingMessageConverter;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseControllerTestRunner {

    ObjectMapper objectMapper;

    MockMvc mockMvc;

    public void setUp(Object controller) {
        this.objectMapper = Environment.getObjectMapper();
        // Standalone setup initializes just the specified controller, without any security or services
        // We also provide the exception handler and message converters, so that error and data handling works
        // the same as usual

        //new JavaTimeModule for serializing LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        // Register the JavaTimeModule here
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new RestExceptionHandler())
                .setMessageConverters(createDefaultMessageConverter(),
                        createStringEncodingMessageConverter())
                .build();
    }

    String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    <T> T readValue(MvcResult result, Class<T> targetType) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), targetType);
    }

    <T> T readValue(MvcResult result, TypeReference<T> targetType) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), targetType);
    }

    void verifyLocationEquals(String expectedPath, MvcResult result) {
        final String locationHeader = result.getResponse().getHeader(HttpHeaders.LOCATION);
        assertEquals("http://localhost:8080" + expectedPath, locationHeader);
    }
}
