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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
import com.querydsl.core.types.Predicate;
import com.rest.api.model.Car;
import com.rest.api.service.CarService;
import com.rest.api.web.ApiRest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CarRestLevel3Test {
	
	private List<Car> carList = new ArrayList<>();
	private Page<Car> carPage;
	
    // This object will be magically initialized by the initFields method below.
    private JacksonTester<List<Car>> jsonCars;
    
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
        carPage = new PageImpl<>(carList, PageRequest.of(0, 2), 5);
        
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
	public void listCars2() throws Exception {
		//given
		//given(carService.findAll())
		//		.willReturn(carList);
		given(carService.findAllPaginated(any(Predicate.class), any(PageRequest.class)))
				.willReturn(carPage);
		
		//when
		ResponseEntity<String> response;
		//response = restTemplate.getForEntity(ApiRest.API_PATH + "/cars", String.class);
		response = restTemplate.exchange(ApiRest.API_PATH + "/cars", HttpMethod.GET, new HttpEntity<>(header), String.class);

		//then
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getBody(), equalTo(jsonCars.write(carList).getJson()));
		
	}
}
