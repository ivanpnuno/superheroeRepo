package com.heroes.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.http.MediaType;

import com.heroes.domain.BaseError;


@Provider
public class CustomExceptionHandler implements ExceptionMapper<BaseException>  {

	@Override
	public Response toResponse(BaseException exception) {
		return Response.status(exception.getExceptionCode())
                .entity(BaseError.builder()
        				.code(exception.getExceptionCode())
        				.message(exception.getMessage())
        				.build())
                .type(MediaType.APPLICATION_JSON_VALUE)
                .build();
	}

}
