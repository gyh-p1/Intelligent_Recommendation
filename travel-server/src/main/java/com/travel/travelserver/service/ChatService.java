package com.travel.travelserver.service;

import com.travel.travelserver.model.dto.ChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatService {
    SseEmitter streamChat(ChatRequest request, String clientId);
}
