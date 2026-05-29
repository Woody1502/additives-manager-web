package com.aksenova.additivesmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ManufacturerDto {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "country is required")
    private String country;

    private String legalAddress;
    private String inn;
    private String ogrn;
    private String contactPhone;

    @Email(message = "contactEmail must be a valid email")
    private String contactEmail;

    private String website;
}
