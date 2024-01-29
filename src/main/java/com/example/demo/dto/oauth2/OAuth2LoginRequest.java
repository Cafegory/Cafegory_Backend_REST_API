package com.example.demo.dto.oauth2;

import org.springframework.util.MultiValueMap;

public interface OAuth2LoginRequest {
	OAuth2Provider getProvider();

	MultiValueMap<String, String> getParameters();
}
