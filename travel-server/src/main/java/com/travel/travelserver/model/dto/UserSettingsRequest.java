package com.travel.travelserver.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserSettingsRequest {
    private Boolean streamEnabled;

    @Size(max = 50, message = "defaultModel length must be less than 50")
    private String defaultModel;

    @Size(max = 20, message = "theme length must be less than 20")
    private String theme;

    private Boolean cacheEnabled;
}
