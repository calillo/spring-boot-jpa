package com.rest.api.test.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.exparity.hamcrest.date.ZonedDateTimeMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import com.rest.api.data.CarRepository;
import com.rest.api.exception.CarNotFoundException;
import com.rest.api.model.Car;
import com.rest.api.service.CarService;

@RunWith(SpringRunner.class)
// Loads a WebApplicationContext and provides a mock servlet environment
// It will not start an embedded servlet container
@SpringBootTest
// reset database after test (like @DataJpaTest)
//@Transactional
// Avoid recreation database on mvn test
@AutoConfigureTestDatabase
public class CarServiceTest {
	
	private List<Car> carList = new ArrayList<>();
	private Car car;

	@Autowired
	private CarService carService;
	
	@MockBean
	private CarRepository carRepository;

/*	@TestConfiguration
	static class CarServiceImplTestContextConfiguration {

		@Bean
		public CarService employeeService() {
			return new CarServiceImpl();
		}
	}*/
	
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

	@Test
	@WithMockUser(authorities = {"CAR_READ"})
	public void getCar() throws Exception {
		//given
		given(carRepository.findById(3L)).willReturn(Optional.of(carList.get(2)));
		
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
		//given
		given(carRepository.findById(99L)).willReturn(Optional.empty());
		
		carService.findById(99L);
	}
	
	@Test
	@WithMockUser(authorities = {"CAR_READ", "CAR_ADD"})
	public void addCar() throws Exception {
		//given
		given(carRepository.save(car)).willReturn(car);
		given(carRepository.findById(car.getId())).willReturn(Optional.of(car));
		
		carService.add(car);
		assertThat(car.getId(), notNullValue());

		Car ins = carService.findById(car.getId());
		assertThat(car.getBrand(), equalTo(ins.getBrand()));
		assertThat(car.getModel(), equalTo(ins.getModel()));
		assertThat(car.getVersion(), equalTo(ins.getVersion()));
		assertThat(car.getPrice(), equalTo(ins.getPrice()));
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
		given(carRepository.findById(2L)).willReturn(Optional.of(carList.get(1)));
				
		Car car = carService.findById(2L);
		car.setBrand("Brand");
		car.setModel("Model");
		car.setVersion(0);
		car.setPrice(new BigDecimal("1000.00"));

		carService.update(2L, car);
		
		//given
		//given(carRepository.findById(2L)).willReturn(Optional.of(carList.get(1))); //?

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
		given(carRepository.findById(99L)).willReturn(Optional.empty());
		
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
		willThrow(new EmptyResultDataAccessException(0)).given(carRepository).deleteById(99L);

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
		//given
		given(carRepository.findAll()).willReturn(carList);
		
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
	
	/*
	@Test
	@WithMockUser(authorities = {"CAR_READ"})
	public void listCarsPaginated() {
		
		PageRequest page0 = PageRequest.of(0, 2, Sort.by("id").ascending());
		PageRequest page1 = PageRequest.of(1, 2, Sort.by("id").ascending());
		PageRequest page2 = PageRequest.of(2, 2, Sort.by("id").ascending());
		
		//given		
		given(carRepository.findAll(any(Predicate.class), eq(page0))).willReturn(new PageImpl<>(carList.subList(0, 1), page0, 5));
		given(carRepository.findAll(any(Predicate.class), eq(page1))).willReturn(new PageImpl<>(carList.subList(2, 3), PageRequest.of(1, 2), 5));
		given(carRepository.findAll(any(Predicate.class), eq(page2))).willReturn(new PageImpl<>(carList.subList(4, 4), PageRequest.of(2, 2), 5));
		
		Page<Car> pageCarList;
		// page 0
		pageCarList = carService.findAllPaginated(QCar.car.instanceOfAny(), page0);
		for (Car c : pageCarList) {
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
		pageCarList = carService.findAllPaginated(QCar.car.instanceOfAny(), page1);
		for (Car c : pageCarList) {
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
		pageCarList = carService.findAllPaginated(QCar.car.instanceOfAny(), page2);
		for (Car c : pageCarList) {
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
	*/

}
