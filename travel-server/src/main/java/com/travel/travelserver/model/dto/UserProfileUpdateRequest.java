package com.travel.travelserver.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    @Size(max = 40, message = "nickname length must be less than 40")
    private String nickname;

    @Size(max = 500, message = "avatar length must be less than 500")
    private String avatar;
}
