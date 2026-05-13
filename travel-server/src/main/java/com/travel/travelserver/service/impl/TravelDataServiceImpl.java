package com.travel.travelserver.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.travel.travelserver.model.vo.CityOptionVO;
import com.travel.travelserver.model.vo.CityVO;
import com.travel.travelserver.service.TravelDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TravelDataServiceImpl implements TravelDataService {
    private static final String HOT_CITIES_KEY = "travel:hot:cities";
    private static final String QUICK_QUESTIONS_KEY = "travel:quick:questions";

    private final RedisCacheService redisCacheService;

    @Override
    public List<CityVO> getHotCities() {
        return redisCacheService.get(HOT_CITIES_KEY, new TypeReference<List<CityVO>>() {
        }).orElseGet(() -> {
            List<CityVO> cities = seedCities().stream().limit(6).toList();
            redisCacheService.set(HOT_CITIES_KEY, cities, Duration.ofHours(24));
            return cities;
        });
    }

    @Override
    public List<CityOptionVO> getCities(String keyword) {
        String safeKeyword = StringUtils.hasText(keyword) ? keyword.trim().toLowerCase(Locale.ROOT) : "";
        return seedCities().stream()
                .filter(city -> safeKeyword.isBlank()
                        || city.getCityCode().toLowerCase(Locale.ROOT).contains(safeKeyword)
                        || city.getCityName().contains(safeKeyword)
                        || city.getProvince().contains(safeKeyword))
                .map(city -> new CityOptionVO(city.getCityName(), city.getCityCode(), city.getProvince()))
                .toList();
    }

    @Override
    public List<String> getQuickQuestions() {
        return redisCacheService.get(QUICK_QUESTIONS_KEY, new TypeReference<List<String>>() {
        }).orElseGet(() -> {
            List<String> questions = List.of(
                    "帮我规划杭州 3 日游",
                    "北京有哪些适合亲子的景点？",
                    "成都 1500 元预算怎么玩？",
                    "上海两天一晚路线推荐",
                    "西安第一次去应该怎么安排？"
            );
            redisCacheService.set(QUICK_QUESTIONS_KEY, questions, Duration.ofHours(24));
            return questions;
        });
    }

    private List<CityVO> seedCities() {
        List<CityVO> cities = new ArrayList<>();
        cities.add(new CityVO("beijing", "北京", "北京市", "", List.of("历史文化", "亲子", "城市漫游")));
        cities.add(new CityVO("shanghai", "上海", "上海市", "", List.of("都市", "美食", "夜景")));
        cities.add(new CityVO("chengdu", "成都", "四川省", "", List.of("美食", "休闲", "熊猫")));
        cities.add(new CityVO("hangzhou", "杭州", "浙江省", "", List.of("自然风光", "城市地标", "轻松")));
        cities.add(new CityVO("guangzhou", "广州", "广东省", "", List.of("早茶", "岭南文化", "城市")));
        cities.add(new CityVO("shenzhen", "深圳", "广东省", "", List.of("海滨", "科技", "亲子")));
        cities.add(new CityVO("xian", "西安", "陕西省", "", List.of("历史", "博物馆", "夜游")));
        cities.add(new CityVO("nanjing", "南京", "江苏省", "", List.of("民国建筑", "博物馆", "城市漫游")));
        cities.add(new CityVO("suzhou", "苏州", "江苏省", "", List.of("园林", "古镇", "江南")));
        cities.add(new CityVO("chongqing", "重庆", "重庆市", "", List.of("山城", "火锅", "夜景")));
        return cities;
    }
}
