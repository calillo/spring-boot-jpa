package com.rest.api.web;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloRest extends ApiRest {
	
	/*
	@Autowired
	private TransactionService transactionService;

	@RequestMapping("hello")
	String home() {
		return "Hello World!";
	}
	
	@GetMapping(value = "test")
	public ResponseEntity<?> test() {
		transactionService.test();
		return ResponseEntity.noContent().build();
	}
	*/

}
