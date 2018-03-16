package answer.king.service;

import answer.king.error.InputValidationException;
import answer.king.model.Item;
import answer.king.repo.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ItemService {

	@Autowired
	private ItemRepository itemRepository;

	public List<Item> getAll() {
		return itemRepository.findAll();
	}

	public Item save(Item item) {
		itemRepository.save(item);
		return itemRepository.findOne(item.getId());
	}

	public Item amendPrice(Long id, BigDecimal newPrice) {
		Item item = itemRepository.findOne(id);
		if (item == null) {
			throw new InputValidationException(ServiceErrorMessage.VALIDATION_NO_ITEM_FOR_ID.getMessage() + id);
		}

		item.setPrice(newPrice);
		itemRepository.save(item);
		return itemRepository.findOne(id);
	}
}
