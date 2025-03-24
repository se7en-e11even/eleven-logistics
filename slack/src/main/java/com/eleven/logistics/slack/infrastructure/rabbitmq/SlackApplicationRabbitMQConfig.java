package com.eleven.logistics.slack.infrastructure.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackApplicationRabbitMQConfig {

    @Value("${message.exchange.delivery}")
    private String deliveryExchange;

    @Value("${message.err.exchange.delivery}")
    private String deliveryErrExchange;

    @Value("${message.queue.delivery.slack}")
    private String deliverySlackQueue;

    @Value("${message.err.queue.delivery.slack}")
    private String deliveryErrQueueSlack;

    @Bean
    public TopicExchange deliveryExchange() {return new TopicExchange(deliveryExchange);}
    @Bean
    public TopicExchange deliveryErrExchange() {return new TopicExchange(deliveryErrExchange);}

    @Bean
    public Queue deliverySlackQueue() {return new Queue(deliverySlackQueue);}

    @Bean
    public Queue deliveryErrQueueSlack() {return new Queue(deliveryErrQueueSlack);}

    @Bean
    public Binding deliverySlackQueueBinding(){
        return BindingBuilder.bind(deliverySlackQueue()).to(deliveryExchange()).with(deliveryErrQueueSlack);
    }

    @Bean
    public Binding deliveryErrQueueSlackBinding(){
        return BindingBuilder.bind(deliveryErrQueueSlack()).to(deliveryErrExchange()).with(deliveryErrQueueSlack);
    }
}
