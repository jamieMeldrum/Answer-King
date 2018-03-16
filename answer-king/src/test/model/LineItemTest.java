package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import matcher.BigDecimalMatcher;
import org.junit.Test;

import java.math.BigDecimal;
import javax.validation.ConstraintViolationException;

public class LineItemTest extends AbstractModelTest {

    @Test
    public void testGetPricePerLineItem() {
        assertPricePerLineItem(4, new BigDecimal("20.00"), new BigDecimal("80.00")); //Positive result
        assertPricePerLineItem(4, new BigDecimal("-20.00"), new BigDecimal("-80.00")); //Negative value
        assertPricePerLineItem(4, new BigDecimal("0.00"), new BigDecimal("0.00")); //Zero value
    }

    @Test
    public void testSaveLineItemUninitted() {
        LineItem lineItem = new LineItem();
        testLineItemFailureDueToConstraintValidation(lineItem, LineItem.VALIDATION_MESSAGE_NULL_PRICE);
    }

    @Test
    public void testSaveLineItemPriceNegative() {
        LineItem lineItem = constructLineItemForTest(1, new BigDecimal("-1.00"));
        testLineItemFailureDueToConstraintValidation(lineItem, LineItem.VALIDATION_MESSAGE_MINIMUM_VALUE_PRICE);
    }

    @Test
    public void testSaveLineItemPriceTooLarge() {
        LineItem lineItem = constructLineItemForTest(1, new BigDecimal("100000.00"));
        testLineItemFailureDueToConstraintValidation(lineItem, LineItem.VALIDATION_MESSAGE_TOO_LARGE_OR_PRECISE_PRICE);
    }

    @Test
    public void testSaveLineItemPriceTooManyDecimals() {
        LineItem lineItem = constructLineItemForTest(1, new BigDecimal("0.001"));
        testLineItemFailureDueToConstraintValidation(lineItem, LineItem.VALIDATION_MESSAGE_TOO_LARGE_OR_PRECISE_PRICE);
    }

    @Test
    public void testSaveLineItemPriceMinBoundaryCondition() {
        testSaveLineItem(new BigDecimal("0.00"), 1);
    }

    @Test
    public void testSaveLineItemPriceMaxBoundaryCondition() {
        testSaveLineItem(new BigDecimal("99999.99"), 1);
    }

    @Test
    public void testSaveLineItemQuantityNegative() {
        LineItem lineItem = constructLineItemForTest(-1, new BigDecimal("1.00"));
        testLineItemFailureDueToConstraintValidation(lineItem, LineItem.VALIDATION_MESSAGE_FIELD_MINIMUM_QUANTITY);
    }

    @Test
    public void testSaveLineItemQuantityZero() {
        LineItem lineItem = constructLineItemForTest(0, new BigDecimal("1.00"));
        testLineItemFailureDueToConstraintValidation(lineItem, LineItem.VALIDATION_MESSAGE_FIELD_MINIMUM_QUANTITY);
    }

    @Test
    public void testSaveLineItemQuantityMinBoundaryCondition() {
        testSaveLineItem(new BigDecimal("0.00"), 1);
    }

    private void assertPricePerLineItem(long quantity, BigDecimal price, BigDecimal expected) {
        LineItem lineItem = constructLineItemForTest(quantity, price);
        BigDecimal pricePerLineItem = lineItem.getPricePerLineItem();
        assertThat(expected, new BigDecimalMatcher(pricePerLineItem));
    }

    private LineItem constructLineItemForTest(long quantity, BigDecimal price) {
        LineItem lineItem = new LineItem();
        lineItem.setQuantity(quantity);
        lineItem.setPrice(price);
        return lineItem;
    }

    private void testLineItemFailureDueToConstraintValidation(LineItem lineItem, String errorMessage) {
        exception.expect(ConstraintViolationException.class);
        exception.expectMessage(errorMessage);
        lineItemRepository.save(lineItem);
    }

    private void testSaveLineItem(BigDecimal price, long quantity) {
        Item item = new Item("Test Item", price);
        item = itemRepository.save(item);

        Order order = new Order();
        order = orderRepository.save(order);

        LineItem lineItem = new LineItem(item, order, quantity);

        lineItemRepository.save(lineItem);
        LineItem lineItemRetrieved = lineItemRepository.findOne(lineItem.getId());

        assertThat(price, new BigDecimalMatcher(lineItemRetrieved.getPrice()));
        assertEquals(quantity, lineItemRetrieved.getQuantity());
    }
}
