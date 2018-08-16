package com.rest.api.test.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.transaction.Transactional;

import org.exparity.hamcrest.date.ZonedDateTimeMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import com.rest.api.exception.CarNotFoundException;
import com.rest.api.model.Car;
import com.rest.api.service.CarService;

@RunWith(SpringRunner.class)
// Loads a WebApplicationContext and provides a mock servlet environment
// It will not start an embedded servlet container
@SpringBootTest
// reset database after test (like @DataJpaTest)
@Transactional
// Avoid recreation database on mvn test
@AutoConfigureTestDatabase
public class CarServiceTest {

	@Autowired
	private CarService carService;
	
	/*
	private List<Car> carList = new ArrayList<>();
	private Car car;
	
	@MockBean
	private CarRepository carRepository;

	@TestConfiguration
	static class CarServiceImplTestContextConfiguration {

		@Bean
		public CarService employeeService() {
			return new CarServiceImpl();
		}
	}
	
	@Before
    public void setup() throws InterruptedException {
		car = new Car(6, "Brand", "Model", 1, new BigDecimal("1000.00"), null, null);
		
        carList.add(new Car(1, "BMW", "320d", 1, new BigDecimal("40000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(2, "Audi", "A3 2.0 TDI", 0, new BigDecimal("35000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(3, "Mercedes", "A 220d", 0, new BigDecimal("25000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(4, "Fiat", "Punto", 0, new BigDecimal("10000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(5, "VW", "Polo", 0, new BigDecimal("16000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        Thread.sleep(1000);
    }	
    */

	@Test
	@WithMockUser(authorities = {"CAR_READ"})
	public void getCar() throws Exception {
		//given
		//given(carRepository.findById(3L)).willReturn(Optional.of(carList.get(2)));
		
		carService.findById(3L);
	}
	
	@Test(expected = CarNotFoundException.class)
	@WithMockUser(authorities = {"CAR_READ"})
	public void getCarNotFound() throws Exception {
		//given
		//given(carRepository.findById(99L)).willReturn(Optional.empty());
		
		carService.findById(99L);
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
	
	@Test
	@WithMockUser(authorities = {"CAR_READ"})
	public void listCars() {	
		//given
		//given(carRepository.findAll()).willReturn(carList);
		
		carService.findAll();
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
	@WithMockUser(authorities = {"CAR_READ", "CAR_ADD"})
	public void addCar() throws Exception {
		//given
		//given(carRepository.save(car)).willReturn(car);
		//given(carRepository.findById(car.getId())).willReturn(Optional.of(car));
		
		Car car = new Car(6, "Brand", "Model", 1, new BigDecimal("1000.00"), null, null);
		
		carService.add(car);
		assertThat(car.getId(), notNullValue());

		Car ins = carService.findById(car.getId());
		assertThat(ins.getInsertDate(), notNullValue());
		assertThat(ins.getUpdateDate(), notNullValue());
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
		//given
		//given(carRepository.findById(2L)).willReturn(Optional.of(carList.get(1)));
				
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
		//given
		//given(carRepository.findById(99L)).willReturn(Optional.empty());
		
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
		//given
		//willThrow(new EmptyResultDataAccessException(0)).given(carRepository).deleteById(99L);

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

}
