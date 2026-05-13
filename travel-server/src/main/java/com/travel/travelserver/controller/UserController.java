package com.travel.travelserver.controller;

import com.travel.travelserver.common.ClientIdUtils;
import com.travel.travelserver.common.Result;
import com.travel.travelserver.model.dto.UserProfileUpdateRequest;
import com.travel.travelserver.model.dto.UserSettingsRequest;
import com.travel.travelserver.model.vo.UserProfileVO;
import com.travel.travelserver.model.vo.UserSettingsVO;
import com.travel.travelserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public Result<UserProfileVO> getProfile(@RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Result.success(userService.getProfile(ClientIdUtils.resolve(clientId)));
    }

    @PutMapping("/profile")
    public Result<Boolean> updateProfile(
            @RequestHeader(value = "X-Client-Id", required = false) String clientId,
            @RequestBody @Valid UserProfileUpdateRequest request
    ) {
        return Result.success(userService.updateProfile(ClientIdUtils.resolve(clientId), request));
    }

    @GetMapping("/settings")
    public Result<UserSettingsVO> getSettings(@RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Result.success(userService.getSettings(ClientIdUtils.resolve(clientId)));
    }

    @PutMapping("/settings")
    public Result<Boolean> updateSettings(
            @RequestHeader(value = "X-Client-Id", required = false) String clientId,
            @RequestBody @Valid UserSettingsRequest request
    ) {
        return Result.success(userService.updateSettings(ClientIdUtils.resolve(clientId), request));
    }
}
