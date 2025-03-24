package com.eleven.logistics.order.application.service;

import com.eleven.logistics.order.application.dto.command.CreateOrderCommand;
import com.eleven.logistics.order.application.dto.command.OrderProductCommand;
import com.eleven.logistics.order.application.dto.command.OrderRollbackCommand;
import com.eleven.logistics.order.application.dto.command.UpdateOrderCommand;
import com.eleven.logistics.order.application.dto.message.ToDelivery;
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

    private final OrderRepository repository;
    private final OrderRepositoryCustom repositoryCustom;
    private final RabbitMQBrokerPort rabbitMQBrokerPort;

    // Feign Client
    private final ProductPort productPort;
    private final CompanyPort companyPort;
    private final UserPort userPort;
    private final HubPort hubPort;

    private record Result(FindUserQuery findUser, FindCompanyQuery.Company findCompany, FindProductQuery findProduct) {}

    private record Ids(UUID companyId, UUID hubId) {}
    @CachePut(cacheNames = "orderRead", key = "{ #result.orderId, #role }")
    public FindOrderQuery create(CreateOrderCommand command, String username, String role) {
        Result result = getUserIdAndCompanyIdAndHubId(command, username);

        // 주문 수량과 재고를 비교해야 한다.
        OrderProductCommand productOrderCommand = new OrderProductCommand(command.commandList().stream()
                .map(product ->
                        new OrderProductCommand.Product(
                                product.productId(),
                                product.quantity()
                        )
                ).toList()
        );
        productPort.putProductOrder(productOrderCommand);

        // 저장할 주문 엔티티 생성
        Order order = Order.builder()
                .supplyId(result.findProduct().companyId())
                .receiverId(result.findCompany().id())
                .orderStatus(OrderStatus.PENDING)
                .request(command.request())
                .orderProductList(new ArrayList<>())
                .build();

        // 주문 상품 추가
        for (CreateOrderCommand.CreateOrderProductCommand orderProductCommand : command.commandList()) {
            OrderProduct orderProduct = OrderProduct.builder()
                    .productId(orderProductCommand.productId())
                    .price(orderProductCommand.price())
                    .quantity(orderProductCommand.quantity())
                    .build();
            order.addOrderProduct(orderProduct);
        }
        repository.save(order);

        FindOrderQuery savedOrder = FindOrderQuery.from(order);
        requestDelivery(savedOrder, result);
        return savedOrder;
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

        order.updateOf(command.request());

        // TODO: updateDto 의 orderProductList 수정
    }


    // 주문자의 주문 취소: 주문 상태를 CANCELED 로 변경하여 정보를 저장한다.
    public FindOrderQuery cancel(UUID orderId) {
        Order order = repositoryCustom.findById(orderId)
                .orElseThrow(()->new CustomException(ORDER_NOT_FOUND));
        if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new CustomException(ORDER_NOT_CANCEL);
        }
        order.changeOrderStatus(OrderStatus.CANCELED);
        // TODO: 주문이 취소되면 배송 정보도 변경 요청을 해야 한다.

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

    public void rollback(UUID orderId) {
        Order byOrderId = repository.findByOrderId(orderId);
        byOrderId.changeOrderStatus(OrderStatus.FAIL);

        // 주문 상품의 목록을 가져온다.
        OrderRollbackCommand command = OrderRollbackCommand.from(byOrderId);

        // TODO: FeignClient 요청이 실패하면 롤백은 어떻게 하지?
        productPort.putProductRollBack(command);
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

    private void requestDelivery(FindOrderQuery savedOrder, Result result) {
        // message publish
        ToDelivery message = ToDelivery.of(
                savedOrder.orderId(),
                result.findProduct().hubId(),
                result.findCompany().hubId(),
                result.findCompany().address(),
                result.findUser().username(),
                result.findUser().slackAccount()
        );
        rabbitMQBrokerPort.publishMessage(message);
    }

    private Result getUserIdAndCompanyIdAndHubId(CreateOrderCommand command, String username) {
        // 배송에 전달할 수신인 username -> userId
        FindUserQuery findUser = userPort.getUserByUsername(username);

        // username 의 companyId: 주문자의 회사 -> receiverId, receiver HubId
        FindCompanyQuery.Company findCompany = companyPort.getCompanyByUsername(username).data();

        // 주문상품의 companyId: 공급자의 회사 -> supplyId, supply HubId
        FindProductQuery findProduct = productPort.getProductByProductId(command.commandList().get(0).productId().toString());

        return new Result(findUser, findCompany, findProduct);
    }
}


