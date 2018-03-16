package controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import answer.king.Application;
import answer.king.controller.ItemController;
import answer.king.model.Item;
import answer.king.service.ItemService;
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
public class ItemControllerTest {

    protected MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Before
    public void init() {
        //Init the mock in the @Before for test isolation. This will reset the mocks for each test to ensure that
        //I have test isolation.
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .build();
    }

    @Test
    public void testGetAll() throws Exception {

        //Mock up some items that we will get when the itemService calls getAll. Don't need to test the service for this
        //explicitly as it is tested elsewhere
        Long id1 = new Long(1);
        Long id2 = new Long(2);
        String name1 = "Test Item 1";
        String name2 = "Test Item 2";
        BigDecimal price1 = new BigDecimal("1.00");
        BigDecimal price2 = new BigDecimal("2.00");

        Item item1 = new Item(name1, price1);
        item1.setId(id1);
        Item item2 = new Item(name2, price2);
        item2.setId(id2);

        List<Item> items = Arrays.asList(item1, item2);
        when(itemService.getAll()).thenReturn(items);

        mockMvc.perform(get("/item"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", new LongMatcher(id1)))
                .andExpect(jsonPath("$[0].name", is(name1)))
                .andExpect(jsonPath("$[0].price", new BigDecimalMatcher(price1)))
                .andExpect(jsonPath("$[1].id", new LongMatcher(id2)))
                .andExpect(jsonPath("$[1].name", is(name2)))
                .andExpect(jsonPath("$[1].price", new BigDecimalMatcher(price2)));

        verify(itemService, times(1)).getAll();
    }

    @Test
    public void testCreateItem() throws Exception {

        Long id = new Long(1);
        String name = "Test Item 1";
        BigDecimal price = new BigDecimal("1.00");
        Item item = new Item(name, price);
        item.setId(id);

        when(itemService.save(any(Item.class))).thenReturn(item);

        Gson gson = new Gson();
        String json = gson.toJson(item);

        MockHttpServletRequestBuilder builder = post("/item")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", new LongMatcher(id)))
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.price", new BigDecimalMatcher(price)));

        verify(itemService, times(1)).save(any(Item.class));
    }

    @Test
    public void testCreateItemValidAnnotation() throws Exception {

        Item item = new Item("", new BigDecimal("1.001"));

        Gson gson = new Gson();
        String json = gson.toJson(item);

        MockHttpServletRequestBuilder builder = post("/item")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isBadRequest());

        verify(itemService, times(0)).save(any(Item.class));
    }

    @Test
    public void testAmendItem() throws Exception {
        String name = "Test Item";
        BigDecimal price = new BigDecimal("1.00");
        Long id = new Long(1);
        Item item = new Item(name, price);
        item.setId(id);

        Gson gson = new Gson();
        String json = gson.toJson(price);

        when(itemService.amendPrice(any(Long.class), any(BigDecimal.class))).thenReturn(item);

        MockHttpServletRequestBuilder builder = put("/item/" + id + "/amendPrice")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", new LongMatcher(id)))
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.price", new BigDecimalMatcher(price)));

        verify(itemService, times(1)).amendPrice(any(Long.class), any(BigDecimal.class));
    }
}