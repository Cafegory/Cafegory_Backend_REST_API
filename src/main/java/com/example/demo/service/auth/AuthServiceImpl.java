package com.example.demo.service.auth;

import static com.example.demo.exception.ExceptionType.*;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.domain.auth.CafegoryTokenManager;
import com.example.demo.dto.auth.CafegoryToken;
import com.example.demo.exception.CafegoryException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final CafegoryTokenManager cafegoryTokenManager;

	@Override
	public CafegoryToken refresh(String refreshToken) {
		boolean canRefresh = cafegoryTokenManager.canRefresh(refreshToken);
		if (canRefresh) {
			long identityId = cafegoryTokenManager.getIdentityId(refreshToken);
			return makeCafegoryToken(identityId);
		}
		throw new CafegoryException(TOKEN_REFRESH_REJECT);
	}

	private CafegoryToken makeCafegoryToken(long memberId) {
		Map<String, Object> claims = Map.of("memberId", memberId);
		return cafegoryTokenManager.createToken(claims);
	}

}
