package com.travel.travelserver.service;

import com.travel.travelserver.model.vo.CityOptionVO;
import com.travel.travelserver.model.vo.CityVO;

import java.util.List;

public interface TravelDataService {
    List<CityVO> getHotCities();

    List<CityOptionVO> getCities(String keyword);

    List<String> getQuickQuestions();
}
