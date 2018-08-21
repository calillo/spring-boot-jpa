package com.rest.api.web;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.querydsl.core.types.Predicate;
import com.rest.api.exception.EntityNotFoundException;
import com.rest.api.exception.ResourceNotFoundException;
import com.rest.api.model.Car;
import com.rest.api.service.CarService;
import com.rest.api.web.event.PaginatedResultsRetrievedEvent;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(ApiRest.API_PATH + "/cars")
@Validated
public class CarRest extends ApiRest {

	@Autowired
	private CarService carService;
	
	@Autowired
    private ApplicationEventPublisher eventPublisher;

	//@GetMapping
	//public Iterable<Car> listCars() {
	//	return carService.findAll();
	//}

	@ApiImplicitParams({
	    @ApiImplicitParam(name = "page", value = "page number", required = false, dataType = "integer", paramType = "query"),
	    @ApiImplicitParam(name = "size", value = "page size", required = false, dataType = "integer", paramType = "query"),
	    @ApiImplicitParam(name = "brand", value = "brand filter", required = false, dataType = "string", paramType = "query")
	  })
	@ApiOperation(value = "View a list of cars", response = Car.class, responseContainer="List")
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully retrieved list"),
	        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	})
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Iterable<Car> listCars(
			@ApiIgnore @QuerydslPredicate(root = Car.class) Predicate predicate, 
			@ApiIgnore Pageable  pageable,
			UriComponentsBuilder uriBuilder, 
			HttpServletResponse response) {
		
		Page<Car> resultPage = carService.findAllPaginated(predicate, pageable);
		
		if(pageable.getPageNumber() > resultPage.getTotalPages() - 1) {
			throw new ResourceNotFoundException();
		}
		
		eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent
		    (this, ServletUriComponentsBuilder.fromCurrentRequest(), response, resultPage.getTotalElements(), pageable.getPageNumber(), resultPage.getTotalPages(), pageable.getPageSize()));
		   
		return resultPage.getContent();
	}

	@GetMapping(value = "{id}")
	@ResponseStatus(HttpStatus.OK)
	public Car getCar(@PathVariable("id") long id) throws EntityNotFoundException {
		return carService.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addCar(@RequestBody Car car) {
		Car carIns = carService.add(car);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(carIns.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping(value = "{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateCar(@PathVariable("id") long id, @RequestBody Car car) throws EntityNotFoundException {
		carService.update(id, car);
	}

	@DeleteMapping(value = "{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCar(@PathVariable("id") long id) throws EntityNotFoundException {
		carService.deleteById(id);
	}

}
