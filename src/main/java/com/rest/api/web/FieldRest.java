package com.rest.api.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRest.API_PATH + "/fields")
public class FieldRest extends ApiRest {

	/*
	@Autowired
	private FieldService fieldService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Iterable<Field> listCars() {
		return fieldService.findAll();
	}

	@GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Field getCar(@PathVariable("id") long id) throws EntityNotFoundException {
		return fieldService.findById(id);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addCar(@RequestBody Field field) {
		fieldService.add(field);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(field.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateCar(@PathVariable("id") long id, @RequestBody Field field) throws EntityNotFoundException {
		fieldService.update(id, field);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<?> deleteCar(@PathVariable("id") long id) {
		fieldService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	*/

}
