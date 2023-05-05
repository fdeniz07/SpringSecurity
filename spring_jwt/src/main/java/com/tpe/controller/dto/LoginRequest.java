package com.tpe.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {

    @NotNull
    @NotBlank
    private String userName;

    @NotNull
    @NotBlank
    private String password;
}
