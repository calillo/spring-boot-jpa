package com.rest.api.service;

import java.io.Serializable;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.rest.api.exception.EntityNotFoundException;

public interface CrudService<T, ID extends Serializable> {
	
	T findById(ID id) throws EntityNotFoundException;
	Iterable<T> findAll();
	Page<T> findAllPaginated(Pageable pageable);
	T add(@Valid T entity);
	void update(ID id, @Valid T entity) throws EntityNotFoundException;
	void delete(T entity);
	void deleteById(ID id);
}
