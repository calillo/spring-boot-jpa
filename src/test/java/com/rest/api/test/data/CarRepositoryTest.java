package com.rest.api.test.data;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import com.rest.api.data.CarRepository;
import com.rest.api.model.Car;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CarRepositoryTest {

	@Autowired
	private CarRepository carRepository;

	@Test
	public void getCar() throws Exception {
		Car car = carRepository.findById(3L).get();
		assertThat(3L, equalTo(car.getId()));
		assertThat("Mercedes", equalTo(car.getBrand()));
		assertThat("A 220d", equalTo(car.getModel()));
		assertThat(0, equalTo(car.getVersion()));
		assertThat(new BigDecimal("25000.00"), equalTo(car.getPrice()));
	}
	
	@Test
	public void getCarNotFound() {
		carRepository.findById(99L);
	}
	
	@Test
	public void addCar() throws Exception {
		Car car = new Car();
		car.setBrand("Brand");
		car.setModel("Model");
		car.setVersion(1);
		car.setPrice(new BigDecimal("1000.00"));

		car = carRepository.save(car);
		assertThat(car.getId(), notNullValue());

		Car ins = carRepository.findById(car.getId()).get();
		assertThat(car.getBrand(), equalTo(ins.getBrand()));
		assertThat(car.getModel(), equalTo(ins.getModel()));
		assertThat(car.getVersion(), equalTo(ins.getVersion()));
		assertThat(car.getPrice(), equalTo(ins.getPrice()));
	}

	@Test
	public void updateCar() throws Exception {
		Car car = carRepository.findById(2L).get();
		car.setBrand("Brand");
		car.setModel("Model");
		car.setVersion(0);
		car.setPrice(new BigDecimal("1000.00"));
		
		Thread.sleep(100);
		carRepository.save(car);

		Car upd = carRepository.findById(2L).get();
		assertThat(car.getBrand(), equalTo(upd.getBrand()));
		assertThat(car.getModel(), equalTo(upd.getModel()));
		assertThat(car.getVersion(), equalTo(upd.getVersion()));
		assertThat(car.getPrice(), equalTo(upd.getPrice()));
	}

	@Test
	public void deleteCar() {
		carRepository.deleteById(2L);
	}
	
	@Test(expected = EmptyResultDataAccessException.class)
	public void deleteCarNotFound() {
		carRepository.deleteById(99L);
	}

	@Test
	public void listCars() {
		Iterable<Car> carList = carRepository.findAll();
		//assertThat(3, equalTo(carList.size()));

		for (Car c : carList) {
			switch ((int) c.getId()) {
			case 1:
				assertThat("BMW", equalTo(c.getBrand()));
				assertThat("320d", equalTo(c.getModel()));
				assertThat(1, equalTo(c.getVersion()));
				assertThat(new BigDecimal("40000.00"), equalTo(c.getPrice()));
				break;
			case 2:
				assertThat("Audi", equalTo(c.getBrand()));
				assertThat("A3 2.0 TDI", equalTo(c.getModel()));
				assertThat(0, equalTo(c.getVersion()));
				assertThat(new BigDecimal("35000.00"), equalTo(c.getPrice()));
				break;
			case 3:
				assertThat("Mercedes", equalTo(c.getBrand()));
				assertThat("A 220d", equalTo(c.getModel()));
				assertThat(0, equalTo(c.getVersion()));
				assertThat(new BigDecimal("25000.00"), equalTo(c.getPrice()));
				break;
			default:
				break;
			}
		}

	}

}
