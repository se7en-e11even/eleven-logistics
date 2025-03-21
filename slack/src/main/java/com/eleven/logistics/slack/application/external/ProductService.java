package com.eleven.logistics.slack.application.external;

import com.eleven.logistics.slack.application.querydto.FindProductQuery;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface ProductService {

    FindProductQuery read(@PathVariable UUID product_id);
}
