package com.rest.api;

import javax.validation.Validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootApplication
public class SpringBootRestApp {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestApp.class, args);
	}
	
    @Bean
    public Validator validator(MessageSource messageSource) {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);
        return factory;
    }
	
    // Use messages properties instead of ValidationMessages for Hibernate Validation
    // at Service Layer
	@Bean
	public ResourceBundleMessageSource messageSource() {
	    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	    messageSource.setBasename("messages");
	    messageSource.setUseCodeAsDefaultMessage(true);
	    return messageSource;
	}  
	
}
