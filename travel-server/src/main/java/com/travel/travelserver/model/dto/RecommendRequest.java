package com.travel.travelserver.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class RecommendRequest {
    @NotBlank(message = "city must not be blank")
    @Size(max = 50, message = "city length must be less than 50")
    private String city;

    @Size(max = 50, message = "cityCode length must be less than 50")
    private String cityCode;

    @NotNull(message = "budget must not be null")
    @DecimalMin(value = "0.01", message = "budget must be greater than 0")
    @DecimalMax(value = "100000.00", message = "budget must not be greater than 100000")
    private BigDecimal budget;

    @NotNull(message = "days must not be null")
    @Min(value = 1, message = "days must be greater than 0")
    @Max(value = 30, message = "days must not be greater than 30")
    private Integer days;

    @Min(value = 1, message = "travelers must be greater than 0")
    @Max(value = 20, message = "travelers must not be greater than 20")
    private Integer travelers = 1;

    @Size(max = 10, message = "preferences size must be less than 10")
    private List<String> preferences = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
}
