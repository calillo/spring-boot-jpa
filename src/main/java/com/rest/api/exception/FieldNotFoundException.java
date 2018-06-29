package com.rest.api.exception;

public class FieldNotFoundException extends EntityNotFoundException {

	private static final long serialVersionUID = 1L;

	public FieldNotFoundException() {
		//TODO I18N FieldNotFoundException message
		super(1, "Field not found!");
	}
}
