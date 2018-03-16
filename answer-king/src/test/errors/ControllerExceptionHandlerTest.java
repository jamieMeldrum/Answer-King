package errors;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import answer.king.Application;
import answer.king.error.ControllerExceptionHandler;
import answer.king.model.Item;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

/**
 * Methods to test that the server's exception handler works as expected. Using the OrderController as the main sample
 * since I can test for each type of exception using that very simply.
 *
 * RUNTIME EXCEPTION - this has not been tested as it is a catch all. We should not really be in a case where we have
 * unexpected runtime exceptions so it doesn't make sense for me to create a code path that allows this to happen.
 *
 * I should still test one exception for each other controller to make sure the handler is actually being used.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ControllerExceptionHandlerTest {

    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        createData();
    }
    private void createData() throws Exception {
        //Makes sure that one order order exists and since it is the first we know it will have id 1.
        MockHttpServletRequestBuilder builder = post("/order");
        mockMvc.perform(builder);

        //Make sure an Item exists with a price
        Item item = new Item("Test Item 1", new BigDecimal("1.00"));
        Gson gson = new Gson();
        String json = gson.toJson(item);

        builder = post("/item")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder);

        //Make sure item is on the order so we can try to pay for it
        builder = put("/order/1/addItem/1").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder);
    }

    @Test
    public void testHttpMessageNotReadableException() throws Exception {

        MockHttpServletRequestBuilder builder = put("/order/1/pay")
                .content("Test")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(containsString(ControllerExceptionHandler.HTTP_MESSAGE_NOT_READABLE_EXCEPTION_MESSAGE)));
    }

    @Test
    public void testInputValidationException() throws Exception {
        BigDecimal payment = new BigDecimal("12.004");
        Gson gson = new Gson();
        String json = gson.toJson(payment);

        MockHttpServletRequestBuilder builder = put("/order/" + Long.MAX_VALUE + "/pay")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(containsString(ControllerExceptionHandler.INPUT_VALIDATION_EXCEPTION_MESSAGE)));
    }

    @Test
    public void testConstraintViolationException() throws Exception {

        BigDecimal payment = new BigDecimal("12.004");
        Gson gson = new Gson();
        String json = gson.toJson(payment);

        MockHttpServletRequestBuilder builder = put("/order/1/pay")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(containsString(ControllerExceptionHandler.CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE)));
    }

    @Test
    public void testItemControllerExceptionHandling() throws Exception {

        MockHttpServletRequestBuilder builder = post("/item")
                .content("Test")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(containsString(ControllerExceptionHandler.HTTP_MESSAGE_NOT_READABLE_EXCEPTION_MESSAGE)));
    }
}
