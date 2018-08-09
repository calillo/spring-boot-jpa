package com.rest.api.test.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.transaction.Transactional;

import org.exparity.hamcrest.date.ZonedDateTimeMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import com.rest.api.exception.CarNotFoundException;
import com.rest.api.model.Car;
import com.rest.api.model.QCar;
import com.rest.api.service.CarService;

@RunWith(SpringRunner.class)
// Loads a WebApplicationContext and provides a mock servlet environment
// It will not start an embedded servlet container
@SpringBootTest
// reset database after test (like @DataJpaTest)
@Transactional
public class CarServiceTest {

	@Autowired
	private CarService carService;

	@Test
	@WithMockUser(authorities = {"CAR_READ"})
	public void getCar() throws Exception {
		Car car = carService.findById(3L);
		assertThat(3L, equalTo(car.getId()));
		assertThat("Mercedes", equalTo(car.getBrand()));
		assertThat("A 220d", equalTo(car.getModel()));
		assertThat(0, equalTo(car.getVersion()));
		assertThat(new BigDecimal("25000.00"), equalTo(car.getPrice()));
	}
	
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(authorities = {"MOCK"})
	public void getCarWrongUser() throws Exception {
		getCar();
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void getCarWithoutUser() throws Exception {
		getCar();
	}
	
	@Test(expected = CarNotFoundException.class)
	@WithMockUser(authorities = {"CAR_READ"})
	public void getCarNotFound() throws Exception {
		carService.findById(99L);
	}
	
	@Test
	@WithMockUser(authorities = {"CAR_READ", "CAR_ADD"})
	public void addCar() throws Exception {
		Car car = new Car();
		car.setBrand("Brand");
		car.setModel("Model");
		car.setVersion(1);
		car.setPrice(new BigDecimal("1000.00"));

		carService.add(car);
		assertThat(car.getId(), notNullValue());

		Car ins = carService.findById(car.getId());
		assertThat(car.getBrand(), equalTo(ins.getBrand()));
		assertThat(car.getModel(), equalTo(ins.getModel()));
		assertThat(car.getVersion(), equalTo(ins.getVersion()));
		assertThat(car.getPrice(), equalTo(ins.getPrice()));
		assertThat(ins.getInsertDate(), notNullValue());
		assertThat(ins.getUpdateDate(), notNullValue());
		assertThat(ins.getInsertDate(), ZonedDateTimeMatchers.before(ZonedDateTime.now()));
		assertThat(ins.getUpdateDate(), ZonedDateTimeMatchers.before(ZonedDateTime.now()));
	}
	
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(authorities = {"MOCK"})
	public void addCarWrongUser() throws Exception {
		addCar();
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void addCarWithoutUser() throws Exception {
		addCar();
	}

	@Test
	@WithMockUser(authorities = {"CAR_READ", "CAR_UPDATE"})
	public void updateCar() throws Exception {
		Car car = carService.findById(2L);
		car.setBrand("Brand");
		car.setModel("Model");
		car.setVersion(0);
		car.setPrice(new BigDecimal("1000.00"));

		carService.update(2L, car);

		Car upd = carService.findById(2L);
		assertThat(car.getBrand(), equalTo(upd.getBrand()));
		assertThat(car.getModel(), equalTo(upd.getModel()));
		assertThat(car.getVersion(), equalTo(upd.getVersion()));
		assertThat(car.getPrice(), equalTo(upd.getPrice()));
		assertThat(car.getInsertDate(), is(notNullValue()));
		assertThat(car.getUpdateDate(), is(notNullValue()));
		assertThat(car.getUpdateDate(), ZonedDateTimeMatchers.after(car.getInsertDate()));
		assertThat(car.getUpdateDate(), ZonedDateTimeMatchers.before(ZonedDateTime.now()));
	}
	
	@Test(expected = CarNotFoundException.class)
	@WithMockUser(authorities = {"CAR_UPDATE"})
	public void updateCarNotFound() throws Exception {
		Car car = new Car();
		car.setId(99);
		car.setBrand("Brand");
		car.setModel("Model");
		car.setVersion(0);
		car.setPrice(new BigDecimal("1000.00"));

		carService.update(car.getId(), car);
	}
	
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(authorities = {"MOCK"})
	public void updateCarWrongUser() throws Exception {
		updateCar();
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void updateCarWithoutUser() throws Exception {
		updateCar();
	}

	@Test
	@WithMockUser(authorities = {"CAR_DELETE"})
	public void deleteCar() throws Exception {
		carService.deleteById(2L);
	}
	
	@Test(expected = CarNotFoundException.class)
	@WithMockUser(authorities = {"CAR_DELETE"})
	public void deleteCarNotFound() throws Exception {
		carService.deleteById(99L);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(authorities = {"MOCK"})
	public void deleteCarWrongUser() throws Exception {
		deleteCar();
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void deleteCarWithoutUser() throws Exception {
		deleteCar();
	}
	
	@Test
	@WithMockUser(authorities = {"CAR_READ"})
	public void listCars() {
		Iterable<Car> carList = carService.findAll();

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
			case 4:
				assertThat("Fiat", equalTo(c.getBrand()));
				assertThat("Punto", equalTo(c.getModel()));
				assertThat(0, equalTo(c.getVersion()));
				assertThat(new BigDecimal("10000.00"), equalTo(c.getPrice()));
				break;
			case 5:
				assertThat("VW", equalTo(c.getBrand()));
				assertThat("Polo", equalTo(c.getModel()));
				assertThat(0, equalTo(c.getVersion()));
				assertThat(new BigDecimal("16000.00"), equalTo(c.getPrice()));
				break;
			default:
				fail();
				break;
			}
		}
	}
	
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(authorities = {"MOCK"})
	public void listCarsWrongUser() throws Exception {
		listCars();
	}
	
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void listCarsWithoutUser() throws Exception {
		listCars();
	}
	
	@Test
	@WithMockUser(authorities = {"CAR_READ"})
	public void listCarsPaginated() {
		Page<Car> carList;
		
		// page 0
		carList = carService.findAllPaginated(QCar.car.instanceOfAny(), PageRequest.of(0, 2, Sort.by("id").ascending()));
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
			default:
				fail();
				break;
			}
		}
		
		// page 1
		carList = carService.findAllPaginated(QCar.car.instanceOfAny(), PageRequest.of(1, 2, Sort.by("id").ascending()));
		for (Car c : carList) {
			switch ((int) c.getId()) {
			case 3:
				assertThat("Mercedes", equalTo(c.getBrand()));
				assertThat("A 220d", equalTo(c.getModel()));
				assertThat(0, equalTo(c.getVersion()));
				assertThat(new BigDecimal("25000.00"), equalTo(c.getPrice()));
				break;
			case 4:
				assertThat("Fiat", equalTo(c.getBrand()));
				assertThat("Punto", equalTo(c.getModel()));
				assertThat(0, equalTo(c.getVersion()));
				assertThat(new BigDecimal("10000.00"), equalTo(c.getPrice()));
				break;
			default:
				fail();
				break;
			}
		}
		
		// page 2
		carList = carService.findAllPaginated(QCar.car.instanceOfAny(), PageRequest.of(2, 2, Sort.by("id").ascending()));
		for (Car c : carList) {
			switch ((int) c.getId()) {
			case 5:
				assertThat("VW", equalTo(c.getBrand()));
				assertThat("Polo", equalTo(c.getModel()));
				assertThat(0, equalTo(c.getVersion()));
				assertThat(new BigDecimal("16000.00"), equalTo(c.getPrice()));
				break;
			default:
				fail();
				break;
			}
		}
	}

}
