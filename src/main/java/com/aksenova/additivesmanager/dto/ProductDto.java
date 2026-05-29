package com.aksenova.additivesmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProductDto {

    @NotBlank(message = "productName is required")
    private String productName;

    @NotNull(message = "productTypeId is required")
    private Integer productTypeId;

    @NotNull(message = "statusId is required")
    private Integer statusId;

    private String tnVedCode;
    private String eNumber;
    private String releaseForm;
    private List<Integer> manufacturerIds;
    private String sgrNumber;
    private LocalDate sgrRegistrationDate;

    @Positive(message = "shelfLifeMonths must be positive")
    private Integer shelfLifeMonths;

    private String storageConditions;
}
