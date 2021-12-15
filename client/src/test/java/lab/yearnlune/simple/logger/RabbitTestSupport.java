package lab.yearnlune.simple.logger;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public abstract class RabbitTestSupport extends SpringBootTestSupport {

	@Autowired
	protected AmqpAdmin amqpAdmin;

	@Autowired
	protected Environment environment;
}
