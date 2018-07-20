package com.rest.api.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Car {
	
	@Id
	@SequenceGenerator(name = "carIdSeq", sequenceName = "CAR_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carIdSeq")
	private long id;
	
	@NotNull
	@Size(min = 0, max = 10)
	private String brand;
	
	@NotNull
	@Size(min = 0, max = 10)
	private String model;
	
	@PositiveOrZero
	private int version;
	
	@NotNull
	@PositiveOrZero
	@Digits(integer = 10, fraction = 2)
	private BigDecimal price;
	
	@JsonIgnore
	private ZonedDateTime insertDate;
	@JsonIgnore
	private ZonedDateTime updateDate;
	
	public Car() {
	}
	
	public Car(long id, String brand, String model, int version, BigDecimal price, ZonedDateTime insertDate,
			ZonedDateTime updateDate) {
		super();
		this.id = id;
		this.brand = brand;
		this.model = model;
		this.version = version;
		this.price = price;
		this.insertDate = insertDate;
		this.updateDate = updateDate;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public ZonedDateTime getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(ZonedDateTime insertDate) {
		this.insertDate = insertDate;
	}

	public ZonedDateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(ZonedDateTime updateDate) {
		this.updateDate = updateDate;
	}
}
