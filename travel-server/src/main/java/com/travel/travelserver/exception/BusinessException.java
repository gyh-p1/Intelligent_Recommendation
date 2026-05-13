package com.travel.travelserver.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(400, message);
    }

    public static BusinessException tooManyRequests() {
        return new BusinessException(429, "Requests are too frequent. Please try again later.");
    }
}
