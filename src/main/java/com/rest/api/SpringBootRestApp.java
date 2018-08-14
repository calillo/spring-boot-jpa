package com.rest.api;

import javax.validation.Validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootApplication
@EnableOAuth2Sso
//@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class SpringBootRestApp {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestApp.class, args);
	}
	
    // Use messages properties instead of ValidationMessages for Hibernate Validation
    // at Service Layer
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultEncoding("UTF-8");
	    //messageSource.setBasename("messages");
	    //messageSource.setUseCodeAsDefaultMessage(true);
	    return messageSource;
	}
	
    @Bean
    public Validator validator(MessageSource messageSource) {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);
        return factory;
    }
    
}
