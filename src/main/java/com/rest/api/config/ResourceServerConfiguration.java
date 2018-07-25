package com.rest.api.config;

import org.springframework.beans.factory.annotation.Value;
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
    
	@Value("${oauth.check.url}")
    private String checkUrl;
	
	@Value("${oauth.client.id}")
    private String clientId;
	
	@Value("${oauth.client.secret}")
    private String clientSecret;
	
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
        final RemoteTokenServices tokenServices = new RemoteTokenServices();
        tokenServices.setCheckTokenEndpointUrl(checkUrl);
        tokenServices.setClientId(clientId);
        tokenServices.setClientSecret(clientSecret);
        return tokenServices;
    }

}
