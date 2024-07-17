package com.oliveiradev.exceptions.handler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.oliveiradev.exceptions.RequiredObjectIsNullException;
import com.oliveiradev.exceptions.ResourceNotFoundException;
import com.oliveiradev.exceptions.ExceptionsResponse;
import com.oliveiradev.exceptions.InvalidJwtAuthenticationException;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionsResponse> handlerAllExceptions(
        Exception ex, WebRequest request) {
        ExceptionsResponse exceptionsResponse = new ExceptionsResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false));
        return new ResponseEntity<>(exceptionsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionsResponse> handleNotFoundExceptions(
        Exception ex, WebRequest request) {
        ExceptionsResponse exceptionsResponse = new ExceptionsResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RequiredObjectIsNullException.class)
    public final ResponseEntity<ExceptionsResponse> handleBadRequestExceptions(
        Exception ex, WebRequest request) {
        ExceptionsResponse exceptionsResponse = new ExceptionsResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponse, HttpStatus.BAD_REQUEST);
    }

	@ExceptionHandler(InvalidJwtAuthenticationException.class)
    public final ResponseEntity<ExceptionsResponse> handleInvalidJwtAuthenticationExceptions(
        Exception ex, WebRequest request) {
        ExceptionsResponse exceptionsResponse = new ExceptionsResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false));

        return new ResponseEntity<>(exceptionsResponse, HttpStatus.FORBIDDEN);
    }
}