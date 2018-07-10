package com.rest.api.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.rest.api.model.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, QuerydslPredicateExecutor<Car> {

	public Page<Car> findByVersion(int version, Pageable pageable);
	
	@Query(value = "select c from Car c where c.brand = ?1",
			countQuery = "select count(c) from Car c where c.brand = ?1")
	public Page<Car> findByBrand(String brand, Pageable pageable);
	
	@Query(value = "SELECT * FROM CAR WHERE MODEL = ?1",
			countQuery = "SELECT count(*) FROM CAR WHERE MODEL = ?1",
			nativeQuery = true)
	public Page<Car> findByModel(String model, Pageable pageable);
	
}
