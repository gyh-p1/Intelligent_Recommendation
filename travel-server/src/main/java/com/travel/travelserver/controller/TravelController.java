package com.travel.travelserver.controller;

import com.travel.travelserver.common.ClientIdUtils;
import com.travel.travelserver.common.Result;
import com.travel.travelserver.model.dto.RecommendRequest;
import com.travel.travelserver.model.vo.CityOptionVO;
import com.travel.travelserver.model.vo.CityVO;
import com.travel.travelserver.model.vo.RecommendResponse;
import com.travel.travelserver.service.TravelDataService;
import com.travel.travelserver.service.TravelRecommendService;
import com.travel.travelserver.service.impl.RateLimitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel")
public class TravelController {
    private final TravelRecommendService recommendService;
    private final TravelDataService travelDataService;
    private final RateLimitService rateLimitService;

    @GetMapping("/hot-cities")
    public Result<List<CityVO>> getHotCities(@RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        rateLimitService.check("hot-cities", clientId, 60);
        return Result.success(travelDataService.getHotCities());
    }

    @GetMapping("/cities")
    public Result<List<CityOptionVO>> getCities(@RequestParam(value = "keyword", required = false) String keyword) {
        return Result.success(travelDataService.getCities(keyword));
    }

    @PostMapping("/recommend")
    public Result<RecommendResponse> recommend(
            @RequestHeader(value = "X-Client-Id", required = false) String clientId,
            @RequestBody @Valid RecommendRequest request
    ) {
        String safeClientId = ClientIdUtils.resolve(clientId);
        rateLimitService.check("recommend", safeClientId, 10);
        return Result.success(recommendService.generateRecommend(request, safeClientId));
    }
}
