package com.rest.api.web;

import java.util.Locale;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rest.api.exception.CarNotFoundException;
import com.rest.api.model.error.Error;
import com.rest.api.model.error.FieldError;
import com.rest.api.model.error.ValidationError;

@RestControllerAdvice
public class ControllerAdvice {
	
	private static final String VALIDATION_ERROR = "exception.validation";
	private static final String INTERNAL_SERVER_ERROR = "exception.internal.server.error";
	
	@Autowired
	private MessageSource messageSource;
	
	@ResponseBody
	@ExceptionHandler(CarNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Error handleCarNotFound(CarNotFoundException ex) {
		return new Error(ex.getCode(), ex.getMessage());
	}

	@ResponseBody
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ValidationError handleMethodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {
		BindingResult result = ex.getBindingResult();
		ValidationError ve = new ValidationError(100, messageSource.getMessage(VALIDATION_ERROR, null, locale));
		
		for (org.springframework.validation.FieldError oe : result.getFieldErrors()) {
			ve.addFieldError(new FieldError(1, oe.getField(), messageSource.getMessage(oe, locale)));
		}
		
		return ve;
	}
		
	@ResponseBody
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ValidationError handleConstraintViolationException(ConstraintViolationException ex, Locale locale) {
		ValidationError ve = new ValidationError(100, messageSource.getMessage(VALIDATION_ERROR, null, locale));
		String field = "";
		int index;
		
		for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
			index = cv.getPropertyPath().toString().lastIndexOf(".");
			
			if(index == -1)
				field = cv.getPropertyPath().toString();
			else
				field = cv.getPropertyPath().toString().substring(cv.getPropertyPath().toString().lastIndexOf(".")+1);
				
			//ve.addFieldError(new FieldError(1, field, messageSource.getMessage(cv.getMessageTemplate(), null, locale)));
			ve.addFieldError(new FieldError(1, field, cv.getMessage()));
		};

		return ve;
	}
	
	@ResponseBody
	@ExceptionHandler(TransactionSystemException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ValidationError handleTransactionSystemException(TransactionSystemException ex, Locale locale) {
		Throwable t = ex.getRootCause();
        if(t instanceof ConstraintViolationException) {
        	ConstraintViolationException cve = (ConstraintViolationException)t;
        	return handleConstraintViolationException(cve, locale);
        } else
        	return null;
	}
	
	@ResponseBody
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Error handleAllExceptions(Exception ex, Locale locale) {
		//TODO Hide Exception message
		return new Error(999, messageSource.getMessage(INTERNAL_SERVER_ERROR, null, locale));
	}

}
