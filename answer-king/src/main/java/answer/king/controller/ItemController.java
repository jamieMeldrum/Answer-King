package answer.king.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import answer.king.model.Item;
import answer.king.service.ItemService;

import javax.validation.Valid;

@RestController
@RequestMapping("/item")
public class ItemController {

	@Autowired
	private ItemService itemService;

	@RequestMapping(method = RequestMethod.GET)
	public List<Item> getAll() {
		return itemService.getAll();
	}

	@RequestMapping(method = RequestMethod.POST)
	public Item create(@RequestBody @Valid Item item) {
		return itemService.save(item);
	}

	@RequestMapping(value = "/{id}/amendPrice", method = RequestMethod.PUT)
	public Item amendPrice(@PathVariable("id") Long id, @RequestBody BigDecimal newPrice) {
		return itemService.amendPrice(id, newPrice);
	}
}
