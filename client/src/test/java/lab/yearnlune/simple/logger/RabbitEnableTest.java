package lab.yearnlune.simple.logger;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.amqp.AmqpIOException;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("create")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RabbitEnableTest extends RabbitTestSupport {

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private final RestTemplate template = new RestTemplate();

	@BeforeEach
	public void purgeQueue() {
		String queue = environment.getProperty("simple-log.rabbitmq.queue");
		try {
			amqpAdmin.purgeQueue(queue);
		} catch (AmqpIOException e) {
			log.error("PURGE QUEUE FAILED: {}", queue);
		}
	}

	@Test
	@Order(1)
	public void alreadyCreatedQueue() {
		/* GIVEN */
		String rabbitQueuesURI =
			"http://" + environment.getProperty("simple-log.rabbitmq.host") + ":" + environment.getProperty(
				"simple-log.rabbitmq.stats-port") + "/api/queues";

		HttpHeaders header = new HttpHeaders();
		header.add("Authorization",
			"Basic " + HttpHeaders.encodeBasicAuth(
				Objects.requireNonNull(environment.getProperty("simple-log.rabbitmq.username")),
				Objects.requireNonNull(environment.getProperty("simple-log.rabbitmq.password")),
				StandardCharsets.UTF_8));

		HttpEntity<?> payload = new HttpEntity<>(header);

		/* WHEN */
		ResponseEntity<String> response = template.exchange(rabbitQueuesURI, HttpMethod.GET, payload, String.class);

		/* THEN */
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody(), containsString(environment.getProperty("simple-log.rabbitmq.queue")));
	}

	@Test
	@Order(2)
	public void publishToQueue() throws ExecutionException, InterruptedException {
		/* GIVEN */
		String queue = environment.getProperty("simple-log.rabbitmq.queue");

		/* WHEN */
		Future<QueueInformation> future = executor.submit(() -> {
			log.warn("LOGBACK TEST: {}", queue);
			Thread.sleep(500L);
			return amqpAdmin.getQueueInfo(queue);
		});

		QueueInformation queueInformation = future.get();

		/* THEN */
		assertThat(queueInformation, notNullValue());
		assertThat(queueInformation.getMessageCount(), greaterThan(0));
	}
}
