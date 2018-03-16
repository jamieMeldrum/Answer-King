package service;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;

import answer.king.error.InputValidationException;
import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import answer.king.service.ServiceErrorMessage;
import matcher.BigDecimalMatcher;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ItemServiceTest extends AbstractServiceTest {

    @Test
    public void testGetAll() {
        //Create and save a couple of Items and keep track of the ids for them so we can check we get them back later
        Item item1 = createAndSaveItem("Test Item 1", new BigDecimal("1.00"));
        Item item2 = createAndSaveItem("Test Item 2", new BigDecimal("2.00"));

        //Get all the items and filter out any that are not for the ids we have just added. This doesn't quite prove that
        //getAll actually gets all but it does prove it can get a list of results that matches to the results we expect.
        List<Item> allItems = itemService.getAll();
        List<Long> itemIds = allItems.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        assertThat(itemIds, hasItems(item1.getId(), item2.getId()));
    }

    @Test
    public void testCreateItem() {
        String name = "Test Item";
        BigDecimal price = new BigDecimal("12.04");
        Item item = createAndSaveItem(name, price);

        assertNotNull("Item did not save correctly.", item);
        assertEquals(item.getName(), item.getName());
        assertEquals(item.getPrice(), item.getPrice());
    }

    @Test
    public void testAmendPrice() {
        BigDecimal originalPrice = new BigDecimal("12.56");
        Item originalItem = createAndSaveItem("Test Item", originalPrice);
        Long id = originalItem.getId();

        BigDecimal newPrice = new BigDecimal("45.56");
        Item item = itemService.amendPrice(id, newPrice);

        BigDecimal priceOnRetrievedItem = item.getPrice();
        assertThat(newPrice, new BigDecimalMatcher(priceOnRetrievedItem));
    }

    @Test
    public void testAmendPriceNoItem() {
        exception.expect(InputValidationException.class);
        exception.expectMessage(ServiceErrorMessage.VALIDATION_NO_ITEM_FOR_ID.getMessage() + Long.MAX_VALUE);

        itemService.amendPrice(Long.MAX_VALUE, new BigDecimal("45.56"));
    }
}