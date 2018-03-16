package answer.king.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@RequestMapping(method = RequestMethod.GET)
	public List<Order> getAll() {
		return orderService.getAll();
	}

	@RequestMapping(method = RequestMethod.POST)
	public Order create() {
		return orderService.save(new Order());
	}

	@RequestMapping(value = "/{id}/addItem/{itemId}/{quantity}", method = RequestMethod.PUT)
	public void addItem(@PathVariable("id") Long id, @PathVariable("itemId") Long itemId, @PathVariable("quantity") Long quantity) {
		orderService.addItem(id, itemId, quantity);
	}

	/**
	 * I've added in an extra call because it makes the client side front-end a little clearer in my opinion. I have
	 * still supported the functionality requested in the spec this allowed you to decrease what is on an Order too. It
	 * would prob make sense to have this code path delete lineItems from Orders if the quantity were set to 0 but for
	 * simplicity I have prevented 0 from being valid. We could also add a delete controller method instead.
	 */
	@RequestMapping(value = "/{id}/setItemQuantity/{itemId}/{quantity}", method = RequestMethod.PUT)
	public void setItemQuantity(@PathVariable("id") Long id, @PathVariable("itemId") Long itemId, @PathVariable("quantity") Long quantity) {
		orderService.setItemQuantity(id, itemId, quantity);
	}

	@RequestMapping(value = "/{id}/pay", method = RequestMethod.PUT)
	public Receipt pay(@PathVariable("id") Long id, @RequestBody BigDecimal payment) {
		return orderService.pay(id, payment);
	}
}
