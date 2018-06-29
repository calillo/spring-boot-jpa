package com.rest.api.exception;

public class CodeException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private int code;

	public CodeException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
		
}
