package com.rest.api.exception;

public class EntityNotFoundException extends CodeException {

	private static final long serialVersionUID = 1L;

	public EntityNotFoundException(int code, String message) {
		super(code, message);
	}
}
