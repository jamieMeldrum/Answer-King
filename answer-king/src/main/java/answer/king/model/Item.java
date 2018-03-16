package answer.king.model;

import java.math.BigDecimal;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "T_ITEM")
public class Item {

	public static final String VALIDATION_MESSAGE_EMPTY_FIELD_NAME = "This field cannot be empty.";
	public static final String VALIDATION_MESSAGE_FIELD_TOO_LONG_NAME = "This field cannot be longer than 50 characters.";
	public static final String VALIDATION_MESSAGE_FIELD_NULL_PRICE = "This field cannot be null.";
	public static final String VALIDATION_MESSAGE_FIELD_MINIMUM_PRICE = "This field must be 0 or greater.";
	public static final String VALIDATION_MESSAGE_FIELD_TOO_LARGE_OR_PRECISE_PRICE = "This field cannot be larger than 99999.99 and can only be to 2dp.";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotEmpty (message = VALIDATION_MESSAGE_EMPTY_FIELD_NAME)
	@Size(max = 50, message = VALIDATION_MESSAGE_FIELD_TOO_LONG_NAME)
	private String name;

	@NotNull (message = VALIDATION_MESSAGE_FIELD_NULL_PRICE)
	@Min(value=0, message = VALIDATION_MESSAGE_FIELD_MINIMUM_PRICE)
	@Digits(integer=5, fraction=2, message = VALIDATION_MESSAGE_FIELD_TOO_LARGE_OR_PRECISE_PRICE)
	private BigDecimal price;

	public Item() {}
	public Item(String name, BigDecimal price) {
		this.name = name;
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
