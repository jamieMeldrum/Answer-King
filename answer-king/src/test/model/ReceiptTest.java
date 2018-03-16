package model;

import static org.junit.Assert.assertThat;

import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import answer.king.model.Receipt;
import matcher.BigDecimalMatcher;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintViolationException;

public class ReceiptTest extends AbstractModelTest {

    @Test
    public void testGetChange() {
        Order order = new Order();

        BigDecimal price1 = new BigDecimal("12.00");
        Item item1 = new Item("Item 1", price1);
        long quantity1 = 2;
        LineItem lineItem1 = new LineItem(item1, order, quantity1);

        BigDecimal price2 = new BigDecimal("33.00");
        Item item2 = new Item("Item 2", price2);
        long quantity2 = 3;
        LineItem lineItem2 = new LineItem(item2, order, quantity2);

        List<LineItem> lineItems = Arrays.asList(lineItem1, lineItem2);
        order.setLineItems(lineItems);

        Receipt receipt = new Receipt();
        receipt.setOrder(order);

        BigDecimal totalToPay = order.getTotalPrice();

        //Positive change
        BigDecimal payment = totalToPay.add(new BigDecimal("2.00"));
        receipt.setPayment(payment);
        Assert.assertEquals(receipt.getChange(), receipt.getPayment().subtract(totalToPay));

        //Negative change
        payment = totalToPay.subtract(new BigDecimal("2.00"));
        receipt.setPayment(payment);
        Assert.assertEquals(receipt.getChange(), receipt.getPayment().subtract(totalToPay));

        //Zero change
        payment = totalToPay;
        receipt.setPayment(payment);
        Assert.assertEquals(receipt.getChange(), receipt.getPayment().subtract(totalToPay));
    }

    @Test
    public void testSaveReceiptFailurePaymentNegative() {
        testReceiptFailureDueToPaymentValidation(new BigDecimal("-2.00"), Receipt.VALIDATION_MESSAGE_FIELD_MINIMUM_PAYMENT);
    }

    @Test
    public void testSaveReceiptFailurePaymentTooLarge() {
        testReceiptFailureDueToPaymentValidation(new BigDecimal("100000.00"), Receipt.VALIDATION_MESSAGE_FIELD_TOO_LARGE_OR_PRESICE_PAYMENT);
    }

    @Test
    public void testSaveReceiptFailurePaymentTooManyDecimals() {
        testReceiptFailureDueToPaymentValidation(new BigDecimal("0.002"), Receipt.VALIDATION_MESSAGE_FIELD_TOO_LARGE_OR_PRESICE_PAYMENT);
    }

    @Test
    public void testSaveReceiptFailurePaymentMinBoundaryCondition() {
        BigDecimal payment = new BigDecimal("0.00");
        testReceiptSave(payment);
    }

    @Test
    public void testSaveReceiptFailurePaymentMaxBoundaryCondition() {
        BigDecimal payment = new BigDecimal("99999.99");
        testReceiptSave(payment);
    }

    private void testReceiptFailureDueToPaymentValidation(BigDecimal payment, String errorMessage) {
        exception.expect(ConstraintViolationException.class);
        exception.expectMessage(errorMessage);

        Receipt receipt = new Receipt();
        receipt.setPayment(payment);
        receiptRepository.save(receipt);
    }

    private void testReceiptSave(BigDecimal payment) {
        Order order = new Order();
        order = orderRepository.save(order);

        Receipt receipt = new Receipt();
        receipt.setPayment(payment);
        receipt.setOrder(order);

        receiptRepository.save(receipt);
        Receipt receiptRetrieved = receiptRepository.findOne(receipt.getId());

        assertThat(payment, new BigDecimalMatcher(receiptRetrieved.getPayment()));
    }
}