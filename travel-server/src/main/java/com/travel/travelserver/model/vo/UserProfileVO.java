package com.travel.travelserver.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileVO {
    private String userId;
    private String nickname;
    private String avatar;
    private long favoriteCount;
    private long historyCount;
}
