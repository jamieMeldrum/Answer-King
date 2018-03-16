package answer.king.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dom4j.tree.AbstractEntity;

import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "T_LINEITEM")
public class LineItem {

    public static final String VALIDATION_MESSAGE_NULL_PRICE = "This field cannot be null.";
    public static final String VALIDATION_MESSAGE_MINIMUM_VALUE_PRICE = "This field must be greater than 0.";
    public static final String VALIDATION_MESSAGE_TOO_LARGE_OR_PRECISE_PRICE = "This field cannot be larger than 99999.99, cannot be negative and can only be to 2dp.";
    public static final String VALIDATION_MESSAGE_FIELD_MINIMUM_QUANTITY = "This field must be greater than 0.";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull (message = VALIDATION_MESSAGE_NULL_PRICE)
    @Min(value=0, message = VALIDATION_MESSAGE_MINIMUM_VALUE_PRICE)
    @Digits(integer=5, fraction=2, message = VALIDATION_MESSAGE_TOO_LARGE_OR_PRECISE_PRICE)
    private BigDecimal price;

    @Min(value=1, message = VALIDATION_MESSAGE_FIELD_MINIMUM_QUANTITY)
    private long quantity;

    @ManyToOne(fetch = FetchType.EAGER, optional=false)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional=false)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    public LineItem() {}
    public LineItem(Item item, Order order, long quantity) {
        this.item = item;
        this.order = order;
        this.price = item.getPrice();
        this.quantity = quantity;
    }

    public Long getOrderId() {
        return order.getId();
    }

    public BigDecimal getPricePerLineItem() {
        return price.multiply(new BigDecimal(quantity));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public long getQuantity() {
        return quantity;
    }
    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
    public Item getItem() {
        return item;
    }
    public void setItem(Item item) {
        this.item = item;
    }
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
}
