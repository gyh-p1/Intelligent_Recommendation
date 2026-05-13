package com.travel.travelserver.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityVO {
    private String cityCode;
    private String cityName;
    private String province;
    private String cover;
    private List<String> tags;
}
