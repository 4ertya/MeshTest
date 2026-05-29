package com.banking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserResponse {

    private Long id;
    private String name;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;

    private List<String> emails;
    private List<String> phones;
    private BigDecimal balance;
}
