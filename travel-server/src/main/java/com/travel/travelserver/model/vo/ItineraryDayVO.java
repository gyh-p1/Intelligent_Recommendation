package com.travel.travelserver.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDayVO {
    private Integer day;
    private String title;
    private List<ItineraryItemVO> items;
}
