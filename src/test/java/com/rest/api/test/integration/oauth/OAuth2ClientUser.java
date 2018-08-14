package com.rest.api.test.integration.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

public class OAuth2ClientUser extends ResourceOwnerPasswordResourceDetails {
	
    @Value("${oauth.token.url}")
    private String _tokenUrl;	
	@Value("${oauth.client.id}")
    private String _clientId;	
	@Value("${oauth.client.secret}")
    private String _clientSecret;
	@Value("${oauth.username}")
    private String _username;
	@Value("${oauth.password}")
    private String _password;
	
    public OAuth2ClientUser() {
        setAccessTokenUri("http://localhost:8081/oauth/token");
        setClientId("client_id");
        setClientSecret("client_secret");
        setUsername("user");
        setPassword("user");
        
        //setAccessTokenUri(_tokenUrl);
        //setClientId(_clientId);
        //setClientSecret(_clientSecret);
        //setUsername(_username);
        //setPassword(_password);
    }
}
