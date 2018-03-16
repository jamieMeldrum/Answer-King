package service;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;

import answer.king.error.InputValidationException;
import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.service.ServiceErrorMessage;
import matcher.BigDecimalMatcher;
import matcher.LongMatcher;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;

public class OrderServiceTest extends AbstractServiceTest {

    @Test
    public void testGetAll() {
        Order order1 = createAndSaveOrder();
        Order order2 = createAndSaveOrder();

        //Get all the orders and filter out any that are not for the ids we have just added. This doesn't quite prove that
        //getAll actually gets all but it does prove it can get a list of results that matches to the results we expect.
        List<Order> allOrders = orderService.getAll();
        List<Long> orderIds = allOrders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        assertThat(orderIds, hasItems(order1.getId(), order2.getId()));
    }

    @Test
    public void testCreateOrder() {
        Order orderToCreate = new Order();
        orderToCreate.setPaid(true);
        Long orderId = createAndSaveOrder(orderToCreate).getId();

        Order orderRetrieved = orderRepository.findOne(orderId);
        assertNotNull("Order did not save correctly.", orderRetrieved);
        assertTrue(orderRetrieved.getPaid());
    }

    @Test
    public void testAddingLineItemToOrderAndChangingItemPrice() {
        Order order = createAndSaveOrder();

        BigDecimal originalPrice = new BigDecimal("1.00");
        Long quantity = new Long(2);
        Item item = createItemAndAddToOrder("Test Item", originalPrice, quantity, order);

        //Change the price on the item now to prove that the line item is not affected
        item.setPrice(new BigDecimal("2.00"));
        itemRepository.save(item);

        List<LineItem> lineItemsRetrieved = lineItemRepository.findByOrder(order);
        assertTrue(lineItemsRetrieved.size() == 1);

        LineItem lineItem = lineItemsRetrieved.get(0);
        assertThat(originalPrice, new BigDecimalMatcher(lineItem.getPrice()));
        assertEquals(quantity.longValue(), lineItem.getQuantity());
    }

    @Test
    public void testAddingLineItemToOrderThatAlreadyHasLineItem() {
        Order order = createAndSaveOrder();
        Item item = createItemAndAddToOrder("Test Item", new BigDecimal("1.00"), new Long(1), order);
        orderService.addItem(order.getId(), item.getId(), new Long(4));

        List<LineItem> lineItemsRetrieved = lineItemRepository.findByOrder(order);
        assertTrue(lineItemsRetrieved.size() == 1);

        LineItem lineItem = lineItemsRetrieved.get(0);
        assertThat(lineItem.getQuantity(), new LongMatcher(new Long(5)));
    }

    @Test
    public void testAddItemToOrderNoItem() {
        Order order = createAndSaveOrder();
        Long orderId = order.getId();

        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_NO_ITEM_FOR_ID.getMessage() + Long.MAX_VALUE);

