package com.travel.travelserver.controller;

import com.travel.travelserver.common.ClientIdUtils;
import com.travel.travelserver.common.PageResult;
import com.travel.travelserver.common.Result;
import com.travel.travelserver.model.dto.FavoriteRequest;
import com.travel.travelserver.model.vo.FavoriteIdVO;
import com.travel.travelserver.model.vo.FavoriteVO;
import com.travel.travelserver.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping
    public Result<PageResult<FavoriteVO>> getFavorites(
            @RequestHeader(value = "X-Client-Id", required = false) String clientId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(favoriteService.pageFavorites(ClientIdUtils.resolve(clientId), page, pageSize));
    }

    @PostMapping
    public Result<FavoriteIdVO> addFavorite(
            @RequestHeader(value = "X-Client-Id", required = false) String clientId,
            @RequestBody @Valid FavoriteRequest request
    ) {
        return Result.success(favoriteService.addFavorite(ClientIdUtils.resolve(clientId), request));
    }

    @DeleteMapping("/{favoriteId}")
    public Result<Boolean> deleteFavorite(
            @RequestHeader(value = "X-Client-Id", required = false) String clientId,
            @PathVariable String favoriteId
    ) {
        return Result.success(favoriteService.deleteFavorite(ClientIdUtils.resolve(clientId), favoriteId));
    }
}
