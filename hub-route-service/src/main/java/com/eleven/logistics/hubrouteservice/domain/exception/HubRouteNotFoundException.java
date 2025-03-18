package com.eleven.logistics.hubrouteservice.domain.exception;

public class HubRouteNotFoundException extends RuntimeException {
    public HubRouteNotFoundException(String message) {
        super(message);
    }
}