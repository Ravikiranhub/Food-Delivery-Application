package com.foodhub.exception;

public class RestaurantNotAvailableException extends RuntimeException {
    public RestaurantNotAvailableException(String message) {
        super(message);
    }
}

