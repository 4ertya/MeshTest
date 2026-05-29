package com.banking.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AddEmailRequest {

    @NotBlank
    @Email
    @Size(max = 200)
    private String email;
}
