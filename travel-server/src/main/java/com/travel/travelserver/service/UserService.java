package com.travel.travelserver.service;

import com.travel.travelserver.model.dto.UserProfileUpdateRequest;
import com.travel.travelserver.model.dto.UserSettingsRequest;
import com.travel.travelserver.model.vo.UserProfileVO;
import com.travel.travelserver.model.vo.UserSettingsVO;

public interface UserService {
    UserProfileVO getProfile(String clientId);

    boolean updateProfile(String clientId, UserProfileUpdateRequest request);

    UserSettingsVO getSettings(String clientId);

    boolean updateSettings(String clientId, UserSettingsRequest request);
}
