package com.travel.travelserver.service;

import com.travel.travelserver.model.dto.RecommendRequest;
import com.travel.travelserver.model.vo.RecommendResponse;

public interface TravelRecommendService {
    RecommendResponse generateRecommend(RecommendRequest request, String clientId);
}
