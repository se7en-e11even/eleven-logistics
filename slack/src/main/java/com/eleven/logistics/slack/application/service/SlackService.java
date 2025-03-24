package com.eleven.logistics.slack.application.service;

import com.eleven.logistics.common.dto.ApiResponseDto;
import com.eleven.logistics.slack.application.companydto.CompanyResponseDto;
import com.eleven.logistics.slack.application.deliverydto.DeliveryPersonResponse;
import com.eleven.logistics.slack.application.deliverydto.DeliveryResponse;
import com.eleven.logistics.slack.application.deliverydto.DeliveryRouteResponse;
import com.eleven.logistics.slack.application.dto.PageResponseDto;
import com.eleven.logistics.slack.application.external.DeliveryService;
import com.eleven.logistics.slack.application.external.HubService;
import com.eleven.logistics.slack.application.external.OrderService;
import com.eleven.logistics.slack.application.external.ProductService;
import com.eleven.logistics.slack.application.hubdto.HubResponseDto;
import com.eleven.logistics.slack.application.querydto.FindOrderQuery;
import com.eleven.logistics.slack.application.querydto.FindProductQuery;
import com.eleven.logistics.slack.application.slackdto.MessageResponse;
import com.eleven.logistics.slack.application.slackdto.SlackDto;
import com.eleven.logistics.slack.application.slackdto.SlackMessageResponse;
import com.eleven.logistics.slack.domain.config.SlackClient;
import com.eleven.logistics.slack.domain.entity.Slack;
import com.eleven.logistics.slack.domain.repository.SlackRepository;
import com.eleven.logistics.slack.infrastructure.feign.config.JpaAuditorAware;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class SlackService {
    private final SlackRepository slackRepository;

    private final SlackClient slackClient;

    private final OrderService orderService;
    private final DeliveryService deliveryService;
    private final HubService hubService;
    private final ProductService productService;
    private final JpaAuditorAware jpaAuditorAware;

    @Value("${gemini.api.key}")
    private String gemini_key;

    @Value("${gemini.api.url}")
    private String gemini_url;

    @Transactional
    public SlackMessageResponse sendMessageRabbitMQ(String username, UUID deliveryId) {
        return sendMessageToUser(username, deliveryId);
    }

    @Transactional
    public SlackMessageResponse sendMessageSlackController(String username) {
        return sendMessageToUser(username, null);
    }


    @Transactional
    public SlackMessageResponse sendMessageToUser(String slackUsername, UUID deliveryId) {
        try {
            // Slack 사용자 ID 조회
            String userId = slackClient.getUserIdByName(slackUsername);
            if (userId == null) {
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
            }

            // 배송 정보 조회
            DeliveryResponse delivery = deliveryService.getDelivery(deliveryId);
            // TODO : username, role 하드코딩 X
            ResponseEntity<FindOrderQuery> orderResponse = orderService.read(delivery.getOrderId(), "testuser", "MASTER");

            // Slack 사용자 검증
            if (!delivery.getReceiverSnsId().equals(slackUsername)) {
                throw new IllegalArgumentException("메시지를 보낼 담당자가 존재하지 않습니다.");
            }

            // 주문 및 허브 정보 추출
            FindOrderQuery order = orderResponse.getBody();
            ResponseEntity<ApiResponseDto<HubResponseDto>> startHub = hubService.findByHubId(delivery.getDepartureHubId());
            ResponseEntity<ApiResponseDto<HubResponseDto>> endHub = hubService.findByHubId(delivery.getDestinationHubId());

            // 배송 경로 설정
            List<String> deliveryRoute = Stream.of(startHub.getBody().getData().getAddress(), endHub.getBody().getData().getAddress())
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            String currentUser = jpaAuditorAware.getCurrentAuditor().orElse("unknown");

            // 메시지 객체 생성
            MessageResponse message = new MessageResponse();
            message.setId(order.orderId());
            message.setRequest(order.request());
            message.setDepartureHubId(deliveryRoute.get(0)); // 출발지
            message.setDeliveryAddress(delivery.getDeliveryAddress());
            message.setProductName(""); // 필요 시 orderProductQueryList에서 추출 가능
            message.setDestinationHubId(deliveryRoute); // 전체 경로
            message.setSupplyUsername(order.supplyId().toString());
            message.setCompanyDeliveryManagerId(currentUser);

            // 메시지 포맷팅
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
                    message.getId(),
                    message.getSupplyUsername(),
                    message.getDepartureHubId(),
                    message.getDestinationHubId(),
                    message.getDeliveryAddress(),
                    message.getCompanyDeliveryManagerId(),
                    message.getRequest(),
                    message.getProductName()
            );

            // Gemini API 요청 본문 구성
            Map<String, Object> geminiRequest = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            Map<String, String> parts = new HashMap<>();
            parts.put("text", "발송지, 경유지, 도착지를 참고하여 메시지를 발송하는 시각을 기준으로 최종 발송 시한을 계산해 추가하고," +
                    "주문 번호는 짧은 번호로 변환한 뒤, 기존 메시지의 형태를 유지해 줘:\n" + rawMessage);
            contents.put("parts", parts);
            geminiRequest.put("contents", contents);

            // Gemini API 호출 및 메시지 개선
            RestTemplate restTemplate = new RestTemplate();
            String geminiApiUrl = gemini_url + gemini_key;
            Map<String, Object> geminiResponse = restTemplate.postForObject(geminiApiUrl, geminiRequest, Map.class);
            String improvedMessage = slackClient.extractTextFromGeminiResponse(geminiResponse);

            // Slack으로 전송
            slackClient.sendMessage(userId, improvedMessage);

            // Slack 메시지 저장
            Slack slack = Slack.create(slackUsername, improvedMessage);
            slack.setCreatedBy(currentUser);
            slackRepository.save(slack);

            return SlackMessageResponse.of(slack);
        } catch (IllegalArgumentException e) {
            throw e; // 사용자 정의 예외는 그대로 전달
        } catch (Exception e) {
            throw new RuntimeException("메시지 전송 중 오류가 발생했습니다.", e);
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
            slack.setUpdatedBy(username);

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