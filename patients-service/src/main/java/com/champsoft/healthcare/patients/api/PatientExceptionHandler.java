package com.champsoft.healthcare.patients.api;

import com.champsoft.healthcare.patients.application.exception.DuplicatePatientException;
import com.champsoft.healthcare.patients.application.exception.PatientNotFoundException;
import com.champsoft.healthcare.patients.domain.exception.*;
import com.champsoft.healthcare.patients.web.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice(assignableTypes = PatientController.class)
public class PatientExceptionHandler {

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> notFound(PatientNotFoundException ex, HttpServletRequest req){
        return build(HttpStatus.NOT_FOUND,ex,req);
    }

    @ExceptionHandler(DuplicatePatientException.class)
    public ResponseEntity<ApiErrorResponse> conflict(DuplicatePatientException ex, HttpServletRequest req){
        return build(HttpStatus.CONFLICT,ex,req);
    }
    @ExceptionHandler(PatientStatusException.class)
    public ResponseEntity<String> handlePatientStatusException(PatientStatusException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, Exception ex, HttpServletRequest req){
        var body = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler({
          ExpiredHealthInsuranceCardException.class,
            InvalidInsuranceCardNumber.class,
            InvalidAddressException.class,
            IllegalArgumentException.class,
            PatientEligibilityAppointmentException.class
    })
    public ResponseEntity<ApiErrorResponse> badRequest(RuntimeException ex, HttpServletRequest req){
        return build(HttpStatus.BAD_REQUEST,ex,req);
    }

    //+
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse>
    badRequest(org.springframework.web.bind.MethodArgumentNotValidException ex,
               HttpServletRequest req){

        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField()+" "+ err.getDefaultMessage()).orElse("Failed");
        return build(HttpStatus.BAD_REQUEST,new IllegalArgumentException(message),req);

    }



}
