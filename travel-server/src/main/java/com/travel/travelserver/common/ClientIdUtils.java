package com.travel.travelserver.common;

import org.springframework.util.StringUtils;

public final class ClientIdUtils {
    private ClientIdUtils() {
    }

    public static String resolve(String clientId) {
        if (!StringUtils.hasText(clientId)) {
            return "anonymous";
        }
        return clientId.trim();
    }
}
