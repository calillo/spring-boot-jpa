package com.rest.api.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rest.api.model.Car;

@Repository
public interface CarRepository extends CrudRepository<Car, Long> {

}
