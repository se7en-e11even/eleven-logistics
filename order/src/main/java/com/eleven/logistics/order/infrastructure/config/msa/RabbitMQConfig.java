package com.eleven.logistics.order.infrastructure.config.msa;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Value("${message.send.exchange}")
    private String orderExchange;

    @Value("${message.send.queue.delivery}")
    private String orderDeliveryQueue;

    @Value("${message.send.err.exchange}")
    private String orderErrExchange;

    @Value("${message.send.err.queue.delivery}")
    private String orderErrDeliveryQueue;

    // order exchange 생성
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(orderExchange);
    }

    // order.delivery queue 생성
    @Bean
    public Queue orderDeliveryQueue() {
        return new Queue(orderDeliveryQueue);
    }

    // order.delivery binding
    @Bean
    public Binding orderDeliveryBinding() {
        return BindingBuilder.bind(orderDeliveryQueue())
                .to(orderExchange())
                .with(orderDeliveryQueue);
    }

    // order.err exchange
    @Bean
    public TopicExchange orderErrExchange() {
        return new TopicExchange(orderErrExchange);
    }

    // order.err.delivery queue
    @Bean
    public Queue orderErrDeliveryQueue() {
        return new Queue(orderErrDeliveryQueue);
    }

    // order.err.delivery binding
    @Bean
    public Binding orderErrDeliveryBinding() {
        return BindingBuilder.bind(orderErrDeliveryQueue())
                .to(orderExchange())
                .with(orderErrDeliveryQueue);
    }

    /**
     * TEST 용
     */

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("test-exchange");
    }

    @Bean
    public Queue queue() {
        return new Queue("test-queue");
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("test-routing-key");
    }
}