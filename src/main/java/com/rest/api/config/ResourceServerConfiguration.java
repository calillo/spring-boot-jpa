package com.rest.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.rest.api.web.ApiRest;
import com.rest.api.web.handler.AuthenticationFailureHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	
    @Override
    public void configure(HttpSecurity http) throws Exception {
    	http.authorizeRequests()
    		.antMatchers("/").permitAll()
    		.antMatchers(ApiRest.API_PATH + "/**").authenticated();
    }
    
    // catch oauth2exception and custom response set handler  
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    	resources
        	.accessDeniedHandler(accessDeniedHandler())
        	.authenticationEntryPoint(authenticationEntryPoint());
    }
    
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        authenticationEntryPoint.setExceptionTranslator(new AuthenticationFailureHandler());
        return authenticationEntryPoint;
    }
    
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
    	OAuth2AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();
    	accessDeniedHandler.setExceptionTranslator(new AuthenticationFailureHandler());
        return accessDeniedHandler;
    }
    
    
    // JWT token
    // if use security.oauth2.resource.jwt.key-uri
    // or use security.oauth2.resource.jwt.key-value
    // never set token provider, use default configuration
    /*  
	@Value("${security.oauth2.resource.jwt.key-value}")
	private String keyValue;
	  
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    	resources.tokenServices(tokenServices());
    }
    
    @Bean
    public TokenStore tokenStore() {
        //return new JwkTokenStore(keyUri, accessTokenConverter());
    	return new JwtTokenStore(accessTokenConverter());
    }
 
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {       
        // unsymmetric key    
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(keyValue);
        
        // symmetric key
        //converter.setSigningKey("12345678");
        return converter;
    }
 
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }
	*/
    
    // mandatory for external authorization server
    // with RemoteTokenServices resource server call authorization server on every authorization request (bad performance)
    /*
    
    @Value("${oauth.check.url}")
    private String checkUrl;
	
	@Value("${oauth.client.id}")
    private String clientId;
	
	@Value("${oauth.client.secret}")
    private String clientSecret;
    
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
    */

}
