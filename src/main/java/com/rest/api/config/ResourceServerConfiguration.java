package com.rest.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

import com.rest.api.web.ApiRest;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    
    @Override
    public void configure(HttpSecurity http) throws Exception {
    	http.authorizeRequests()
    		.antMatchers("/").permitAll()
    		.antMatchers(ApiRest.API_PATH + "/**").authenticated();
    }

    // mandatory for external authorization server
    // with RemoteTokenServices resource server call authorization server on every authorization request (bad performance)
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(remoteTokenServices());
    }
    
    @Bean
    public RemoteTokenServices remoteTokenServices() {
    	//TODO: move to config file
        final RemoteTokenServices tokenServices = new RemoteTokenServices();
        tokenServices.setCheckTokenEndpointUrl("http://localhost:8081/oauth/check_token");
        tokenServices.setClientId("client_id");
        tokenServices.setClientSecret("client_secret");
        return tokenServices;
    }

}
