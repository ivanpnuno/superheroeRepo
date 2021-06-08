package com.heroes.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.heroes.domain.BaseError;
import com.heroes.domain.ConstraintError;
import com.heroes.domain.CustomFieldError;


@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler  {

	@ExceptionHandler({BaseException.class})
    public ResponseEntity<Object> handleBaseException(BaseException e) {
        return new ResponseEntity<Object>(BaseError.builder().code(e.getExceptionCode()).message(e.getMessage()).build(), HttpStatus.valueOf(e.getExceptionCode()));
    }
	
	@ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
		List<ConstraintError> errors = e.getConstraintViolations().stream().map(violation -> ConstraintError.from(violation)).collect(Collectors.toList());
		
        return new ResponseEntity<Object>( CustomFieldError.builder().errorType(e.getClass().getName()).parameterError(errors).build(), HttpStatus.BAD_REQUEST);
    }
}
