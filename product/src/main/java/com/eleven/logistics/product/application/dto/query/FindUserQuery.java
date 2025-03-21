package com.eleven.logistics.product.application.dto.query;

/**
 * FeignClient 를 사용해 User 정보를 요청하는 데 사용할 DTO
 */
public record FindUserQuery(
        Long id,
        String username,
        String slackAccount,
        String role
) {

}
