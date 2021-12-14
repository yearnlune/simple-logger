package lab.yearnlune.simple.logger;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@ConditionalOnProperty(prefix = "simple-log.rabbitmq", name = "create")
public class RabbitConfig {

	@Value("${simple-log.rabbitmq.host:rabbitmq}")
	private String host;

	@Value("${simple-log.rabbitmq.port:5672}")
	private Integer port;

	@Value("${simple-log.rabbitmq.username:guest}")
	private String userName;

	@Value("${simple-log.rabbitmq.password:guest}")
	private String password;

	@Value("${simple-log.rabbitmq.exchange:simple-log.topic}")
	private String exchange;

	@Value("${simple-log.rabbitmq.routing-key.pattern:simple-log.*}")
	private String routingKeyPattern;

	@Value("${simple-log.rabbitmq.queue:simple-log.general}")
	private String queue;

	@Bean
	public Jackson2JsonMessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setHost(host);
		factory.setPort(port);
		factory.setUsername(userName);
		factory.setPassword(password);
		return factory;
	}

	@Bean
	public TopicExchange simpleLogTopicExchange() {
		return new TopicExchange(exchange);
	}

	@Bean
	public Queue simpleLogQueue() {
		return new Queue(queue, true);
	}

	@Bean
	public Binding simpleLogBinding() {
		return BindingBuilder
			.bind(simpleLogQueue())
			.to(simpleLogTopicExchange())
			.with(routingKeyPattern);
	}

	@Bean
	@DependsOn({"simpleLogTopicExchange", "simpleLogQueue", "simpleLogBinding"})
	public AmqpAdmin amqpAdmin() {
		System.out.println("amqpAdmin");
		AmqpAdmin amqpAdmin = new RabbitAdmin(connectionFactory());
		amqpAdmin.declareExchange(simpleLogTopicExchange());
		amqpAdmin.declareQueue(simpleLogQueue());
		amqpAdmin.declareBinding(simpleLogBinding());
		return amqpAdmin;
	}

	@Bean
	public RabbitTemplate simpleLogRabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setExchange(exchange);
		template.setRoutingKey(routingKeyPattern);
		template.setMessageConverter(jsonMessageConverter());
		return template;
	}
}
