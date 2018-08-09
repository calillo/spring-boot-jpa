package com.rest.api.test.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.security.oauth2.client.test.OAuth2ContextSetup;
import org.springframework.security.oauth2.client.test.RestTemplateHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.api.model.Car;
import com.rest.api.test.integration.oauth.OAuth2ClientUser;
import com.rest.api.web.ApiRest;

@RunWith(SpringRunner.class)
// Loads a ServletWebServerApplicationContext and 
// starts an embedded servlet container listening on a random available port
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CarRestLevel3Test2 implements RestTemplateHolder {
	
	@Value("http://localhost:${local.server.port}")
	private String host;
	
	private List<Car> carList = new ArrayList<>();
	
    // This object will be magically initialized by the initFields method below.
    private JacksonTester<List<Car>> jsonCars;
    private JacksonTester<Car> jsonCar;
    
    private RestTemplate restTemplate = new RestTemplate();
    
    @Rule
    public OAuth2ContextSetup context = OAuth2ContextSetup.standard(this);
	
	@Before
    public void setup() {
        // We would need this line if we would not use MockitoJUnitRunner
        // MockitoAnnotations.initMocks(this);
        // Initializes the JacksonTester
        JacksonTester.initFields(this, new ObjectMapper());
       
        // same ad DB
        carList.add(new Car(1, "BMW", "320d", 1, new BigDecimal("40000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(2, "Audi", "A3 2.0 TDI", 0, new BigDecimal("35000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(3, "Mercedes", "A 220d", 0, new BigDecimal("25000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(4, "Fiat", "Punto", 0, new BigDecimal("10000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(5, "VW", "Polo", 0, new BigDecimal("16000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
    }
	
	@Test
	@OAuth2ContextConfiguration(OAuth2ClientUser.class)
	public void getCar() throws Exception {	
		//when
		ResponseEntity<String> response;
		response = restTemplate.getForEntity(host + ApiRest.API_PATH + "/cars/1", String.class);

		//then
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getBody(), equalTo(jsonCar.write(carList.get(0)).getJson()));		
	}
	
	@Test
	@OAuth2ContextConfiguration(OAuth2ClientUser.class)
	public void listCars2() throws Exception {	
		//when
		ResponseEntity<String> response;
		response = restTemplate.getForEntity(host + ApiRest.API_PATH + "/cars", String.class);

		//then
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getBody(), equalTo(jsonCars.write(carList).getJson()));		
	}
	
	@Test
	@OAuth2ContextConfiguration(OAuth2ClientUser.class)
	public void deleteForbidden() throws Exception {		
		//when
		try {
			restTemplate.delete(host + ApiRest.API_PATH + "/cars/1");
		} catch (HttpClientErrorException e) {
			//then
			assertThat(e.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
		}
	}
	
	@Test
	public void unouthorized() throws Exception {
		//when
		try {
			restTemplate.getForEntity(host + ApiRest.API_PATH + "/cars", String.class);
		} catch (HttpClientErrorException e) {
			//then
			assertThat(e.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
		}		
	}

	@Override
	public RestOperations getRestTemplate() {
		return restTemplate;
	}

	@Override
	public void setRestTemplate(RestOperations arg0) {
		restTemplate = (RestTemplate)arg0;
	}
	
}


