package com.banking.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class AddPhoneRequest {

    @NotBlank
    @Pattern(regexp = "^7\\d{10}$", message = "Phone must be in format 79207865432")
    private String phone;
}
