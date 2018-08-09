package com.rest.api.test.web.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.stereotype.Component;

@Component
public class OAuth2ClientUser extends ResourceOwnerPasswordResourceDetails {
	
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
	
    public OAuth2ClientUser() {
        //MyControllerIT it = (MyControllerIT) obj;
        setAccessTokenUri("http://localhost:8081/oauth/token");
        setClientId("client_id");
        setClientSecret("client_secret");
        setUsername("user");
        setPassword("user");
    }
}
