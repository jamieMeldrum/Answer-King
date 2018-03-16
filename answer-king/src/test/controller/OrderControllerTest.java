package controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import answer.king.Application;
import answer.king.controller.OrderController;
import answer.king.error.ControllerExceptionHandler;
import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.service.OrderService;
import com.google.gson.Gson;
import matcher.BigDecimalMatcher;
import matcher.LongMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class OrderControllerTest {

    protected MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Before
    public void init() {
        //Init the mock in the @Before for test isolation. This will reset the mocks for each test to ensure that
        //I have test isolation.
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    public void testGetAll() throws Exception {
        //Mock up some items that we will get when the itemService calls getAll. Don't need to test the service for this
        //explicitly as it is tested elsewhere
        Long id1 = new Long(1);
        Long id2 = new Long(2);

        Order order1 = new Order();
        order1.setId(id1);
        order1.setPaid(true);
        Order order2 = new Order();
        order2.setId(id2);
        order2.setPaid(false);

        List<Order> orders = Arrays.asList(order1, order2);
        when(orderService.getAll()).thenReturn(orders);

        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", new LongMatcher(id1)))
                .andExpect(jsonPath("$[0].paid", is(true)))
                .andExpect(jsonPath("$[1].id", new LongMatcher(id2)))
                .andExpect(jsonPath("$[1].paid", is(false)));

        verify(orderService, times(1)).getAll();
    }

    @Test
    public void testCreateOrder() throws Exception {
        Order order = new Order();
        Long orderId = new Long(1);
        order.setId(orderId);
        when(orderService.save(any(Order.class))).thenReturn(order);

        MockHttpServletRequestBuilder builder = post("/order")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", new LongMatcher(orderId)))
                .andExpect(jsonPath("$.paid", is(false)));

        verify(orderService, times(1)).save(any(Order.class));
    }

    @Test
    public void testAddItem() throws Exception {
        MockHttpServletRequestBuilder builder = put("/order/1/addItem/1/1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isOk());

        verify(orderService, times(1)).addItem(any(Long.class), any(Long.class), any(Long.class));
    }

    @Test
    public void testSetItemQuantity() throws Exception {
        MockHttpServletRequestBuilder builder = put("/order/1/setItemQuantity/1/1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isOk());

        verify(orderService, times(1)).setItemQuantity(any(Long.class), any(Long.class), any(Long.class));
    }

    @Test
    public void testPayForOrder() throws Exception {
        Order order = new Order();
        Long orderId = new Long(4);
        order.setId(orderId);

        BigDecimal price1 = new BigDecimal("12.00");
        Item item1 = new Item("Item 1", price1);
        BigDecimal price2 = new BigDecimal("33.00");
        Item item2 = new Item("Item 2", price2);

        LineItem lineItem1 = new LineItem(item1, order, 1);
        LineItem lineItem2 = new LineItem(item2, order, 1);

        List<LineItem> lineItems = Arrays.asList(lineItem1, lineItem2);
        order.setLineItems(lineItems);

        BigDecimal payment = new BigDecimal("50.00");
        Receipt receipt = new Receipt(order, payment);

        when(orderService.pay(any(Long.class), any(BigDecimal.class))).thenReturn(receipt);

        Gson gson = new Gson();
        String json = gson.toJson(payment);

        MockHttpServletRequestBuilder builder = put("/order/1/pay")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.orderId", new LongMatcher(orderId)))
                .andExpect(jsonPath("$.payment", new BigDecimalMatcher(receipt.getPayment())))
                .andExpect(jsonPath("$.change", new BigDecimalMatcher(receipt.getChange())));

        verify(orderService, times(1)).pay(any(Long.class), any(BigDecimal.class));
    }
}