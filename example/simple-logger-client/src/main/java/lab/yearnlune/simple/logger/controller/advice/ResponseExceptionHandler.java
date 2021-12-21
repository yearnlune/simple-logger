package lab.yearnlune.simple.logger.controller.advice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ResponseExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<?> handleMethodArgumentNotValid(RuntimeException exception,
		HttpServletRequest request) {
		log.error("#1 ISSUE", exception);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getCause());
	}
}
