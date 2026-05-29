package com.banking.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class UserSearchRequest {

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^7\\d{10}$", message = "Phone must be in format 79207865432")
    private String phone;

    @Size(max = 500)
    private String name;

    @Size(max = 200)
    private String email;

    private int page = 0;
    private int size = 20;
}
