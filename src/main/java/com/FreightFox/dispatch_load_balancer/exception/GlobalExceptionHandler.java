
package com.FreightFox.dispatch_load_balancer.exception;

import com.FreightFox.dispatch_load_balancer.dto.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle DTO validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        ApiErrorResponse response = new ApiErrorResponse(
                400,
                "Bad Request",
                "Validation failed",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    // 2. Handle duplicate IDs and business-rule errors
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex) {

        ApiErrorResponse response = new ApiErrorResponse(
                400,
                "Bad Request",
                ex.getMessage(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    // 3. Handle database constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDatabaseError(
            DataIntegrityViolationException ex) {

        ApiErrorResponse response = new ApiErrorResponse(
                409,
                "Conflict",
                "The record conflicts with existing database data.",
                null
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // 4. Handle malformed JSON and invalid enum values
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidJson(
            HttpMessageNotReadableException ex) {

        ApiErrorResponse response = new ApiErrorResponse(
                400,
                "Bad Request",
                "Invalid JSON request. Check the field names, data types and enum values.",
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    // 5. Handle constraint violations outside request-body validation
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex) {

        ApiErrorResponse response = new ApiErrorResponse(
                400,
                "Bad Request",
                ex.getMessage(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    // 6. Handle unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(
            Exception ex,
            HttpServletRequest request) {

        ex.printStackTrace();

        ApiErrorResponse response = new ApiErrorResponse(
                500,
                "Internal Server Error",
                "An unexpected error occurred.",
                null
        );

        return ResponseEntity.internalServerError().body(response);
    }
}
