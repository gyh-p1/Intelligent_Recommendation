package com.travel.travelserver.service.impl;

import com.travel.travelserver.model.dto.UserProfileUpdateRequest;
import com.travel.travelserver.model.dto.UserSettingsRequest;
import com.travel.travelserver.model.vo.UserProfileVO;
import com.travel.travelserver.model.vo.UserSettingsVO;
import com.travel.travelserver.service.FavoriteService;
import com.travel.travelserver.service.HistoryService;
import com.travel.travelserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final FavoriteService favoriteService;
    private final HistoryService historyService;
    private final Map<String, UserProfileVO> profileStore = new ConcurrentHashMap<>();
    private final Map<String, UserSettingsVO> settingsStore = new ConcurrentHashMap<>();

    @Override
    public UserProfileVO getProfile(String clientId) {
        UserProfileVO profile = profileStore.computeIfAbsent(clientId, this::defaultProfile);
        profile.setFavoriteCount(favoriteService.count(clientId));
        profile.setHistoryCount(historyService.count(clientId));
        return profile;
    }

    @Override
    public boolean updateProfile(String clientId, UserProfileUpdateRequest request) {
        UserProfileVO profile = profileStore.computeIfAbsent(clientId, this::defaultProfile);
        if (StringUtils.hasText(request.getNickname())) {
            profile.setNickname(request.getNickname().trim());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            profile.setAvatar(request.getAvatar().trim());
        }
        return true;
    }

    @Override
    public UserSettingsVO getSettings(String clientId) {
        return settingsStore.computeIfAbsent(clientId, ignored -> new UserSettingsVO(true, "default", "light", true));
    }

    @Override
    public boolean updateSettings(String clientId, UserSettingsRequest request) {
        UserSettingsVO settings = settingsStore.computeIfAbsent(clientId, ignored -> new UserSettingsVO(true, "default", "light", true));
        if (request.getStreamEnabled() != null) {
            settings.setStreamEnabled(request.getStreamEnabled());
        }
        if (StringUtils.hasText(request.getDefaultModel())) {
            settings.setDefaultModel(request.getDefaultModel().trim());
        }
        if (StringUtils.hasText(request.getTheme())) {
            settings.setTheme(request.getTheme().trim());
        }
        if (request.getCacheEnabled() != null) {
            settings.setCacheEnabled(request.getCacheEnabled());
        }
        return true;
    }

    private UserProfileVO defaultProfile(String clientId) {
        return new UserProfileVO(
                clientId,
                "旅行用户",
                "https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg",
                0,
                0
        );
    }
}
