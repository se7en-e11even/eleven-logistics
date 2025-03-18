package com.eleven.logistics.hubrouteservice.application.service.external;

import com.eleven.logistics.hubrouteservice.application.dto.MapDto;

public interface RouteService {
    MapDto getRoute(String origin, String destination);
}
