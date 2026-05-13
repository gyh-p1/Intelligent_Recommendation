package com.travel.travelserver.controller;

import com.travel.travelserver.common.ClientIdUtils;
import com.travel.travelserver.common.PageResult;
import com.travel.travelserver.common.Result;
import com.travel.travelserver.model.vo.HistoryVO;
import com.travel.travelserver.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/history")
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping
    public Result<PageResult<HistoryVO>> getHistory(
            @RequestHeader(value = "X-Client-Id", required = false) String clientId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(historyService.pageHistory(ClientIdUtils.resolve(clientId), page, pageSize));
    }

    @DeleteMapping
    public Result<Boolean> clearHistory(@RequestHeader(value = "X-Client-Id", required = false) String clientId) {
        return Result.success(historyService.clearHistory(ClientIdUtils.resolve(clientId)));
    }
}
