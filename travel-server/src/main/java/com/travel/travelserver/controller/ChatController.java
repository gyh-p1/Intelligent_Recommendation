package com.travel.travelserver.controller;

import com.travel.travelserver.common.ClientIdUtils;
import com.travel.travelserver.common.Result;
import com.travel.travelserver.model.dto.ChatRequest;
import com.travel.travelserver.service.ChatService;
import com.travel.travelserver.service.TravelDataService;
import com.travel.travelserver.service.impl.RateLimitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel/chat")
public class ChatController {
    private final TravelDataService travelDataService;
    private final ChatService chatService;
    private final RateLimitService rateLimitService;

    @GetMapping("/quick-questions")
    public Result<List<String>> getQuickQuestions() {
        return Result.success(travelDataService.getQuickQuestions());
    }

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(
            @RequestHeader(value = "X-Client-Id", required = false) String clientId,
            @RequestBody @Valid ChatRequest request
    ) {
        String safeClientId = ClientIdUtils.resolve(clientId);
        rateLimitService.check("chat", safeClientId, 20);
        return chatService.streamChat(request, safeClientId);
    }
}
