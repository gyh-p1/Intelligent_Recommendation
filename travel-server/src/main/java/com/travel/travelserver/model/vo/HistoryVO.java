package com.travel.travelserver.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryVO {
    private String historyId;
    private String type;
    private String title;
    private String summary;
    private String createdAt;
}
