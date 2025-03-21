package com.eleven.logistics.hubrouteservice.infrastructure.rabbitMQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Value("${message.exchange}")
    private String exchange;

    @Value("${message.queue.delivery}")
    private String queueDelivery;

    @Value("${message.err.exchange}")
    private String exchangeErr;

    @Value("${message.err.queue.delivery}")
    private String queueErrDelivery;


    // market exchange 생성
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    // queue
    @Bean public Queue queueDelivery() {
        return new Queue(queueDelivery);}


    // 바인딩 생성
    @Bean public Binding bindingProduct() {
        return BindingBuilder.bind(queueDelivery()).to(exchange()).with(queueDelivery); }

    // market.err exchange 생성
    @Bean public TopicExchange exchangeErr() {
        return new TopicExchange(exchangeErr);}

    // queue
    @Bean public Queue queueErrDelivery() {
        return new Queue(queueErrDelivery);
    }

    // 바인딩
    @Bean public Binding bindingErrDelivery() {
        return BindingBuilder.bind(queueErrDelivery()).to(exchangeErr()).with(queueErrDelivery);
    }
}
