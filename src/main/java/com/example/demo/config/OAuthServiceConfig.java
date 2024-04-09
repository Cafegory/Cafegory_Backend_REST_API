package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.example.demo.domain.auth.CafegoryTokenManager;
import com.example.demo.domain.auth.JwtCafegoryTokenManager;
import com.example.demo.domain.auth.JwtManager;
import com.example.demo.domain.oauth2.KakaoOAuth2ProfileRequester;
import com.example.demo.domain.oauth2.KakaoOAuth2TokenRequester;
import com.example.demo.domain.oauth2.NaverOAuth2ProfileRequester;
import com.example.demo.domain.oauth2.NaverOAuth2TokenRequester;
import com.example.demo.service.oauth2.OAuth2ServiceImpl;

@Configuration
public class OAuthServiceConfig {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public KakaoOAuth2ProfileRequester kakaoOAuth2ProfileRequester() {
		return new KakaoOAuth2ProfileRequester(restTemplate());
	}

	@Bean
	public NaverOAuth2ProfileRequester naverOAuth2ProfileRequester() {
		return new NaverOAuth2ProfileRequester(restTemplate());
	}

	@Bean
	public KakaoOAuth2TokenRequester kakaoOAuth2TokenRequester() {
		return new KakaoOAuth2TokenRequester(restTemplate());
	}

	@Bean
	public NaverOAuth2TokenRequester naverOAuth2TokenRequester() {
		return new NaverOAuth2TokenRequester(restTemplate());
	}

	@Bean
	public OAuth2ServiceImpl kakaoOAuth2Service() {
		return new OAuth2ServiceImpl(kakaoOAuth2ProfileRequester(), kakaoOAuth2TokenRequester());
	}

	@Bean
	public OAuth2ServiceImpl naverOAuth2Service() {
		return new OAuth2ServiceImpl(naverOAuth2ProfileRequester(), naverOAuth2TokenRequester());
	}

	@Bean
	public JwtManager jwtManager() {
		return new JwtManager(jwtSecret);
	}

	@Bean
	public CafegoryTokenManager cafegoryTokenManager() {
		return new JwtCafegoryTokenManager(jwtManager());
	}
}
