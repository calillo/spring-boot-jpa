package com.rest.api.exception;

public class CarNotFoundException extends EntityNotFoundException {

	private static final long serialVersionUID = 1L;

	public CarNotFoundException() {
		super(1, "car.notfound");
	}
}
