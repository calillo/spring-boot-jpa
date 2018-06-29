package com.rest.api.service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rest.api.data.CarRepository;
import com.rest.api.exception.CarNotFoundException;
import com.rest.api.exception.EntityNotFoundException;
import com.rest.api.model.Car;

@Service
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

	public Car add(@Valid Car entity) {
		return carRepository.save(entity);
	}

	public void update(Long id, @Valid Car entity) throws EntityNotFoundException {
		Optional<Car> optCar = carRepository.findById(id);
		if(optCar.isPresent()) {
			entity.setId(id);
			carRepository.save(entity);
		} else
			throw new CarNotFoundException();		
	}

	public void deleteById(Long id) {
		carRepository.deleteById(id);
	}

	public void delete(Car entity) {
		carRepository.delete(entity);
	}

}
