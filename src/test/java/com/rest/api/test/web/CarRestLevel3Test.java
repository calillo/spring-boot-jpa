package com.rest.api.test.web;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.api.model.Car;
import com.rest.api.service.CarService;
import com.rest.api.web.ApiRest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CarRestLevel3Test {
	
	private List<Car> carList = new ArrayList<>();
	
    // This object will be magically initialized by the initFields method below.
    private JacksonTester<List<Car>> jsonCars;
    
    @Autowired
    private TestRestTemplate restTemplate;
	
    @MockBean
	private CarService carService;
	
	@Before
    public void setup() {
        // We would need this line if we would not use MockitoJUnitRunner
        // MockitoAnnotations.initMocks(this);
        // Initializes the JacksonTester
        JacksonTester.initFields(this, new ObjectMapper());
        
        carList.add(new Car(1, "BMW", "320d", 0, new BigDecimal("40000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(2, "Audi", "A3 2.0 TDI", 0, new BigDecimal("35000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
    }
	
	@Test
	public void listCars2() throws Exception {
		//given
		given(carService.findAll())
				.willReturn(carList);
		//when
		ResponseEntity<String> response;
		response = restTemplate.getForEntity(ApiRest.API_PATH + "/cars", String.class);

		//then
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getBody(), equalTo(jsonCars.write(carList).getJson()));
		
	}
}
