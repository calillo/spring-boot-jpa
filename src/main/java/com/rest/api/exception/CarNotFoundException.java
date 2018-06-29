package com.rest.api.exception;

public class CarNotFoundException extends EntityNotFoundException {

	private static final long serialVersionUID = 1L;

	public CarNotFoundException() {
		//TODO I18N CarNotFoundException message
		super(1, "Car not found!");
	}
}
