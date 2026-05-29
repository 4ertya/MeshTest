package com.banking.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AuthRequest {

    @Size(max = 200)
    private String email;

    @Pattern(regexp = "^7\\d{10}$", message = "Phone must be in format 79207865432")
    private String phone;

    @NotBlank
    @Size(min = 8, max = 500)
    private String password;
}
