package com.travel.travelserver.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsVO {
    private boolean streamEnabled;
    private String defaultModel;
    private String theme;
    private boolean cacheEnabled;
}
