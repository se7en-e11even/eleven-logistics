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

            ApiResponseDto<PageResponseDto<CompanyResponseDto>> companyAll = hubService.findByAllCompany();
            ApiResponseDto<PageResponseDto<HubResponseDto>> hubAll = hubService.findByAllHub();
            PageResponseDto<DeliveryResponse> deliveryAll = deliveryService.searchDeliveries();
            PageResponseDto<DeliveryPersonResponse> deliveryPersonAll = deliveryService.searchDeliveryPersons();

            /**
             업체를 전체 조회해서 각자 업체를 담당하는 사용자의 이름을 id와 Map 자료구조로 묶음
             **/
            Map<UUID, String> companyUsername = companyAll.getData().getContent().stream()
                    .collect(Collectors.toMap(CompanyResponseDto::getId, CompanyResponseDto::getUsername));


            /**
             허브를 전체 조회해서 각긱 허브의 주소와 id를 Map 자료구조로 묶음
             **/
            Map<UUID, String> hubAddress = hubAll.getData().getContent().stream()
                    .collect(Collectors.toMap(HubResponseDto::getId, HubResponseDto::getAddress));

            /**
             전체 배송을 조회해서 슬렉에서 가져온 사용자 이름과 비교해서 배송 응답 객체를 만듦
             **/
            List<DeliveryResponse> deliveries = deliveryAll.getContent().stream()
                    .filter(receiver -> receiver.getReceiver().equals(slackDto.getUsername()))
                    .toList();

            /**
             전체 배송 응답 객체에서 orderId (FK) 가 존재하는 배송 응답 객체를 뽑음
             **/
            List<DeliveryResponse> orderID = deliveries.stream()
                    .filter(orderId -> orderId.getOrderId() != null)
                    .toList();

            /**
             배송 응답 객체의 orderId 만 뽑음
             **/
            List<UUID> ordered = orderID.stream().map(DeliveryResponse::getOrderId).toList();

            /**
             배송 응답 객체의 orderId 로 order 단일 조회를 시도하여 해당 order 의 상세 정보를 저장할 빈 리스트를 만들고
             찾아온 order 데이터를 추가함.
             **/
            List<ResponseEntity<FindOrderQuery>> orderDetails = new ArrayList<>();
            for (UUID orderId : ordered) {
                ResponseEntity<FindOrderQuery> orderDetail = orderService.read(orderId);
                orderDetails.add(orderDetail);
            }

            /**
             색출이 된 order 데이터에서 발송자의 id 를 뽑아옴
             **/
            List<UUID> supplyIds = orderDetails.stream()
                    .map(ResponseEntity::getBody)
                    .map(body -> body.supplyId())
                    .filter(Objects::nonNull)
                    .toList();

            /**
             발송자의 아이디에서 사용자의 이름을 뽑아옴.
             **/
            List<String> supplyUsername = supplyIds.stream()
                    .map(companyUsername::get)
                    .filter(Objects::nonNull)
                    .toList();

            /**
             최종 메시지를 담을 메시지 객체를 생성
             **/
            SlackMessageResponse returnMessage = null;

            /**
             생성된 메시지를 담을 빈 리스트를 생성
             **/
            List<MessageResponse> messageResponses = new ArrayList<>();

            /**
             Gemini API 설정
             **/
            RestTemplate restTemplate = new RestTemplate();
            String geminiApiUrl = gemini_url + gemini_key;

            /**
             메시지를 요청할 때 담당자 이름과 실제로 배송 담당자의 이름이 같은 사람을 뽑아옴
             **/
            List<String> deliveryPersonName = deliveryPersonAll.getContent().stream()
                    .filter(deliveryPerson -> deliveryPerson.getUsername().equals(slackDto.getUsername()))
                    .map(DeliveryPersonResponse::getUsername)
                    .toList();

            /**
             배송 정보를 전체 조회할 때, 슬렉에서 가져온 사용자 이름과 담당자의 이름을 비교한 deliveryPersonName 으로
             배송 담당자의 receiver 와 비교해서 매칭이 되는 deliveryId 를 뽑아옴
             **/
            List<UUID> deliveryIds = deliveryAll.getContent().stream()
                    .filter(person -> deliveryPersonName.stream()
                            .anyMatch(name -> name.equals(person.getReceiver())))
                    .map(DeliveryResponse::getId)
                    .toList();

            /**
             색출한 deliveryId 가 비어있지 않다면, deliveryId 를 하나씩 뽑아서 delivery 단일 조회와, 배송 경로를 조회한다.
             **/
            if (!deliveryIds.isEmpty()) {
                for (UUID deliveryId : deliveryIds) {
                    DeliveryResponse deliveryDetail = deliveryService.getDelivery(deliveryId);
                    List<DeliveryRouteResponse> route = deliveryService.getDeliveryRoutes(deliveryId);

                    /**
                     배송 경로의 허브 id 를 뽑아옴
                     **/
                    List<UUID> departureId = route.stream()
                            .map(DeliveryRouteResponse::getDepartureHubId)
                            .toList();

                    /**
                     배송 경로의 허브 주소를 뽑아옴
                     **/
                    List<String> departure = departureId.stream()
                            .map(hubAddress::get)
                            .filter(Objects::nonNull)
                            .toList();

                    /**
                     허브의 실제 주소를 조회해서 배송 경로의 출발, 도착 허브를 각각 실제 주소로 뽑아옴
                     **/
                    List<String> deliveryRoute = route.stream()
                            .flatMap(r -> Stream.of(hubAddress.get(r.getDepartureHubId()), hubAddress.get(r.getArrivalHubId())))
                            .filter(Objects::nonNull)
                            .distinct()
                            .toList();

                    /**
                     색출한 delivery 의 담당자와 슬렉에서 가져온 사용자 이름이 같다면,
                     색출한 order 데이터에서 id (PK)와 orderProduct 를 Map 자료구조로 묶,
                     만약 이미 존재하는 주문 정보가 있다면 모든 요소를 추가함.
                     **/
                    if (deliveryDetail.getReceiver().equals(slackDto.getUsername())) {
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

                        /**
                         Map 자료구조로 묶은 order 정보를 실제 상품 정보를 조회하기 위해 사용
                         이후 메시지를 생성하고 Gemini 연동하는 기능을 수행합니다.
                         **/
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
                                            deliveryDetail.getId(),
                                            supplyUsername.isEmpty() ? "" : supplyUsername.get(0),
                                            departure.isEmpty() ? "" : departure.get(0),
                                            deliveryRoute.size() > 1 ? deliveryRoute.subList(1, deliveryRoute.size()) : List.of(),
                                            deliveryDetail.getDeliveryAddress(),
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