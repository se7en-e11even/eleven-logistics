package com.eleven.logistics.product.presentation.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

@Component
public class CustomPageableArgumentResolver extends PageableHandlerMethodArgumentResolver {

    private static final List<Integer> ALLOWED_PAGE_SIZES = List.of(10, 30, 50);
    private static final int DEFAULT_SIZE = 10;

    @Override
    public Pageable resolveArgument(
            MethodParameter methodParameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        Pageable pageable = super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        // 요청한 size 의 값이 허용된 값인지 확인
        int size = pageable.getPageSize();
        if (!ALLOWED_PAGE_SIZES.contains(size)) {
            size =  DEFAULT_SIZE;
        }
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }
}
