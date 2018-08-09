package com.rest.api.test.web;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.security.oauth2.client.test.OAuth2ContextSetup;
import org.springframework.security.oauth2.client.test.RestTemplateHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import com.rest.api.model.Car;
import com.rest.api.service.CarService;
import com.rest.api.test.web.oauth.OAuth2ClientUser;
import com.rest.api.web.ApiRest;

@RunWith(SpringRunner.class)
// Loads a ServletWebServerApplicationContext and 
// starts an embedded servlet container listening on a random available port
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CarRestLevel3Test2 implements RestTemplateHolder {
	
	@Value("http://localhost:${local.server.port}")
	private String host;
	
	private List<Car> carList = new ArrayList<>();
	private Page<Car> carPage;
	
    // This object will be magically initialized by the initFields method below.
    private JacksonTester<List<Car>> jsonCars;
    
    //@Autowired
    private RestTemplate restTemplate = new RestTemplate();
	
    @MockBean
	private CarService carService;
    
    @Rule
    public OAuth2ContextSetup context = OAuth2ContextSetup.standard(this);
	
	@Before
    public void setup() {
        // We would need this line if we would not use MockitoJUnitRunner
        // MockitoAnnotations.initMocks(this);
        // Initializes the JacksonTester
        JacksonTester.initFields(this, new ObjectMapper());
        
        carList.add(new Car(1, "BMW", "320d", 0, new BigDecimal("40000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(2, "Audi", "A3 2.0 TDI", 0, new BigDecimal("35000.00"), ZonedDateTime.now(), ZonedDateTime.now()));     
        carPage = new PageImpl<>(carList, PageRequest.of(0, 2), 5);
        
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            protected boolean hasError(HttpStatus statusCode) {
                return false;
            }});
        
    }
	
	@Test
	@OAuth2ContextConfiguration(OAuth2ClientUser.class)
	public void listCars2() throws Exception {
		//given
		//given(carService.findAll())
		//		.willReturn(carList);
		given(carService.findAllPaginated(any(Predicate.class), any(PageRequest.class)))
				.willReturn(carPage);
		
		//when
		ResponseEntity<String> response;
		response = restTemplate.getForEntity(host + ApiRest.API_PATH + "/cars", String.class);
		//response = restTemplate.exchange(ApiRest.API_PATH + "/cars", HttpMethod.GET, new HttpEntity<>(header), String.class);

		//then
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getBody(), equalTo(jsonCars.write(carList).getJson()));		
	}
	
	@Test
	@OAuth2ContextConfiguration(OAuth2ClientUser.class)
	public void deleteForbidden() throws Exception {		
		//when
		ResponseEntity<String> response;
		response = restTemplate.exchange(host + ApiRest.API_PATH + "/cars/1", HttpMethod.DELETE, null, String.class);

		//then
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void unouthorized() throws Exception {
		//when
		ResponseEntity<String> response;
		response = restTemplate.getForEntity(host + ApiRest.API_PATH + "/cars", String.class);

		//then
		assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
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


