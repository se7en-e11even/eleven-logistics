package com.eleven.logistics.product;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class ProductControllerApiV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetProductsSuccess() throws Exception {

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/products")
                                .header("X-Username", "alice")
                                .header("X-Role", "MASTER")
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk()
                )
                .andDo(
                        MockMvcRestDocumentationWrapper.document(
                                "PRODUCTS 조회 성공",
                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                ResourceDocumentation.resource(
                                        ResourceSnippetParameters.builder()
                                                .tag("PRODUCT-SERVICE V1")
                                                .summary("PRODUCT 조회")
                                                .description(
                                                        """
                                                                ## PRODUCT 조회 엔드포인트 입니다.
                                                                
                                                                ---
                                                                
                                                                PRODUCT LIST 가 리턴됩니다.
                                                                
                                                                """)
                                                .build()
                                )

                        )
                );

    }
}
