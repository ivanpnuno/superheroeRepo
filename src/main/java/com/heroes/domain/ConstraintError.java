package com.heroes.domain;

import javax.validation.ConstraintViolation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConstraintError {

	String message;
	String path;
	
	public static ConstraintError from(ConstraintViolation violation) {
		return ConstraintError.builder()
		.message(violation.getMessage())
		.path(violation.getPropertyPath().toString())
		.build();
	}
}
