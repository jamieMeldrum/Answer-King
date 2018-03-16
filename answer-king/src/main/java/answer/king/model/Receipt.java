package answer.king.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "T_RECEIPT")
public class Receipt {

	public static final String VALIDATION_MESSAGE_FIELD_NULL_PAYMENT = "This field cannot be null.";
	public static final String VALIDATION_MESSAGE_FIELD_MINIMUM_PAYMENT = "This field must be 0 or greater.";
	public static final String VALIDATION_MESSAGE_FIELD_TOO_LARGE_OR_PRESICE_PAYMENT = "This field cannot be larger than 99999.99, cannot be negative and can only be to 2dp.";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = VALIDATION_MESSAGE_FIELD_NULL_PAYMENT)
	@Min(value=0, message = VALIDATION_MESSAGE_FIELD_MINIMUM_PAYMENT)
	@Digits(integer=5, fraction=2, message = VALIDATION_MESSAGE_FIELD_TOO_LARGE_OR_PRESICE_PAYMENT)
	private BigDecimal payment;

	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER, optional=false)
	@JoinColumn(name = "ORDER_ID")
	private Order order;

	public Receipt() {}
	public Receipt(Order order, BigDecimal payment) {
		this.order = order;
		this.payment = payment;
	}

	public Long getOrderId() {
		return order.getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public BigDecimal getPayment() {
		return payment;
	}

	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}

	public BigDecimal getChange() {
		BigDecimal totalOrderPrice = order.getLineItems()
				.stream()
				.map(LineItem::getPricePerLineItem)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return payment.subtract(totalOrderPrice);
	}
}
