package com.travel.travelserver.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FavoriteRequest {
    @NotBlank(message = "type must not be blank")
    @Size(max = 30, message = "type length must be less than 30")
    private String type;

    @NotBlank(message = "targetId must not be blank")
    @Size(max = 80, message = "targetId length must be less than 80")
    private String targetId;

    @NotBlank(message = "title must not be blank")
    @Size(max = 100, message = "title length must be less than 100")
    private String title;

    @Size(max = 500, message = "description length must be less than 500")
    private String description;

    @Size(max = 500, message = "cover length must be less than 500")
    private String cover;
}
