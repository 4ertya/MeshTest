package com.banking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {

    private int code;
    private String message;
    private Object details;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String message, Object details, LocalDateTime timestamp) {
       this.code = status;
       this.message = message;
       this.details = details;
       this.timestamp = timestamp;
    }

    public ErrorResponse(int status, String message, Object details) {
        this(status, message, details, LocalDateTime.now());
    }

    public ErrorResponse(int status, String message) {
        this(status, message, null, LocalDateTime.now());
    }

}
