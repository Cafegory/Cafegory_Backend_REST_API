package com.example.demo.service;

import com.example.demo.dto.auth.CafegoryToken;
import com.example.demo.dto.oauth2.OAuth2LoginRequest;

public interface OAuth2Service {
	CafegoryToken joinOrLogin(OAuth2LoginRequest oAuth2LoginRequest);
}
