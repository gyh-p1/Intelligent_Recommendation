package com.travel.travelserver.service.impl;

import com.travel.travelserver.common.PageResult;
import com.travel.travelserver.model.dto.FavoriteRequest;
import com.travel.travelserver.model.vo.FavoriteIdVO;
import com.travel.travelserver.model.vo.FavoriteVO;
import com.travel.travelserver.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Map<String, List<FavoriteVO>> favoriteStore = new ConcurrentHashMap<>();

    @Override
    public PageResult<FavoriteVO> pageFavorites(String clientId, int page, int pageSize) {
        List<FavoriteVO> favorites = new ArrayList<>(favoriteStore.getOrDefault(clientId, List.of()));
        favorites.sort(Comparator.comparing(FavoriteVO::getCreatedAt).reversed());
        return PageResult.of(favorites, page, pageSize);
    }

    @Override
    public FavoriteIdVO addFavorite(String clientId, FavoriteRequest request) {
        FavoriteVO favorite = new FavoriteVO(
                "fav_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12),
                request.getType(),
                request.getTargetId(),
                request.getTitle(),
                request.getDescription(),
                request.getCover(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
        favoriteStore.computeIfAbsent(clientId, ignored -> new ArrayList<>()).add(favorite);
        return new FavoriteIdVO(favorite.getFavoriteId());
    }

    @Override
    public boolean deleteFavorite(String clientId, String favoriteId) {
        List<FavoriteVO> favorites = favoriteStore.get(clientId);
        if (favorites == null) {
            return true;
        }
        favorites.removeIf(favorite -> favorite.getFavoriteId().equals(favoriteId));
        return true;
    }

    @Override
    public long count(String clientId) {
        return favoriteStore.getOrDefault(clientId, List.of()).size();
    }
}
