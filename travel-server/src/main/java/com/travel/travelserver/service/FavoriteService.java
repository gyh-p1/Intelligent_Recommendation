package com.travel.travelserver.service;

import com.travel.travelserver.common.PageResult;
import com.travel.travelserver.model.dto.FavoriteRequest;
import com.travel.travelserver.model.vo.FavoriteIdVO;
import com.travel.travelserver.model.vo.FavoriteVO;

public interface FavoriteService {
    PageResult<FavoriteVO> pageFavorites(String clientId, int page, int pageSize);

    FavoriteIdVO addFavorite(String clientId, FavoriteRequest request);

    boolean deleteFavorite(String clientId, String favoriteId);

    long count(String clientId);
}
