package answer.king.service;

public enum ServiceErrorMessage {
    VALIDATION_NO_ITEM_FOR_ID("No Item for id "),
    VALIDATION_NO_ORDER_FOR_ID("No Order for id "),
    VALIDATION_ORDER_ALREADY_PAID("Order has already been paid for."),
    VALIDATION_PAYMENT_DOES_NOT_COVER_ORDER("Payment provided does not cover the cost of the Order."),
    VALIDATION_ITEM_PRICE_CHANGED("Item cannot be added to Order as the price has changed since it was first added."),
    VALIDATION_ADD_ITEM_QUANTITY_ZERO_OR_NEGATIVE("Item cannot be added to Order as quantity is 0 or negative."),
    VALIDATION_NO_LINE_ITEM("The Item does not currently exist on the Order.");

    private String message;

    private ServiceErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