        orderService.addItem(orderId, Long.MAX_VALUE, new Long(1));
    }

    @Test
    public void testAddItemToOrderNoOrder() {
        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_NO_ORDER_FOR_ID.getMessage() + Long.MAX_VALUE);

        orderService.addItem(Long.MAX_VALUE, Long.MAX_VALUE, new Long(1));
    }

    @Test
    public void testAddItemToOrderAlreadyPaid() {
        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_ORDER_ALREADY_PAID.getMessage());

        Order order = createAndSaveOrder();
        order.setPaid(true);
        orderService.save(order);

        createItemAndAddToOrder("Test Item", new BigDecimal("1.00"), new Long(1), order);
    }

    @Test
    public void testAddingItemToOrderNegativeQuantity() {
        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_ADD_ITEM_QUANTITY_ZERO_OR_NEGATIVE.getMessage());

        Order order = createAndSaveOrder();
        createItemAndAddToOrder("Test Item", new BigDecimal("1.00"), new Long(-2), order);
    }

    @Test
    public void testSettingItemQuantity() {
        Order order = createAndSaveOrder();
        Item item = createItemAndAddToOrder("Test Item 1", new BigDecimal("1.00"), new Long(2), order);

        Long quantityToSet = new Long(1);
        orderService.setItemQuantity(order.getId(), item.getId(), quantityToSet);
        LineItem lineItem = lineItemRepository.findByOrderAndItem(order, item);
        assertEquals(quantityToSet.longValue(), lineItem.getQuantity());
    }

    @Test
    public void testSettingItemQuantityNoItem() {
        Order order = createAndSaveOrder();
        Long orderId = order.getId();

        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_NO_ITEM_FOR_ID.getMessage() + Long.MAX_VALUE);

        orderService.setItemQuantity(orderId, Long.MAX_VALUE, new Long(1));
    }

    @Test
    public void testSettingItemQuantityNoOrder() {
        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_NO_ORDER_FOR_ID.getMessage() + Long.MAX_VALUE);

        orderService.setItemQuantity(Long.MAX_VALUE, Long.MAX_VALUE, new Long(1));
    }

    @Test
    public void testSettingItemQuantityOrderAlreadyPaid() {
        Item item = createAndSaveItem("Test Item 1", new BigDecimal("1.00"));

        Order order = createAndSaveOrder();
        order.setPaid(true);
        orderService.save(order);

        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_ORDER_ALREADY_PAID.getMessage());

        orderService.setItemQuantity(order.getId(), item.getId(), new Long(1));
    }

    @Test
    public void testSettingItemQuantityNegativeQuantity() {
        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_ADD_ITEM_QUANTITY_ZERO_OR_NEGATIVE.getMessage());

        BigDecimal originalPrice = new BigDecimal("1.00");
        Item item = createAndSaveItem("Test Item", originalPrice);

        Order order = createAndSaveOrder();
        Long quantity = new Long(-2);
        orderService.setItemQuantity(order.getId(), item.getId(), quantity);
    }

    @Test
    public void testSettingItemQuantityNoLineItem() {
        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_NO_LINE_ITEM.getMessage());

        BigDecimal originalPrice = new BigDecimal("1.00");
        Item item = createAndSaveItem("Test Item", originalPrice);

        Order order = createAndSaveOrder();
        Long quantity = new Long(2);
        orderService.setItemQuantity(order.getId(), item.getId(), quantity);
    }

    @Test
    public void testSettingItemQuantityOnOrderThatAlreadyHasLineItemAndItemPriceHasChanged() {
        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_ITEM_PRICE_CHANGED.getMessage());

        Order order = createAndSaveOrder();

        Long quantity = new Long(2);
        Item item = createItemAndAddToOrder("Test Item 1", new BigDecimal("1.00"), quantity, order);

        item.setPrice(new BigDecimal("2.00"));
        itemRepository.save(item);

        orderService.setItemQuantity(order.getId(), item.getId(), quantity);
    }

    @Test
    public void testPayForOrder() {
        testPayForOrder(new BigDecimal("2.00"), new BigDecimal("1.00"));
    }

    @Test
    public void testPayForOrderBoundaryCondition() {
        testPayForOrder(new BigDecimal("1.00"), new BigDecimal("1.00"));
    }

    @Test
    public void testPayForOrderAlreadyPaid() {
        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_ORDER_ALREADY_PAID.getMessage());

        Order order = createAndSaveOrder();
        createItemAndAddToOrder("Test Item 1", new BigDecimal("1.00"), 1, order);

        Order orderSaved = orderRepository.findOne(order.getId());
        orderSaved.setPaid(true);
        orderService.save(orderSaved);

        BigDecimal payment = new BigDecimal("2.00");
        orderService.pay(order.getId(), payment);
    }

    @Test
    public void testPayForOrderInsufficientFunds() {
        Order order = createAndSaveOrder();
        createItemAndAddToOrder("Test Item 1", new BigDecimal("10.00"), 1, order);

        Throwable e = manuallyCatchPaymentFailureException(order, new BigDecimal("2.00"));
        assertTrue(e instanceof InputValidationException);
        assertEquals(ServiceErrorMessage.VALIDATION_PAYMENT_DOES_NOT_COVER_ORDER.getMessage(), e.getMessage());
        assertOrderAndReceiptOkAfterPayFailure(order);
    }

    @Test
    public void testPayForNoOrder() {
        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_NO_ORDER_FOR_ID.getMessage() + Long.MAX_VALUE);

        orderService.pay(Long.MAX_VALUE, new BigDecimal("12.04"));
    }

    @Test
    public void testPayForOrderReceiptSaveFailure() {
        Order order = createAndSaveOrder();
        createItemAndAddToOrder("Test Item 1", new BigDecimal("0.00"), 1, order);

        Throwable e = manuallyCatchPaymentFailureException(order, new BigDecimal("0.002"));
        assertTrue(e instanceof ConstraintViolationException);
        assertNotNull(e);
        assertOrderAndReceiptOkAfterPayFailure(order);
    }

    private Throwable manuallyCatchPaymentFailureException(Order order, BigDecimal payment) {
        Throwable e = null;
        try {
            orderService.pay(order.getId(), payment);
        }
        catch (Throwable eCaught) {
            e = eCaught;
        }
        return e;
    }

    private void assertOrderAndReceiptOkAfterPayFailure(Order order) {
        order = orderRepository.findOne(order.getId());
        assertFalse("Order has been paid for when it should not have been.", order.getPaid());

        Receipt receipt = receiptRepository.findReceiptByOrder(order);
        assertNull("Receipt has been saved when it should not have been.", receipt);
    }

    private Item createItemAndAddToOrder(String itemName, BigDecimal price, long quantity, Order order) {
        Item item = createAndSaveItem(itemName, price);
        orderService.addItem(order.getId(), item.getId(), quantity);
        return item;
    }

    private void testPayForOrder(BigDecimal payment, BigDecimal itemPrice) {
        Item item = createAndSaveItem("Test Item 1", itemPrice);
        Order order = createAndSaveOrder();
        assertFalse(order.getPaid());

        orderService.addItem(order.getId(), item.getId(), new Long(1));
        Receipt receipt = orderService.pay(order.getId(), payment);

        BigDecimal paymentOnReceipt = receipt.getPayment();
        assertThat(payment, new BigDecimalMatcher(paymentOnReceipt));

        //Confirm that the receipt actually saved, it is linked to the Order and it has the correct payment
        Receipt receiptRetrieved = receiptRepository.findReceiptByOrder(order);
        assertNotNull(receiptRetrieved);
        BigDecimal paymentOnRetrievedReceipt = receiptRetrieved.getPayment();
        assertThat(payment, new BigDecimalMatcher(paymentOnRetrievedReceipt));

        //Confirm that the Order is linked to the Receipt and has been marked as paid
        order = orderRepository.findOne(order.getId());
        assertTrue(order.getPaid());
        assertEquals(receipt.getId(), order.getReceipt().getId());
    }
}
