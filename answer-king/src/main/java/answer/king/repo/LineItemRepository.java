package answer.king.repo;

import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineItemRepository extends JpaRepository<LineItem, Long> {
    public List<LineItem> findByOrder(Order order);
    public LineItem findByOrderAndItem(Order order, Item item);
}
