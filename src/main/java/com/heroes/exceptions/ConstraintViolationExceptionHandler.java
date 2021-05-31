package com.heroes.exceptions;

import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.heroes.domain.ConstraintError;
import com.heroes.domain.CustomFieldError;

@Provider
public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException>  {

	@Override
	public Response toResponse(ConstraintViolationException exception) {
		return Response.status(Response.Status.BAD_REQUEST)
                .entity(prepareMessage(exception))
                .type("application/json")
                .build();
	}
	
	private CustomFieldError prepareMessage(ConstraintViolationException exception) {
		CustomFieldError msg = new CustomFieldError();
		msg.setErrorType(exception.getClass().getName());
		msg.setParameterError(exception.getConstraintViolations().stream().map(violation -> ConstraintError.from(violation)).collect(Collectors.toList()));

        return msg;
    }
}
