package com.travel.travelserver.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpotVO {
    private String spotId;
    private String name;
    private String level;
    private String address;
    private String description;
    private String recommendedDuration;
    private BigDecimal ticketPrice;
    private List<String> tags;
    private String reason;
}
