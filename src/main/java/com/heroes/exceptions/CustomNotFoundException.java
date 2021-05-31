package com.heroes.exceptions;

public class CustomNotFoundException extends BaseException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9021788299577114401L;

	public CustomNotFoundException(String message) {
		super(message, 404);
	}
}
