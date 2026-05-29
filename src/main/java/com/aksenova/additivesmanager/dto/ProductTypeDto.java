package com.aksenova.additivesmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductTypeDto {

    @NotBlank(message = "typeName is required")
    private String typeName;

    private String description;
}
