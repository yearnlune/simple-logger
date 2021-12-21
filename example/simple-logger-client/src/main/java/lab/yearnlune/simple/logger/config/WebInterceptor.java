package lab.yearnlune.simple.logger.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebInterceptor implements HandlerInterceptor {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
		Exception ex) throws JsonProcessingException {
		log.info("API: {}", this.objectMapper.writeValueAsString(
			RestApiLog.builder()
				.method(request.getMethod())
				.requestUri(request.getRequestURI())
				.status(response.getStatus())
				.remoteAddress(request.getRemoteAddr())
				.build()));
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RestApiLog {
		private String method;

		private String requestUri;

		private Integer status;

		private String remoteAddress;
	}
}
