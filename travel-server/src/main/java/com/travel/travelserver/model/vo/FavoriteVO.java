package com.travel.travelserver.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteVO {
    private String favoriteId;
    private String type;
    private String targetId;
    private String title;
    private String description;
    private String cover;
    private String createdAt;
}
