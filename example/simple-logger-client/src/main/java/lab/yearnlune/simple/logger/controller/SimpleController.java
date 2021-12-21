package lab.yearnlune.simple.logger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lab.yearnlune.simple.logger.service.SimpleService;

@RestController
public class SimpleController {

	private final SimpleService simpleService;

	public SimpleController(SimpleService simpleService) {
		this.simpleService = simpleService;
	}

	@GetMapping("/simple")
	public ResponseEntity<?> getSimples() {
		return ResponseEntity.ok().body(simpleService.findSimples());
	}

	@GetMapping("/exception")
	public ResponseEntity<?> occurException() {
		return ResponseEntity.ok().body(simpleService.occurException());
	}
}
