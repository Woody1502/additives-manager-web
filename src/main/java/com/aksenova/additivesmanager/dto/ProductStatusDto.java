package com.aksenova.additivesmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductStatusDto {

    @NotBlank(message = "statusName is required")
    private String statusName;

    private String description;
}
