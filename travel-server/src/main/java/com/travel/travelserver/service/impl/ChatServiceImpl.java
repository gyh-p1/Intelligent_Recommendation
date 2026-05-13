package com.travel.travelserver.service.impl;

import com.travel.travelserver.model.dto.ChatRequest;
import com.travel.travelserver.service.ChatService;
import com.travel.travelserver.service.HistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final HistoryService historyService;

    @Override
    public SseEmitter streamChat(ChatRequest request, String clientId) {
        SseEmitter emitter = new SseEmitter(60_000L);
        CompletableFuture.runAsync(() -> doStream(emitter, request, clientId));
        return emitter;
    }

    private void doStream(SseEmitter emitter, ChatRequest request, String clientId) {
        String sessionId = StringUtils.hasText(request.getSessionId())
                ? request.getSessionId()
                : "chat_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String messageId = "msg_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        try {
            send(emitter, "start", Map.of("sessionId", sessionId, "messageId", messageId));
            String answer = buildAnswer(request);
            for (String chunk : split(answer, 12)) {
                send(emitter, "message", Map.of("content", chunk));
            }
            historyService.addHistory(clientId, "chat", "AI 对话：" + limit(request.getMessage(), 40), answer);
            send(emitter, "done", Map.of("sessionId", sessionId, "messageId", messageId, "finishReason", "stop"));
            emitter.complete();
        } catch (Exception exception) {
            log.error("SSE chat failed", exception);
            try {
                send(emitter, "error", Map.of("code", 503, "message", "AI chat service is temporarily unavailable."));
            } catch (IOException ignored) {
                log.debug("Failed to send SSE error event", ignored);
            }
            emitter.completeWithError(exception);
        }
    }

    private String buildAnswer(ChatRequest request) {
        String city = StringUtils.hasText(request.getCity()) ? request.getCity() : detectCity(request.getMessage());
        return "可以的。建议你把%s行程拆成“核心景点 + 本地美食 + 轻量休息”三段：上午安排人气景点，下午选择博物馆、街区或公园，晚上体验夜景和本地餐饮。"
                .formatted(city)
                + " 如果预算有限，优先选择公共交通和免费城市地标，把门票预算留给最想去的 1 到 2 个景点。";
    }

    private String detectCity(String message) {
        for (String city : List.of("杭州", "北京", "上海", "成都", "西安", "南京", "苏州", "重庆", "广州", "深圳")) {
            if (message.contains(city)) {
                return city;
            }
        }
        return "这座城市";
    }

    private void send(SseEmitter emitter, String eventName, Object data) throws IOException {
        emitter.send(SseEmitter.event().name(eventName).data(data, MediaType.APPLICATION_JSON));
    }

    private java.util.List<String> split(String value, int chunkSize) {
        java.util.List<String> chunks = new java.util.ArrayList<>();
        for (int index = 0; index < value.length(); index += chunkSize) {
            chunks.add(value.substring(index, Math.min(index + chunkSize, value.length())));
        }
        return chunks;
    }

    private String limit(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
