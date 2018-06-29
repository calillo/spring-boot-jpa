package com.rest.api.model.error;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends Error {

	private List<FieldError> errors = new ArrayList<FieldError>();
	
	public ValidationError(int code, String message) {
		super(code, message);
	}
	
	public void addFieldError(FieldError fieldError) {
		errors.add(fieldError);
	}

	public List<FieldError> getErrorList() {
		return errors;
	}
	
}
