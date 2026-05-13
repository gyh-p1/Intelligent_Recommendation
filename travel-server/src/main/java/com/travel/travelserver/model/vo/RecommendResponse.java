package com.travel.travelserver.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendResponse {
    private String requestId;
    private String city;
    private String summary;
    private BudgetAdviceVO budgetAdvice;
    private List<SpotVO> spots;
    private List<ItineraryDayVO> itinerary;
    private List<String> tips;
    private boolean cached;
    private String createdAt;
}
