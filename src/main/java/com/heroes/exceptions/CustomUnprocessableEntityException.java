package com.heroes.exceptions;

public class CustomUnprocessableEntityException extends BaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8694204429867835548L;

	public CustomUnprocessableEntityException(String message) {
		super(message, 422);
	}
}
