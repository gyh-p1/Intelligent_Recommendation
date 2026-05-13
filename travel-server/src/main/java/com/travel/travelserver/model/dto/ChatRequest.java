package com.travel.travelserver.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatRequest {
    @Size(max = 80, message = "sessionId length must be less than 80")
    private String sessionId;

    @NotBlank(message = "message must not be blank")
    @Size(max = 1000, message = "message length must be less than 1000")
    private String message;

    @Size(max = 50, message = "city length must be less than 50")
    private String city;

    @Size(max = 50, message = "model length must be less than 50")
    private String model = "default";

    private Boolean stream = true;
}
