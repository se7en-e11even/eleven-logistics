package com.eleven.logistics.slack.application.slackdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageResponse {

  private UUID id;

  @JsonProperty("주문자")
  private String supplyUsername;

  @JsonProperty("발송지")
  private String departureHubId; // 발송지

  @JsonProperty("경유지")
  private List<String> destinationHubId; // 경유지

  @JsonProperty("도착지 주소")
  private String deliveryAddress; // 도착지 주소

  @JsonProperty("담당자")
  private String companyDeliveryManagerId; // 담당자

  @JsonProperty("요청 사항")
  private String request;

  @JsonProperty("상품 정보")
  private String productName;
}
