package com.rest.api.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rest.api.model.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

}
