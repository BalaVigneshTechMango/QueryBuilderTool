package com.tm.querybuilder.validation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tm.querybuilder.response.QueryResponsePojo;

@RestControllerAdvice
public class ExpectionValidation {
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public QueryResponsePojo handleValidationExceptions(MethodArgumentNotValidException ex) {
		QueryResponsePojo responsePojo = new QueryResponsePojo();
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			errors.put(((FieldError) error).getField(), error.getDefaultMessage());
		});
		responsePojo.setMessage("Not Valid Request");
		responsePojo.setResponseData(errors);
		responsePojo.setIsSuccess(false);
		return responsePojo;
	}
}
