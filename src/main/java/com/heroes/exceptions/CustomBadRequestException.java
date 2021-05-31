package com.heroes.exceptions;

public class CustomBadRequestException extends BaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8694204429867835548L;

	public CustomBadRequestException(String message) {
		super(message, 400);
	}
}
