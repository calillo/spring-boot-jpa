package com.rest.api.model.error;

public class FieldError extends Error {

	private String field;
	
	public FieldError(int code, String message) {
		super(code, message);
	}
	
	public FieldError(int code, String field, String message) {
		super(code, message);
		this.field = field;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
	
}
