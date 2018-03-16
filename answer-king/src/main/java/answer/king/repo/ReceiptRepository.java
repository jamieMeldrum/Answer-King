package answer.king.repo;

import answer.king.model.Order;
import answer.king.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    public Receipt findReceiptByOrder(Order order);
}
