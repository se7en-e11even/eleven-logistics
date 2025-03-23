package com.eleven.logistics.order.application.service;

import com.eleven.logistics.order.application.dto.command.CreateOrderCommand;
import com.eleven.logistics.order.application.dto.command.CreateOrderProductCommand;
import com.eleven.logistics.order.application.dto.command.UpdateOrderCommand;
import com.eleven.logistics.order.application.dto.message.OrderMessage;
import com.eleven.logistics.order.application.dto.query.*;
import com.eleven.logistics.order.application.port.out.*;
import com.eleven.logistics.order.domain.entity.Order;
import com.eleven.logistics.order.domain.entity.OrderProduct;
import com.eleven.logistics.order.domain.exception.CustomException;
import com.eleven.logistics.order.domain.repository.OrderRepository;
import com.eleven.logistics.order.domain.repository.OrderRepositoryCustom;
import com.eleven.logistics.order.domain.vo.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static com.eleven.logistics.order.domain.exception.OrderErrorCode.*;

@Slf4j(topic = "OrderService")
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    // Feign Client
    private final ProductPort productPort;
    private final CompanyPort companyPort;
    private final UserPort userPort;
    private final HubPort hubPort;

    private final RabbitMQBrokerPort rabbitMQBrokerPort;

    private final OrderRepository repository;
    private final OrderRepositoryCustom repositoryCustom;

    @CachePut(cacheNames = "orderRead", key = "{ #result.orderId, #role }")
    public FindOrderQuery create(CreateOrderCommand command, String username, String role) {
        // TODO: 주문 생성은 상태 변화를 활용하여 우선 생성하고 이후에 요청 응답의 결과에 따라 처리 한다.
        // 주문 요청이 들어오면 PENDING
        // 재고 확인 여부 : PENDING, CANCEL
        // 배송 정보 확인 : 접수됨?, 배송 중....

        // 배송에 전달할 수신인 username -> userId
//        FindUserQuery findUser = userPort.getUserByUsername(username);

        // username 의 companyId: 주문자의 회사 -> receiverId
        FindCompanyQuery.Company findCompany = companyPort.getCompanyByUsername(username).data();

        // feign client 요청 테스트, 상품 id를 통해 공급업체 id, 상품 재고를 알 수 있다.
        // supplyId -> 상품 id의 companyId, receiverId -> username 의 companyId
        // 주문 수량과 재고를 비교해야 한다.
        FindProductQuery findProduct = productPort.getProductByProductId(command.commandList().get(0).productId().toString());
        
        log.info("productDto = {}", findProduct);

        // 저장할 주문 엔티티 생성
        Order order = Order.builder()
                .supplyId(findProduct.companyId())
                .receiverId(findCompany.id())
                .orderStatus(OrderStatus.PENDING)
                .request(command.request())
                .orderProductList(new ArrayList<>())
                .build();

        // 주문 상품 추가
        for (CreateOrderProductCommand orderProductCommand : command.commandList()) {
            OrderProduct orderProduct = OrderProduct.builder()
                    .productId(orderProductCommand.productId())
                    .price(orderProductCommand.price())
                    .quantity(orderProductCommand.quantity())
                    .build();
            order.addOrderProduct(orderProduct);
        }
        repository.save(order);

        // message publish
        OrderMessage message = OrderMessage.of(
                order.getOrderId(),
                findProduct.hubId(),
                findCompany.hubId(),
                findCompany.address(),
"username", "slackSnsID"
//                findUser.username(),
//                findUser.slackAccount()
        );
        rabbitMQBrokerPort.publishMessage(message);
        return FindOrderQuery.from(order);
    }

    @Cacheable(cacheNames = "orderRead", key = "{ #orderId, #role }")
    @Transactional(readOnly = true)
    public FindOrderQuery read(UUID orderId, String username, String role) {
        FindOrderQuery findOrder = repositoryCustom.findById(orderId)
                .map(FindOrderQuery::from)
                .orElseThrow(()->new CustomException(ORDER_NOT_FOUND));

        checkAuthority(username, role, findOrder);

        return findOrder;
    }

    @CachePut(cacheNames = "orderRead", key = "{ #command.orderId, #role }")
    @CacheEvict(cacheNames = "orderSearch", allEntries = true)
    public void update(UpdateOrderCommand command, String username, String role) {
        Order order = repositoryCustom.findById(command.orderId())
                        .orElseThrow(()->new CustomException(ORDER_NOT_FOUND));

        // 허브 관리자는 담당 허브만, 공급업체의 hubId
        String supplyId = order.getSupplyId().toString();
        checkHubAuthority(username, role, supplyId);

        order.updateOf(
                command.request()
        );

        // TODO: updateDto 의 orderProductList 수정
    }


    // 주문 취소, 주문 상태를 CANCELED 로 변경하여 정보를 저장한다.
    public FindOrderQuery cancel(UUID orderId) {
        Order order = repositoryCustom.findById(orderId)
                        .orElseThrow(()->new CustomException(ORDER_NOT_FOUND));
        order.changeOrderStatus("CANCELED");
        return FindOrderQuery.from(order);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "orderRead", key = "{ #orderId, #role }"),
            @CacheEvict(cacheNames = "orderSearch", allEntries = true)
    })
    public void delete(UUID orderId, String username, String role) {
        Order order = repository.findByOrderId(orderId);

        // 허브 관리자는 담당 허브만 삭제, 공급업체의 hubId
        String supplyId = order.getSupplyId().toString();
        checkHubAuthority(username, role, supplyId);

        order.deleteOf(username);
    }

    // 주문한 상품 정보 삭제

    public void deleteOrderProducts(UUID orderId, UUID orderProductId) {
        // 주문 정보에서 주문상품 정보를 불러와서 ...
//        OrderProduct orderProduct;
//        orderProduct.deleteOf("user");
    }

    @Cacheable(
            cacheNames = "orderSearch",
            key = "{ #keyword, #pageable.pageNumber, #pageable.pageSize, #username }"
    )
    @Transactional(readOnly = true)
    public Page<FindOrderQuery> search(
            String keyword,
            Pageable pageable,
            String username,
            String role
    ) {
        // DB 를 조회하고 FeignClient 를 호출 해 자격 검증하는 것과
        // FeignClient 를 호출해 자격 조건으로 DB 를 조회하는 것 중 어느 것이 성능이 더 좋을까?

        return repositoryCustom.retrieve(keyword, pageable)
                .map(FindOrderQuery::from);
    }

    private Ids getCompanyIdAndHubId(String username) {
        // FeignClient 호출 companyPort, hubPort
        FindCompanyQuery company = companyPort.getCompanyByUsername(username);

        UUID companyId = Optional.ofNullable(company)
                .filter(c -> c.code() == 200)
                .map(c -> c.data().id())
                .orElseThrow(() -> new CustomException(COMPANY_NOT_FOUND));

        UUID hubId = Optional.of(company)
                .map(c -> c.data().hubId())
                .orElse(null); // hubId가 없어도 예외 처리 X

        if (hubId != null) {
            FindHubQuery hub = hubPort.getHubByHubId(hubId.toString(), 1, 1);
            if (hub == null || hub.code() != 200) {
                throw new CustomException(HUB_NOT_FOUND);
            }
        }
        return new Ids(companyId, hubId);
    }

    private record Ids(UUID companyId, UUID hubId) {}

    private void checkAuthority(String username, String role, FindOrderQuery findOrder) {
        if (!"MASTER".equals(role)) {
            // user 의 companyId, hubId
            Ids ids = getCompanyIdAndHubId(username);
            // 주문 상품의 hubId
            UUID hubId = companyPort.getCompanyByCompanyId(findOrder.supplyId().toString()).data().hubId();

            boolean isUnauthorized = switch (role) {
                case "HUB"-> !ids.hubId().equals(hubId);
                case "COMPANY" -> !ids.companyId().equals(findOrder.receiverId());
// TODO:                case "DELIVERY" -> 배송 담당자는 어떻게 해야 하나?
                default -> false;
            };

            if (isUnauthorized) {
                throw new CustomException(ORDER_UNAUTHORIZED);
            }
        }
    }

    private void checkHubAuthority(String username, String role, String supplyId) {
        // 허브 관리자는 담당 허브만 수정
        if ("HUB".equals(role)) {
            UUID hubId = companyPort.getCompanyByCompanyId(supplyId).data().hubId();
            Ids ids = getCompanyIdAndHubId(username);
            if (!ids.hubId().equals(hubId)) {
                throw new CustomException(ORDER_UNAUTHORIZED);
            }
        }
    }
}
