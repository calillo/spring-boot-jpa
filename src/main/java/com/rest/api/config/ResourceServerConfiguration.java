package com.rest.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

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
