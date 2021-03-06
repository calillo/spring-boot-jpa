package com.rest.api.test.web;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import com.rest.api.exception.CarNotFoundException;
import com.rest.api.model.Car;
import com.rest.api.service.CarService;
import com.rest.api.web.ApiRest;
import com.rest.api.web.CarRest;

@RunWith(SpringRunner.class)
// @Component, @Service, @Repository, etc. will not be scanned when using this annotation
// Disable oauth2 security (secure = false)
@WebMvcTest(controllers = CarRest.class, secure = false)
// Configure QuerydslPredicateArgumentResolver and PageableHandlerMethodArgumentResolver
@EnableSpringDataWebSupport
// Load messages.properties
@ImportAutoConfiguration(MessageSourceAutoConfiguration.class)
public class CarRestLevel2Test {
	
	private Car carJson, carBean;
	private List<Car> carList = new ArrayList<>();
	private Page<Car> carPage;
	
	// This object will be magically initialized by the initFields method below.
    private JacksonTester<List<Car>> jsonCars;
    private JacksonTester<Car> jsonCar;
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private CarService carService;
	@MockBean
	private ApplicationEventPublisher eventPublishere;
	
	@Before
    public void setup() {
		// We would need this line if we would not use MockitoJUnitRunner
        // MockitoAnnotations.initMocks(this);
        // Initializes the JacksonTester
        JacksonTester.initFields(this, new ObjectMapper());
        
        carList.add(new Car(1, "BMW", "320d", 0, new BigDecimal("40000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(2, "Audi", "A3 2.0 TDI", 0, new BigDecimal("35000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        
        carJson = new Car(0, "Brand", "Model", 1, new BigDecimal("1000.00"), null, null);
        carBean = new Car(10, "Brand", "Model", 1, new BigDecimal("1000.00"), ZonedDateTime.now(), ZonedDateTime.now());
        
        carPage = new PageImpl<>(carList, PageRequest.of(0, 2), 5);
    }
	
	@Test
	public void listCars() throws Exception {
		//given
		given(carService.findAllPaginated(any(Predicate.class), any(PageRequest.class)))
				.willReturn(carPage);
		
		//when
		mockMvc.perform(get(ApiRest.API_PATH + "/cars"))
		
		//then
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].id", is((int)carList.get(0).getId())))
			.andExpect(jsonPath("$[0].brand", is(carList.get(0).getBrand())))
			.andExpect(jsonPath("$[0].model", is(carList.get(0).getModel())))
			.andExpect(jsonPath("$[0].version", is(carList.get(0).getVersion())))
			.andExpect(jsonPath("$[1].id", is((int)carList.get(1).getId())))
			.andExpect(jsonPath("$[1].brand", is(carList.get(1).getBrand())))
			.andExpect(jsonPath("$[1].model", is(carList.get(1).getModel())))
			.andExpect(jsonPath("$[1].version", is(carList.get(1).getVersion())))
		;
	}
	
	@Test
	public void listCars2() throws Exception {
		//given
		given(carService.findAllPaginated(any(Predicate.class), any(PageRequest.class)))
				.willReturn(carPage);
		//when
		MockHttpServletResponse response;
		response = mockMvc.perform(get(ApiRest.API_PATH + "/cars")).andReturn().getResponse();

		//then
		assertThat(response.getStatus(), equalTo(HttpStatus.OK.value()));
        assertThat(response.getContentAsString(), equalTo(jsonCars.write(carList).getJson()));	
	}
	
	@Test
	public void getCar() throws Exception {
		//given
		given(carService.findById(1L)).willReturn(carList.get(1));
		
		//when
		MockHttpServletResponse response;
		response = mockMvc.perform(get(ApiRest.API_PATH + "/cars/1")).andReturn().getResponse();

		//then
		assertThat(response.getStatus(), equalTo(HttpStatus.OK.value()));
        assertThat(response.getContentAsString(), equalTo(jsonCar.write(carList.get(1)).getJson()));		
	}
	
	@Test
	public void getCarNotFound() throws Exception {
		//given
		willThrow(new CarNotFoundException()).given(carService).findById(any(Long.class));
		
		//when
		MockHttpServletResponse response;
		response = mockMvc.perform(get(ApiRest.API_PATH + "/cars/1")).andReturn().getResponse();

		//then
		assertThat(response.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
	}
	
	@Test
	public void addCar() throws Exception {
		//given
		given(carService.add(any(Car.class))).willReturn(carBean);
		
		//when
		MockHttpServletResponse response;
		response = mockMvc.perform(post(ApiRest.API_PATH + "/cars")
									.contentType(MediaType.APPLICATION_JSON)
									.content(jsonCar.write(carJson).getJson()))
							.andReturn().getResponse();

		//then
		assertThat(response.getStatus(), equalTo(HttpStatus.CREATED.value()));
		assertThat(response.getHeaderValue("location").toString(), endsWith(ApiRest.API_PATH + "/cars/" + carBean.getId()));
	}
	
	@Test
	public void updateCar() throws Exception {
		//given
		willDoNothing().given(carService).update(any(Long.class), any(Car.class));
		
		//when
		MockHttpServletResponse response;
		response = mockMvc.perform(put(ApiRest.API_PATH + "/cars/1")
									.contentType(MediaType.APPLICATION_JSON)
									.content(jsonCar.write(carJson).getJson()))
							.andReturn().getResponse();

		//then
		assertThat(response.getStatus(), equalTo(HttpStatus.NO_CONTENT.value()));
	}
	
	@Test
	public void updateCarNotFound() throws Exception {
		//given
		willThrow(new CarNotFoundException()).given(carService).update(any(Long.class), any(Car.class));
		
		//when
		MockHttpServletResponse response;
		response = mockMvc.perform(put(ApiRest.API_PATH + "/cars/99")
									.contentType(MediaType.APPLICATION_JSON)
									.content(jsonCar.write(carJson).getJson()))
							.andReturn().getResponse();

		//then
		assertThat(response.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
	}
	
	@Test
	public void deleteCar() throws Exception {
		//given
		willDoNothing().given(carService).deleteById(any(Long.class));
		
		//when
		MockHttpServletResponse response;
		response = mockMvc.perform(delete(ApiRest.API_PATH + "/cars/1")).andReturn().getResponse();

		//then
		assertThat(response.getStatus(), equalTo(HttpStatus.NO_CONTENT.value()));
	}
	
	@Test
	public void deleteCarNotFound() throws Exception {
		//given
		willThrow(new CarNotFoundException()).given(carService).deleteById(any(Long.class));
		
		//when
		MockHttpServletResponse response;
		response = mockMvc.perform(delete(ApiRest.API_PATH + "/cars/99")).andReturn().getResponse();

		//then
		assertThat(response.getStatus(), equalTo(HttpStatus.NOT_FOUND.value()));
	}
	
}
