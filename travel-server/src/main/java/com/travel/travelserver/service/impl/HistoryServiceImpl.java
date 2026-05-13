package com.travel.travelserver.service.impl;

import com.travel.travelserver.common.PageResult;
import com.travel.travelserver.model.vo.HistoryVO;
import com.travel.travelserver.service.HistoryService;
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
public class HistoryServiceImpl implements HistoryService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int MAX_HISTORY_SIZE = 100;

    private final Map<String, List<HistoryVO>> historyStore = new ConcurrentHashMap<>();

    @Override
    public PageResult<HistoryVO> pageHistory(String clientId, int page, int pageSize) {
        List<HistoryVO> history = new ArrayList<>(historyStore.getOrDefault(clientId, List.of()));
        history.sort(Comparator.comparing(HistoryVO::getCreatedAt).reversed());
        return PageResult.of(history, page, pageSize);
    }

    @Override
    public void addHistory(String clientId, String type, String title, String summary) {
        HistoryVO item = new HistoryVO(
                "his_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12),
                type,
                limit(title, 100),
                limit(summary, 300),
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
        List<HistoryVO> history = historyStore.computeIfAbsent(clientId, ignored -> new ArrayList<>());
        history.add(item);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }

    @Override
    public boolean clearHistory(String clientId) {
        historyStore.remove(clientId);
        return true;
    }

    @Override
    public long count(String clientId) {
        return historyStore.getOrDefault(clientId, List.of()).size();
    }

    private String limit(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
