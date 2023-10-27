package com.project.Eparking.exception;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<ApiException> handleApiRequestException(ApiRequestException e) {
        ApiException apiException = new ApiException();
        apiException.setTitle("Internal Server Error");
        apiException.setMessage(e.getMessage());
        apiException.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        ZoneId vietnamZoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vietnamTime = ZonedDateTime.now(vietnamZoneId);
        apiException.setTimestamp(vietnamTime);
        return new ResponseEntity<>(apiException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidDefinitionException.class)
    public ResponseEntity<ApiException> handleInvalidDefinitionException(InvalidDefinitionException e) {
        ApiException apiException = new ApiException();
        apiException.setTitle("Internal Server Error");
        apiException.setMessage("Invalid JSON Definition");
        apiException.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        ZoneId vietnamZoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vietnamTime = ZonedDateTime.now(vietnamZoneId);
        apiException.setTimestamp(vietnamTime);
        return new ResponseEntity<>(apiException, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
