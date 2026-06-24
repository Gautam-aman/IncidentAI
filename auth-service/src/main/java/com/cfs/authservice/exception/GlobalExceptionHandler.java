package com.cfs.authservice.exception;

import com.cfs.authservice.dto.MessageResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler  {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<MessageResponse> handleException(Exception ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(ex.getMessage()));
	}

}
