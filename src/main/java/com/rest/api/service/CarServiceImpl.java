package com.rest.api.service;

import java.time.ZonedDateTime;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.rest.api.data.CarRepository;
import com.rest.api.exception.CarNotFoundException;
import com.rest.api.exception.EntityNotFoundException;
import com.rest.api.model.Car;

@Service
@Validated
public class CarServiceImpl implements CarService {

	@Autowired
	private CarRepository carRepository;
	
	public Car findById(Long id) throws EntityNotFoundException {
		Optional<Car> optCar = carRepository.findById(id);
		if(optCar.isPresent())
			return optCar.get();
		else
			throw new CarNotFoundException();
	}

	public Iterable<Car> findAll() {
		return carRepository.findAll();
	}

	public Page<Car> findAllPaginated(Pageable pageable) {
		return carRepository.findAll(pageable);
	}
	
	public Car add(@Valid Car entity) {
		entity.setInsertDate(ZonedDateTime.now());
		entity.setUpdateDate(ZonedDateTime.now());
		return carRepository.save(entity);
	}

	public void update(Long id, @Valid Car entity) throws EntityNotFoundException {
		Optional<Car> optCar = carRepository.findById(id);
		if(optCar.isPresent()) {
			entity.setId(id);
			entity.setUpdateDate(ZonedDateTime.now());
			carRepository.save(entity);
		} else
			throw new CarNotFoundException();		
	}

	public void deleteById(Long id) throws EntityNotFoundException {
		try {
			carRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new CarNotFoundException();
		}
	}

	public void delete(Car entity) throws EntityNotFoundException {		
		try {
			carRepository.delete(entity);
		} catch (EmptyResultDataAccessException e) {
			throw new CarNotFoundException();
		}
	}

}
