package com.heroes.exceptions;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3307068449507830691L;
	private Integer exceptionCode;
	
	public BaseException(String message, Integer exceptionCode) {
		super(message);
		this.exceptionCode = exceptionCode;
	}
}
