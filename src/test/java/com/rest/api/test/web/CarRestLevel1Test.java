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
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.querydsl.QuerydslPredicateArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import com.rest.api.exception.CarNotFoundException;
import com.rest.api.model.Car;
import com.rest.api.service.CarService;
import com.rest.api.web.ApiRest;
import com.rest.api.web.CarRest;
import com.rest.api.web.handler.ControllerAdvice;

@RunWith(MockitoJUnitRunner.class)
public class CarRestLevel1Test {
	
	private MockMvc mockMvc;
	
	private Car carJson, carBean;
	private List<Car> carList = new ArrayList<>();
	private Page<Car> carPage;
	
    // This object will be magically initialized by the initFields method below.
    private JacksonTester<List<Car>> jsonCars;
    private JacksonTester<Car> jsonCar;
	
	@InjectMocks
	private CarRest carRest;
	@Mock
	private CarService carService;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	
	//@InjectMocks
	//private ControllerAdvice controllerAdvice;
	//@Mock
	//private MessageSource messageSource;
	
	@Before
    public void setup() {
        // We would need this line if we would not use MockitoJUnitRunner
        // MockitoAnnotations.initMocks(this);
        // Initializes the JacksonTester
        JacksonTester.initFields(this, new ObjectMapper());    
            
        // MockMvc standalone approach
        mockMvc = MockMvcBuilders.standaloneSetup(carRest)
                //.setControllerAdvice(controllerAdvice)
                .setControllerAdvice(new ControllerAdvice().setMessageSource(messageSource()))
                .setCustomArgumentResolvers(
                		new QuerydslPredicateArgumentResolver(new QuerydslBindingsFactory(SimpleEntityPathResolver.INSTANCE), Optional.empty()),
                		new PageableHandlerMethodArgumentResolver())
                //.addFilters(new CarFilter())
                .build();

        carList.add(new Car(1, "BMW", "320d", 0, new BigDecimal("40000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(2, "Audi", "A3 2.0 TDI", 0, new BigDecimal("35000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        
        carJson = new Car(0, "Brand", "Model", 1, new BigDecimal("1000.00"), null, null);
        carBean = new Car(10, "Brand", "Model", 1, new BigDecimal("1000.00"), ZonedDateTime.now(), ZonedDateTime.now());
        
        carPage = new PageImpl<>(carList, PageRequest.of(0, 2), 5);
    }
	
	private MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

		messageSource.setBasename("classpath:messages");
		messageSource.setUseCodeAsDefaultMessage(true);

		return messageSource;
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
		//given(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).willReturn("car not found!");
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
