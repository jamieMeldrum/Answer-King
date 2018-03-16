package answer.king.service;

import java.math.BigDecimal;
import java.util.List;

import answer.king.error.InputValidationException;
import answer.king.model.LineItem;
import answer.king.repo.LineItemRepository;
import answer.king.repo.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import answer.king.model.Item;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.repo.ItemRepository;
import answer.king.repo.OrderRepository;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ReceiptRepository receiptRepository;

	@Autowired
	private LineItemRepository lineItemRepository;

	public List<Order> getAll() {
		return orderRepository.findAll();
	}

	public Order save(Order order) {
		orderRepository.save(order);
		return orderRepository.findOne(order.getId());
	}

	public void addItem(Long id, Long itemId, Long quantity) {
		Order order = orderRepository.findOne(id);
		Item item = itemRepository.findOne(itemId);

		doAmendOrderItemsValidation(id, itemId, quantity, order, item);
		linkItemToOrderAndSave(item, order, quantity);
	}

	public void setItemQuantity(Long id, Long itemId, Long quantity) {
		Order order = orderRepository.findOne(id);
		Item item = itemRepository.findOne(itemId);

		doAmendOrderItemsValidation(id, itemId, quantity, order, item);

		LineItem existingLineItem = lineItemRepository.findByOrderAndItem(order, item);
		if (existingLineItem == null) {
			throw new InputValidationException(ServiceErrorMessage.VALIDATION_NO_LINE_ITEM.getMessage());
		}
		updateAndSaveExistingLineItem(item, order, quantity, existingLineItem);
	}

	public Receipt pay(Long id, BigDecimal payment) {
		Order order = orderRepository.findOne(id);
		if (order == null) {
			throw new InputValidationException(ServiceErrorMessage.VALIDATION_NO_ORDER_FOR_ID.getMessage() + id);
		}

		if (order.getPaid()) {
			throw new InputValidationException(ServiceErrorMessage.VALIDATION_ORDER_ALREADY_PAID.getMessage());
		}

		Receipt receipt = new Receipt(order, payment);
		order.setReceipt(receipt);
		order.setPaid(true);

		BigDecimal change = receipt.getChange();
		if (change.signum() == -1) {
			throw new InputValidationException(ServiceErrorMessage.VALIDATION_PAYMENT_DOES_NOT_COVER_ORDER.getMessage());
		}

		orderRepository.save(order);
		return receiptRepository.findReceiptByOrder(order);
	}

	/**
	 * Helper methods
	 */
	private void linkItemToOrderAndSave(Item item, Order order, Long quantity) {
		LineItem existingLineItem = lineItemRepository.findByOrderAndItem(order, item);
		if (existingLineItem == null) {
			createAndSaveNewLineItem(item, order, quantity);
		}
		else {
			quantity = existingLineItem.getQuantity() + quantity;
			updateAndSaveExistingLineItem(item, order, quantity, existingLineItem);
		}
	}

	private void createAndSaveNewLineItem(Item item, Order order, Long quantity) {
		LineItem lineItem = new LineItem(item, order, quantity);
		order.getLineItems().add(lineItem);
		orderRepository.save(order);
	}

	private void updateAndSaveExistingLineItem(Item item, Order order, Long quantity, LineItem existingLineItem) {
		BigDecimal lineItemPrice = existingLineItem.getPrice();
		BigDecimal itemPrice = item.getPrice();
		if (!lineItemPrice.equals(itemPrice)) {
			throw new InputValidationException(ServiceErrorMessage.VALIDATION_ITEM_PRICE_CHANGED.getMessage());
		}

		existingLineItem.setQuantity(quantity);
		orderRepository.save(order);
	}

	private void doAmendOrderItemsValidation(Long id, Long itemId, Long quantity, Order order, Item item) {
		if (order == null) {
			throw new InputValidationException(ServiceErrorMessage.VALIDATION_NO_ORDER_FOR_ID.getMessage() + id);
		}

		if (item == null) {
			throw new InputValidationException(ServiceErrorMessage.VALIDATION_NO_ITEM_FOR_ID.getMessage() + itemId);
		}

		if (order.getPaid()) {
			throw new InputValidationException(ServiceErrorMessage.VALIDATION_ORDER_ALREADY_PAID.getMessage());
		}

		if (quantity.longValue() <= 0) {
			throw new InputValidationException(ServiceErrorMessage.VALIDATION_ADD_ITEM_QUANTITY_ZERO_OR_NEGATIVE.getMessage());
		}
	}
}
