package com.eleven.logistics.delivery.infrastructure.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${spring.rabbitmq.host}") private String rabbitmqHost;
  @Value("${spring.rabbitmq.port}") private int rabbitmqPort;
  @Value("${spring.rabbitmq.username}") private String rabbitmqUsername;
  @Value("${spring.rabbitmq.password}") private String rabbitmqPassword;

  @Value("${message.exchange}") private String deliveryExchange;
  @Value("${message.queue.order}") private String deliveryOrderQueue;
  @Value("${message.queue.slack}") private String deliverySlackQueue;
  @Value("${message.queue.hubroute}") private String deliveryHubRouteQueue;
  @Value("${message.receive.order-delivery}") private String orderDeliveryQueue;
  @Value("${message.receive.hubroute-delivery}") private String hubRouteDeliveryQueue;
  @Value("${err.exchange}") private String deliveryErrExchange;
  @Value("${err.queue.order}") private String deliveryErrOrderQueue;
  @Value("${err.queue.slack}") private String deliveryErrSlackQueue;
  @Value("${err.queue.hubroute}") private String deliveryErrHubRouteQueue;

  // Exchanges
  @Bean public TopicExchange deliveryExchange() { return new TopicExchange(deliveryExchange); }
  @Bean public TopicExchange deliveryErrExchange() { return new TopicExchange(deliveryErrExchange); }

  // Queue (발행용)
  @Bean public Queue deliveryOrderQueue() { return new Queue(deliveryOrderQueue); }
  @Bean public Queue deliverySlackQueue() { return new Queue(deliverySlackQueue); }
  @Bean public Queue deliveryHubRouteQueue() { return new Queue(deliveryHubRouteQueue); }

  // Queue (수신용)
  @Bean public Queue orderDeliveryQueue() { return new Queue(orderDeliveryQueue); }
  @Bean public Queue hubRouteDeliveryQueue() { return new Queue(hubRouteDeliveryQueue); }

  // Queue (Err)
  @Bean public Queue deliveryErrOrderQueue() { return new Queue(deliveryErrOrderQueue); }
  @Bean public Queue deliveryErrSlackQueue() { return new Queue(deliveryErrSlackQueue); }
  @Bean public Queue deliveryErrHubRouteQueue() { return new Queue(deliveryErrHubRouteQueue); }

  // Binding (발행용)
  @Bean public Binding bindingDeliveryOrderQueue() { return BindingBuilder.bind(deliveryOrderQueue()).to(deliveryExchange()).with(deliveryOrderQueue);}
  @Bean public Binding bindingDeliverySlackQueue() { return BindingBuilder.bind(deliverySlackQueue()).to(deliveryExchange()).with(deliverySlackQueue);}
  @Bean public Binding bindingDeliveryHubRouteQueue() { return BindingBuilder.bind(deliveryHubRouteQueue()).to(deliveryExchange()).with(deliveryHubRouteQueue);}

  // Binding (Err)
  @Bean public Binding bindingDeliveryErrOrder() { return BindingBuilder.bind(deliveryErrOrderQueue()).to(deliveryErrExchange()).with(deliveryErrOrderQueue);}
  @Bean public Binding bindingDeliveryErrSlack() { return BindingBuilder.bind(deliveryErrSlackQueue()).to(deliveryErrExchange()).with(deliveryErrSlackQueue);}
  @Bean public Binding bindingDeliveryErrHubRoute() { return BindingBuilder.bind(deliveryErrHubRouteQueue()).to(deliveryErrExchange()).with(deliveryErrHubRouteQueue);}

  // RabbitMQ 연결 설정
  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setHost(rabbitmqHost);
    connectionFactory.setPort(rabbitmqPort);
    connectionFactory.setUsername(rabbitmqUsername);
    connectionFactory.setPassword(rabbitmqPassword);
    return connectionFactory;
  }

  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  // 연결 설정으로 연결 후 실제 작업을 위한 RabbitTemplate
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    // JSON 형식의 메시지를 직렬화하고 역직렬할 수 있도록 설정
    rabbitTemplate.setMessageConverter(messageConverter());
    return rabbitTemplate;
  }
}
