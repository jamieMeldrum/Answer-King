package model;

import static org.junit.Assert.*;

import answer.king.model.Item;
import org.junit.Test;

import java.math.BigDecimal;
import javax.validation.ConstraintViolationException;

public class ItemTest extends AbstractModelTest {

    @Test
    public void testCreateItemNameNotSet() {
        Item item = new Item();
        item.setPrice(new BigDecimal("12.04"));
        assertSaveFailedForBadItemParameters(item, Item.VALIDATION_MESSAGE_EMPTY_FIELD_NAME);
    }

    @Test
    public void testCreateItemNameEmpty() {
        Item item = new Item("", new BigDecimal("12.04"));
        assertSaveFailedForBadItemParameters(item, Item.VALIDATION_MESSAGE_EMPTY_FIELD_NAME);
    }

    @Test
    public void testCreateItemNameTooLong() {
        //This should be 51 characters
        Item item = new Item("Lorem ipsum dolor sit amet, consectetuer adipiscin2", new BigDecimal("12.04"));
        assertSaveFailedForBadItemParameters(item, Item.VALIDATION_MESSAGE_FIELD_TOO_LONG_NAME);
    }

    @Test
    public void testCreateItemNameBoundaryMax() {
        //This should be 50 characters
        Item item = createAndSaveItem("Lorem ipsum dolor sit amet, consectetuer adipiscin", "12.04");
        assertItemIsCorrect(item);
    }

    @Test
    public void testCreateItemPriceNotSet() {
        Item item = new Item();
        item.setName("Test Item");
        assertSaveFailedForBadItemParameters(item, Item.VALIDATION_MESSAGE_FIELD_NULL_PRICE);
    }

    @Test
    public void testCreateItemPriceNegative() {
        Item item = new Item("Test Item", new BigDecimal("-0.01"));
        assertSaveFailedForBadItemParameters(item, Item.VALIDATION_MESSAGE_FIELD_MINIMUM_PRICE);
    }

    @Test
    public void testCreateItemPriceTooLarge() {
        Item item = new Item("Test Item", new BigDecimal("100000.00"));
        assertSaveFailedForBadItemParameters(item, Item.VALIDATION_MESSAGE_FIELD_TOO_LARGE_OR_PRECISE_PRICE);
    }

    @Test
    public void testCreateItemPriceTooManyDecimals() {
        Item item = new Item("Test Item", new BigDecimal("44.045"));
        assertSaveFailedForBadItemParameters(item, Item.VALIDATION_MESSAGE_FIELD_TOO_LARGE_OR_PRECISE_PRICE);
    }

    @Test
    public void testCreateItemPriceMinBoundaryCondition() {
        Item item = createAndSaveItem("Test Item", "0.00");
        assertItemIsCorrect(item);
    }

    @Test
    public void testCreateItemPriceMaxBoundaryCondition() {
        Item item = createAndSaveItem("Test Item", "99999.99");
        assertItemIsCorrect(item);
    }

    private void assertSaveFailedForBadItemParameters(Item item, String error) {
        ConstraintViolationException e = null;
        try {
            itemRepository.save(item);
        } catch (ConstraintViolationException eCaught) {
            e = eCaught;
        }
        assertNotNull(e);

        //Check that each expected error is contained in the error message
        String exceptionMessage = e.getMessage();
        assertTrue("Exception does not contain expected error:\n" + error, exceptionMessage.contains(error));

        Long itemId = item.getId();
        if (itemId != null) {
            Item itemRetrieved = itemRepository.findOne(item.getId());
            assertNull("Item saved when it should not have.", itemRetrieved);
        }
    }

    private Item createAndSaveItem(String s, String s2) {
        Item item = new Item(s, new BigDecimal(s2));
        itemRepository.save(item);
        return item;
    }

    private void assertItemIsCorrect(Item item) {
        assertNotNull("Item did not save correctly.", item);
        assertEquals(item.getName(), item.getName());
        assertEquals(item.getPrice(), item.getPrice());
    }
}
