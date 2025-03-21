package com.eleven.logistics.slack.application.service;

import com.eleven.logistics.slack.application.dto.PageResponseDto;
import com.eleven.logistics.slack.application.dto.SlackDto;
import com.eleven.logistics.slack.application.dto.SlackMessageResponse;
import com.eleven.logistics.slack.domain.config.SlackClient;
import com.eleven.logistics.slack.domain.entity.Slack;
import com.eleven.logistics.slack.domain.repository.SlackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SlackService {

    private final SlackRepository slackRepository;

    private final SlackClient slackClient;

    @Value("${gemini.api.key}")
    private String gemini_key;

    @Value("${gemini.api.url}")
    private String gemini_url;

    @Transactional
    public SlackMessageResponse sendMessageToUser(SlackDto slackDto, String username) {
        try {
            String userId = slackClient.getUserIdByName(slackDto.getUsername());

            if (userId == null) {
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다");
            }

            // 기존 데이터 조회 로직 (생략)
            ApiResponseDto<PageResponseDto<CompanyResponseDto>> companyAll = hubService.findByAllCompany();
            ApiResponseDto<PageResponseDto<HubResponseDto>> hubAll = hubService.findByAllHub();
            PageResponseDto<DeliveryResponse> deliveryAll = deliveryService.searchDeliveries();
            PageResponseDto<DeliveryPersonResponse> deliveryPersonAll = deliveryService.searchDeliveryPersons();

            List<String> deliveryPersonName = deliveryPersonAll.getContent().stream()
                    .filter(deliveryPerson -> deliveryPerson.getUsername().equals(slackDto.getUsername()))
                    .map(DeliveryPersonResponse::getUsername)
                    .toList();

            Map<UUID, String> companyUsername = companyAll.getData().getContent().stream()
                    .collect(Collectors.toMap(CompanyResponseDto::getId, CompanyResponseDto::getUsername));

            Map<UUID, String> hubAddress = hubAll.getData().getContent().stream()
                    .collect(Collectors.toMap(HubResponseDto::getId, HubResponseDto::getAddress));

            List<UUID> deliveryIds = deliveryAll.getContent().stream()
                    .filter(person -> deliveryPersonName.stream()
                            .anyMatch(name -> name.equals(person.getReceiver())))
                    .map(DeliveryResponse::getId)
                    .toList();

            List<DeliveryResponse> deliveries = deliveryAll.getContent().stream()
                    .filter(receiver -> receiver.getReceiver().equals(slackDto.getUsername()))
                    .toList();

            List<DeliveryResponse> orderID = deliveries.stream()
                    .filter(orderId -> orderId.getOrderId() != null)
                    .toList();

            List<UUID> ordered = orderID.stream().map(DeliveryResponse::getOrderId).toList();
            List<ResponseEntity<FindOrderQuery>> orderDetails = new ArrayList<>();
            for (UUID orderId : ordered) {
                ResponseEntity<FindOrderQuery> orderDetail = orderService.read(orderId);
                orderDetails.add(orderDetail);
            }

            List<UUID> supplyIds = orderDetails.stream()
                    .map(ResponseEntity::getBody)
                    .map(body -> body.supplyId())
                    .filter(Objects::nonNull)
                    .toList();

            List<String> supplyUsername = supplyIds.stream()
                    .map(companyUsername::get)
                    .filter(Objects::nonNull)
                    .toList();

            SlackMessageResponse returnMessage = null;
            List<MessageResponse> messageResponses = new ArrayList<>();

            // Gemini API 설정
            RestTemplate restTemplate = new RestTemplate();
            String geminiApiUrl = gemini_url + gemini_key;

            if (!deliveryIds.isEmpty()) {
                for (UUID deliveryId : deliveryIds) {
                    DeliveryResponse deliveryDetail = deliveryService.getDelivery(deliveryId);
                    List<DeliveryRouteResponse> route = deliveryService.getDeliveryRoutes(deliveryId);

                    List<UUID> departureId = route.stream()
                            .map(DeliveryRouteResponse::getDepartureHubId)
                            .toList();

                    List<String> departure = departureId.stream()
                            .map(hubAddress::get)
                            .filter(Objects::nonNull)
                            .toList();

                    List<String> deliveryRoute = route.stream()
                            .flatMap(r -> Stream.of(hubAddress.get(r.getDepartureHubId()), hubAddress.get(r.getArrivalHubId())))
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList();

                    DeliveryResponse deliveryRes = deliveryDetail;

                    if (deliveryRes.getReceiver().equals(slackDto.getUsername())) {
                        Map<UUID, List<FindOrderProductQuery>> ordersGroupedById = orderDetails.stream()
                                .map(ResponseEntity::getBody)
                                .collect(Collectors.toMap(
                                        FindOrderQuery::orderId,
                                        FindOrderQuery::orderProductDtoList,
                                        (existing, newItems) -> {
                                            existing.addAll(newItems);
                                            return existing;
                                        }
                                ));

                        messageResponses = ordersGroupedById.entrySet().stream()
                                .map(entry -> {
                                    UUID orderId = entry.getKey();
                                    List<FindOrderProductQuery> orderProducts = entry.getValue();

                                    String request = orderDetails.stream()
                                            .map(ResponseEntity::getBody)
                                            .filter(order -> order.orderId().equals(orderId))
                                            .findFirst()
                                            .map(FindOrderQuery::request)
                                            .orElse("");

                                    String productDetails = orderProducts.stream()
                                            .map(product -> {
                                                UUID productId = product.productId();
                                                FindProductQuery productInfo = productService.read(productId);
                                                String productName = productInfo != null ? productInfo.name() : "";
                                                return productName + " " + product.quantity() + "개";
                                            })
                                            .collect(Collectors.joining(", "));

                                    return new MessageResponse(
                                            deliveryRes.getId(),
                                            supplyUsername.isEmpty() ? "" : supplyUsername.get(0),
                                            departure.isEmpty() ? "" : departure.get(0),
                                            deliveryRoute.size() > 1 ? deliveryRoute.subList(1, deliveryRoute.size()) : List.of(),
                                            deliveryRes.getDeliveryAddress(),
                                            deliveryPersonName.isEmpty() ? "" : deliveryPersonName.get(0),
                                            request,
                                            productDetails
                                    );
                                })
                                .toList();

                        for (MessageResponse messageResponse : messageResponses) {
                            // 기존 메시지 포맷
                            String rawMessage = String.format(
                                    "\n 주문 정보 \n━━━━━━━━━━━━━━━\n" +
                                            " 주문 번호 : %s\n" +
                                            " 주문자 : %s\n" +
                                            " 발송지 : %s\n" +
                                            " 경유지 : %s\n" +
                                            " 배송지 주소 : %s\n" +
                                            " 담당자 : %s\n" +
                                            " 요청 사항 : %s\n" +
                                            " 상품 정보 : %s\n" +
                                            "━━━━━━━━━━━━━━━",
                                    messageResponse.getId(),
                                    messageResponse.getSupplyUsername(),
                                    messageResponse.getDepartureHubId(),
                                    messageResponse.getDestinationHubId(),
                                    messageResponse.getDeliveryAddress(),
                                    messageResponse.getCompanyDeliveryManagerId(),
                                    messageResponse.getRequest(),
                                    messageResponse.getProductName()
                            );

                            // Gemini API 요청 본문 구성
                            Map<String, Object> geminiRequest = new HashMap<>();
                            geminiRequest.put("contents", new HashMap<String, Object>() {{
                                put("parts", new HashMap<String, String>() {{
                                    put("text", "발송지, 경유지, 도착지를 참고하여 메시지를 발송하는 시각을 기준으로 최종 발송 시한을 계산해 추가하고," +
                                            "주문 번호는 짧은 번호로 변환한 뒤, 기존 메시지의 형태를 유지해 줘, 참고 사항이랑 추가적인 대답은 안해도 돼 :\n" + rawMessage);
                                }});
                            }});

                            // Gemini API 호출
                            Map<String, Object> geminiResponse = restTemplate.postForObject(geminiApiUrl, geminiRequest, Map.class);
                            String improvedMessage = slackClient.extractTextFromGeminiResponse(geminiResponse);

                            // Slack으로 전송
                            slackClient.sendMessage(userId, improvedMessage);

                            // Slack 저장소에 메시지 저장
                            Slack slack = Slack.create(slackDto.getUsername(), improvedMessage);
                            slack.setCreatedBy(username);
                            slackRepository.save(slack);

                            returnMessage = SlackMessageResponse.of(slack);
                        }
                    } else {
                        throw new IllegalArgumentException("메시지를 보낼 담당자가 존재하지 않습니다.");
                    }
                }
                return returnMessage;
            } else {
                throw new IllegalArgumentException("전송할 메시지가 없습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("메시지 전송 중 오류가 발생했습니다", e);
        }
    }

    @Transactional(readOnly = true)
    public SlackMessageResponse findBySlackId(UUID slackId) {
        Slack slack = slackRepository.findById(slackId)
                .orElseThrow(()->new IllegalArgumentException("찾으시는 메시지가 없습니다."));

        return SlackMessageResponse.of(slack);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "findByAll", key = "#page+'-'+#size")
    public PageResponseDto<SlackMessageResponse> findByAll(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Slack> slack = slackRepository.findByDeletedAtIsNull(pageable);

        if (slack.isEmpty()){
            throw new IllegalArgumentException("메시지가 존재하지 않습니다.");
        }
        Page<SlackMessageResponse> pageDto = slack.map(SlackMessageResponse::of);

        return PageResponseDto.of(pageDto);
    }

    @Transactional
    public SlackMessageResponse updateSlackMessage(UUID slackId, SlackDto dto, String username) {
        Slack slack = slackRepository.findById(slackId)
                .orElseThrow(()->new IllegalArgumentException("찾으시는 메시지가 없습니다."));
        try {
            String userId = slackClient.getUserIdByName(dto.getUsername());

            if (userId == null) {
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다");
            }

            // Gemini API 설정
            RestTemplate restTemplate = new RestTemplate();
            String geminiApiUrl = gemini_url + gemini_key;

            // Gemini API 요청 본문 구성
            Map<String, Object> geminiRequest = new HashMap<>();
            geminiRequest.put("contents", new HashMap<String, Object>() {{
                put("parts", new HashMap<String, String>() {{
                    put("text", "발송지, 경유지, 도착지를 참고하여 메시지를 발송하는 시각을 기준으로 최종 발송 시한을 계산해 추가하고," +
                            "주문 번호는 짧은 번호로 변환한 뒤, 기존 메시지의 형태를 유지해 줘, 참고 사항이랑 추가적인 대답은 안해도 돼 :\n" + dto.getMessage());
                }});
            }});
            // Gemini API 호출
            Map<String, Object> geminiResponse = restTemplate.postForObject(geminiApiUrl, geminiRequest, Map.class);
            String improvedMessage = slackClient.extractTextFromGeminiResponse(geminiResponse);
            slackClient.sendMessage(userId, improvedMessage);

            slack.update(dto.getUsername(), improvedMessage);
            slack.getUpdatedBy(username);

        } catch (Exception e) {
            throw new RuntimeException("메시지 전송 중 오류가 발생했습니다", e);
        }
        return SlackMessageResponse.of(slack);
    }

    @Transactional
    public void deleteSlackMessage(UUID slackId, String username) {
        Slack slack = slackRepository.findById(slackId)
                .orElseThrow(()->new IllegalArgumentException("찾으시는 메시지가 없습니다."));

        slack.delete(username);
    }
}