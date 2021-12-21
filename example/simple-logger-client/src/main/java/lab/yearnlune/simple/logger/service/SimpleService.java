package lab.yearnlune.simple.logger.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SimpleService {

	public String findSimples() {
		return "FIND";
	}

	public String occurException() {
		throw new RuntimeException("INTENTIONAL ERROR!!");
	}
}
