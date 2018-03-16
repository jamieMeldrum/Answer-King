package service;

import static org.junit.Assert.assertEquals;

import answer.king.Application;
import answer.king.model.Item;
import answer.king.model.Order;
import answer.king.repo.ItemRepository;
import answer.king.repo.LineItemRepository;
import answer.king.repo.OrderRepository;
import answer.king.repo.ReceiptRepository;
import answer.king.service.ItemService;
import answer.king.service.OrderService;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Ignore
public class AbstractServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    protected ItemService itemService;

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected ReceiptRepository receiptRepository;

    @Autowired
    protected LineItemRepository lineItemRepository;

    protected Item createAndSaveItem(String name, BigDecimal price) {
        Item item = new Item(name, price);
        itemService.save(item);
        return itemRepository.findOne(item.getId());
    }
    protected Order createAndSaveOrder() {
        return createAndSaveOrder(new Order());
    }
    protected Order createAndSaveOrder(Order order) {
        order = orderService.save(order);
        return orderRepository.findOne(order.getId());
    }
}