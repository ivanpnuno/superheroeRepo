package com.heroes.exceptions;

public class CustomDataIntegrityViolationException extends BaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4002532138635056971L;

	public CustomDataIntegrityViolationException(String message) {
		super(message, 422);
	}
}
