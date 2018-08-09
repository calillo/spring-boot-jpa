package com.rest.api.test.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.api.model.Car;
import com.rest.api.web.ApiRest;

@RunWith(SpringRunner.class)
// Loads a ServletWebServerApplicationContext and 
// starts an embedded servlet container listening on a random available port
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CarRestLevel3Test {
	
	private List<Car> carList = new ArrayList<>();
	
    // This object will be magically initialized by the initFields method below.
    private JacksonTester<List<Car>> jsonCars;
    private JacksonTester<Car> jsonCar;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Value("${oauth.token.url}")
    private String tokenUrl;	
	@Value("${oauth.client.id}")
    private String clientId;	
	@Value("${oauth.client.secret}")
    private String clientSecret;
	@Value("${oauth.username}")
    private String username;
	@Value("${oauth.password}")
    private String password;

    private OAuth2RestTemplate oauthTemplate;
    private HttpHeaders header = new HttpHeaders();
	
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
        
        // get oauth2 token
        oauthTemplate = getOAuth2RestTemplate();
        header.add("Authorization", "Bearer " + oauthTemplate.getAccessToken());
    }
	
	private OAuth2RestTemplate getOAuth2RestTemplate() {
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
		resourceDetails.setGrantType("password");
		resourceDetails.setAccessTokenUri(tokenUrl);
	
		//-- set the clients info
		resourceDetails.setClientId(clientId);
		resourceDetails.setClientSecret(clientSecret);
		
		// set scopes
		//List<String> scopes = new ArrayList<>();
		//scopes.add("read"); 
		//scopes.add("write");
		//scopes.add("trust");
		//resourceDetails.setScope(scopes);
		
		//-- set Resource Owner info
		resourceDetails.setUsername(username);
		resourceDetails.setPassword(password);
		
		return new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext(new DefaultAccessTokenRequest()));
	}
	
	@Test
	public void getCar() throws Exception {	
		//when
		ResponseEntity<String> response;
		response = restTemplate.exchange(ApiRest.API_PATH + "/cars/1", HttpMethod.GET, new HttpEntity<>(header), String.class);

		//then
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getBody(), equalTo(jsonCar.write(carList.get(0)).getJson()));		
	}
	
	@Test
	public void listCars2() throws Exception {		
		//when
		ResponseEntity<String> response;
		response = restTemplate.exchange(ApiRest.API_PATH + "/cars", HttpMethod.GET, new HttpEntity<>(header), String.class);

		//then
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getBody(), equalTo(jsonCars.write(carList).getJson()));	
	}
	
	@Test
	public void deleteForbidden() throws Exception {		
		//when
		ResponseEntity<String> response;
		response = restTemplate.exchange(ApiRest.API_PATH + "/cars/1", HttpMethod.DELETE, new HttpEntity<>(header), String.class);

		//then
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void unouthorized() throws Exception {
		//when
		ResponseEntity<String> response;
		response = restTemplate.getForEntity(ApiRest.API_PATH + "/cars", String.class);

		//then
		assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
	}
}
