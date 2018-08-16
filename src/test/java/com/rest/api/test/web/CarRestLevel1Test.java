package com.rest.api.test.web;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.rest.api.model.Car;
import com.rest.api.service.CarService;
import com.rest.api.web.ApiRest;
import com.rest.api.web.CarRest;
import com.rest.api.web.handler.ControllerAdvice;

@RunWith(MockitoJUnitRunner.class)
public class CarRestLevel1Test {
	
	private MockMvc mockMvc;
	
	private List<Car> carList = new ArrayList<>();
	private Page<Car> carPage;
	
    // This object will be magically initialized by the initFields method below.
    private JacksonTester<List<Car>> jsonCars;
    private JacksonTester<Car> jsonCar;
	
	@Mock
	private CarService carService;
	@Mock
	private ApplicationEventPublisher eventPublishere;
	
	@InjectMocks
	private CarRest carRest;
	
	@Before
    public void setup() {
        // We would need this line if we would not use MockitoJUnitRunner
        // MockitoAnnotations.initMocks(this);
        // Initializes the JacksonTester
        JacksonTester.initFields(this, new ObjectMapper());
            
        // MockMvc standalone approach
        mockMvc = MockMvcBuilders.standaloneSetup(carRest)
                .setControllerAdvice(new ControllerAdvice())
                .setCustomArgumentResolvers(
                		new QuerydslPredicateArgumentResolver(new QuerydslBindingsFactory(SimpleEntityPathResolver.INSTANCE), Optional.empty()),
                		new PageableHandlerMethodArgumentResolver())
                //.addFilters(new CarFilter())
                .build();

        carList.add(new Car(1, "BMW", "320d", 0, new BigDecimal("40000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        carList.add(new Car(2, "Audi", "A3 2.0 TDI", 0, new BigDecimal("35000.00"), ZonedDateTime.now(), ZonedDateTime.now()));
        
        carPage = new PageImpl<>(carList, PageRequest.of(0, 2), 5);
    }
	
	@Test
	public void getCar() throws Exception {
		//given
		given(carService.findById(1L))
			.willReturn(carList.get(1));
		
		//when
		MockHttpServletResponse response;
		response = mockMvc.perform(get(ApiRest.API_PATH + "/cars/1")).andReturn().getResponse();

		//then
		assertThat(response.getStatus(), equalTo(HttpStatus.OK.value()));
        assertThat(response.getContentAsString(), equalTo(jsonCar.write(carList.get(1)).getJson()));		
	}
	
	@Test
	public void listCars() throws Exception {
		//given
		//given(carService.findAllPaginated())
		//		.willReturn(carList);
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
		//given(carService.findAllPaginated())
		//		.willReturn(carList);
		given(carService.findAllPaginated(any(Predicate.class), any(PageRequest.class)))
				.willReturn(carPage);
		
		//when
		MockHttpServletResponse response;
		response = mockMvc.perform(get(ApiRest.API_PATH + "/cars")).andReturn().getResponse();

		//then
		assertThat(response.getStatus(), equalTo(HttpStatus.OK.value()));
        assertThat(response.getContentAsString(), equalTo(jsonCars.write(carList).getJson()));	
	}
	
}
