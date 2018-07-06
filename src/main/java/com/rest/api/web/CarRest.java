package com.rest.api.web;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.rest.api.exception.EntityNotFoundException;
import com.rest.api.exception.ResourceNotFoundException;
import com.rest.api.model.Car;
import com.rest.api.service.CarService;
import com.rest.api.web.event.PaginatedResultsRetrievedEvent;

@RestController
@RequestMapping(ApiRest.API_PATH + "/cars")
@Validated
public class CarRest extends ApiRest {

	@Autowired
	private CarService carService;
	
	@Autowired
    private ApplicationEventPublisher eventPublisher;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Iterable<Car> listCars() {
		return carService.findAll();
	}
	
	@GetMapping(params = {"page", "size"}, produces = MediaType.APPLICATION_JSON_VALUE)
	public Iterable<Car> listCars(@Min(1) @RequestParam("page") int page, @Max(50) @RequestParam("size") int size,
			UriComponentsBuilder uriBuilder, HttpServletResponse response) {
		
		Page<Car> resultPage = carService.findAllPaginated(page-1, size);
		
		if(page > resultPage.getTotalPages()) {
			throw new ResourceNotFoundException();
		}
		
		eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<Car>
		    (this, ServletUriComponentsBuilder.fromCurrentRequest(), response, resultPage.getTotalElements(), page, resultPage.getTotalPages(), size));
		   
		return resultPage.getContent();
	}

	@GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Car getCar(@PathVariable("id") long id) throws EntityNotFoundException {
		return carService.findById(id);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addCar(@RequestBody Car car) {
		carService.add(car);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(car.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateCar(@PathVariable("id") long id, @RequestBody Car car) throws EntityNotFoundException {
		carService.update(id, car);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = "{id}")
	public ResponseEntity<?> deleteCar(@PathVariable("id") long id) {
		carService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
