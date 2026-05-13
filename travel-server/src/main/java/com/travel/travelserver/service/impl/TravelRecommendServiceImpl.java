package com.travel.travelserver.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.travel.travelserver.model.dto.RecommendRequest;
import com.travel.travelserver.model.vo.BudgetAdviceVO;
import com.travel.travelserver.model.vo.BudgetItemVO;
import com.travel.travelserver.model.vo.ItineraryDayVO;
import com.travel.travelserver.model.vo.ItineraryItemVO;
import com.travel.travelserver.model.vo.RecommendResponse;
import com.travel.travelserver.model.vo.SpotVO;
import com.travel.travelserver.service.HistoryService;
import com.travel.travelserver.service.TravelRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TravelRecommendServiceImpl implements TravelRecommendService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RedisCacheService redisCacheService;
    private final HistoryService historyService;

    @Override
    public RecommendResponse generateRecommend(RecommendRequest request, String clientId) {
        String cacheKey = "travel:recommend:" + hashRequest(request);
        return redisCacheService.get(cacheKey, new TypeReference<RecommendResponse>() {
        }).map(cached -> {
            cached.setCached(true);
            return cached;
        }).orElseGet(() -> {
            RecommendResponse response = buildRecommend(request);
            redisCacheService.set(cacheKey, response, Duration.ofMinutes(30));
            historyService.addHistory(
                    clientId,
                    "recommend",
                    "%s %d 日游推荐".formatted(request.getCity(), request.getDays()),
                    response.getSummary()
            );
            return response;
        });
    }

    private RecommendResponse buildRecommend(RecommendRequest request) {
        BigDecimal totalBudget = request.getBudget().setScale(2, RoundingMode.HALF_UP);
        BigDecimal estimatedCost = totalBudget.multiply(new BigDecimal("0.92")).setScale(2, RoundingMode.HALF_UP);
        int travelers = request.getTravelers() == null ? 1 : request.getTravelers();
        List<String> preferences = request.getPreferences() == null ? List.of() : request.getPreferences();
        String preferenceText = preferences.isEmpty() ? "轻松游览" : String.join("、", preferences);

        List<SpotVO> spots = buildSpots(request.getCity());
        List<ItineraryDayVO> itinerary = buildItinerary(request.getCity(), request.getDays(), spots);
        BudgetAdviceVO budgetAdvice = new BudgetAdviceVO(
                totalBudget,
                estimatedCost,
                "CNY",
                buildBudgetItems(estimatedCost)
        );

        String summary = "适合 %d 人 %d 天的%s方案，重点安排%s等景点，并控制在 %.0f 元预算内。"
                .formatted(travelers, request.getDays(), preferenceText, spots.get(0).getName(), totalBudget.doubleValue());

        return new RecommendResponse(
                "rec_" + System.currentTimeMillis(),
                request.getCity(),
                summary,
                budgetAdvice,
                spots,
                itinerary,
                List.of(
                        "热门景点建议提前查看预约和开放时间。",
                        "城市内短途移动优先选择地铁、公交或打车组合。",
                        "餐饮和门票预算建议预留 10% 作为弹性支出。"
                ),
                false,
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
    }

    private List<BudgetItemVO> buildBudgetItems(BigDecimal estimatedCost) {
        return List.of(
                new BudgetItemVO("交通", estimatedCost.multiply(new BigDecimal("0.22")).setScale(2, RoundingMode.HALF_UP)),
                new BudgetItemVO("餐饮", estimatedCost.multiply(new BigDecimal("0.26")).setScale(2, RoundingMode.HALF_UP)),
                new BudgetItemVO("门票", estimatedCost.multiply(new BigDecimal("0.16")).setScale(2, RoundingMode.HALF_UP)),
                new BudgetItemVO("住宿", estimatedCost.multiply(new BigDecimal("0.36")).setScale(2, RoundingMode.HALF_UP))
        );
    }

    private List<SpotVO> buildSpots(String city) {
        if (city.contains("北京")) {
            return List.of(
                    spot("bj_gugong", "故宫博物院", "5A", "北京市东城区景山前街4号", "适合了解明清宫廷建筑与历史文化。", "3-4小时", "历史文化"),
                    spot("bj_tiantan", "天坛公园", "5A", "北京市东城区天坛东路甲1号", "城市中轴线上的经典公园，适合半日慢逛。", "2-3小时", "城市漫游"),
                    spot("bj_mutianyu", "慕田峪长城", "5A", "北京市怀柔区渤海镇", "相对舒适的长城线路，适合第一次到北京的游客。", "4-5小时", "自然风光")
            );
        }
        if (city.contains("上海")) {
            return List.of(
                    spot("sh_waitan", "外滩", "", "上海市黄浦区中山东一路", "欣赏浦江两岸建筑和城市夜景的代表路线。", "1-2小时", "夜景"),
                    spot("sh_bowuguan", "上海博物馆", "", "上海市黄浦区人民大道201号", "适合雨天和文化主题行程。", "2-3小时", "博物馆"),
                    spot("sh_yuyuan", "豫园", "4A", "上海市黄浦区福佑路168号", "老城厢风貌和本帮小吃集中区域。", "2小时", "美食")
            );
        }
        if (city.contains("成都")) {
            return List.of(
                    spot("cd_panda", "成都大熊猫繁育研究基地", "4A", "成都市成华区熊猫大道1375号", "亲子和第一次到成都的高人气景点。", "3-4小时", "亲子"),
                    spot("cd_kuanzhai", "宽窄巷子", "2A", "成都市青羊区金河路口", "适合轻松散步和体验成都慢生活。", "1-2小时", "休闲"),
                    spot("cd_jinli", "锦里古街", "", "成都市武侯区武侯祠大街231号", "夜游和小吃体验友好。", "1-2小时", "美食")
            );
        }
        if (city.contains("西安")) {
            return List.of(
                    spot("xa_bingmayong", "秦始皇帝陵博物院", "5A", "西安市临潼区秦陵北路", "西安历史主题行程的核心景点。", "3-4小时", "历史"),
                    spot("xa_citywall", "西安城墙", "5A", "西安市碑林区南大街", "适合骑行和俯瞰古城格局。", "2-3小时", "城市地标"),
                    spot("xa_datang", "大唐不夜城", "", "西安市雁塔区慈恩路46号", "夜景和表演氛围浓厚。", "2小时", "夜游")
            );
        }
        return List.of(
                spot("hz_xihu", "西湖", "5A", "杭州市西湖区龙井路1号", "杭州代表性景区，适合步行、骑行和乘船游览。", "3-4小时", "自然风光"),
                spot("hz_lingyin", "灵隐寺", "5A", "杭州市西湖区法云弄1号", "山林氛围和人文体验兼具，适合上午游览。", "2-3小时", "历史文化"),
                spot("hz_grand_canal", "京杭大运河杭州段", "4A", "杭州市拱墅区", "适合夜游和体验本地生活气息。", "2小时", "城市漫游")
        );
    }

    private SpotVO spot(String id, String name, String level, String address, String description, String duration, String tag) {
        return new SpotVO(
                id,
                name,
                level,
                address,
                description,
                duration,
                BigDecimal.ZERO,
                List.of(tag, "推荐"),
                "路线成熟、交通方便，适合作为本次行程的核心节点。"
        );
    }

    private List<ItineraryDayVO> buildItinerary(String city, Integer days, List<SpotVO> spots) {
        return java.util.stream.IntStream.rangeClosed(1, days)
                .mapToObj(day -> {
                    SpotVO mainSpot = spots.get((day - 1) % spots.size());
                    return new ItineraryDayVO(
                            day,
                            "%s第 %d 天路线".formatted(city, day),
                            List.of(
                                    new ItineraryItemVO("09:00", "前往%s，安排核心游览。".formatted(mainSpot.getName())),
                                    new ItineraryItemVO("12:30", "在景点周边用餐，预留休息时间。"),
                                    new ItineraryItemVO("15:00", "补充城市漫游、咖啡或博物馆等轻量体验。"),
                                    new ItineraryItemVO("19:00", "体验本地晚餐，结束当天行程。")
                            )
                    );
                })
                .toList();
    }

    private String hashRequest(RecommendRequest request) {
        String raw = "%s|%s|%s|%s|%s|%s".formatted(
                request.getCity(),
                Objects.toString(request.getCityCode(), ""),
                request.getBudget(),
                request.getDays(),
                request.getTravelers(),
                Objects.toString(request.getPreferences(), "")
        );
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            return Integer.toHexString(raw.hashCode());
        }
    }
}
