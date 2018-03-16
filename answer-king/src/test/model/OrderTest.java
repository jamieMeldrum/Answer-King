package model;

import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class OrderTest extends AbstractModelTest {

    @Test
    public void testGetTotalPrice() {
        Order order = new Order();

        BigDecimal price1 = new BigDecimal("12.00");
        Item item1 = new Item("Item 1", price1);
        long quantity1 = 2;
        LineItem lineItem1 = new LineItem(item1, order, quantity1);

        BigDecimal price2 = new BigDecimal("33.00");
        Item item2 = new Item("Item 2", price2);
        long quantity2 = 3;
        LineItem lineItem2 = new LineItem(item2,order,  quantity2);

        List<LineItem> lineItems = Arrays.asList(lineItem1, lineItem2);
        order.setLineItems(lineItems);

        BigDecimal totalToPayExpected = (price1.multiply(new BigDecimal(quantity1))).add((price2.multiply(new BigDecimal(quantity2))));
        BigDecimal totalToPayActual = order.getTotalPrice();

        Assert.assertEquals(totalToPayExpected, totalToPayActual);
    }

    @Test
    public void testGetTotalPriceNullLineItems() {
        Order order = new Order();
        BigDecimal totalToPayActual = order.getTotalPrice();
        Assert.assertEquals(BigDecimal.ZERO, totalToPayActual);
    }

    @Test
    public void testGetTotalPriceNoLineItems() {
        Order order = new Order();
        order.setLineItems(Arrays.asList());
        BigDecimal totalToPayActual = order.getTotalPrice();
        Assert.assertEquals(BigDecimal.ZERO, totalToPayActual);
    }
}
