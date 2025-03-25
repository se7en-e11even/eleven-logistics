package com.eleven.logistics.slack.application.external;

import com.eleven.logistics.slack.application.deliverydto.DeliveryResponse;
import com.eleven.logistics.slack.application.deliverydto.DeliveryRouteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {

    DeliveryResponse getDelivery(@PathVariable UUID deliveryId);


    ResponseEntity<List<DeliveryRouteResponse>> getDeliveryRoutes(@PathVariable UUID deliveryId);
}
