package com.travel.travelserver.service;

import com.travel.travelserver.common.PageResult;
import com.travel.travelserver.model.vo.HistoryVO;

public interface HistoryService {
    PageResult<HistoryVO> pageHistory(String clientId, int page, int pageSize);

    void addHistory(String clientId, String type, String title, String summary);

    boolean clearHistory(String clientId);

    long count(String clientId);
}
