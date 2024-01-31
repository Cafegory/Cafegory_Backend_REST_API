package com.example.demo.domain.auth;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
class JwtManager {
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.type}")
	private String type;
	private JwtBuilder jwtBuilder;

	JwtManager() {
		this.jwtBuilder = Jwts.builder();
		jwtBuilder.header().type(type);
	}

	void claim(String key, Object value) {
		jwtBuilder.claim(key, value);
	}

	void setLife(Date issuedAt, int lifeTimeAsSeconds) {
		jwtBuilder
			.issuedAt(issuedAt)
			.expiration(Date.from(issuedAt.toInstant().plusSeconds(lifeTimeAsSeconds)));
	}

	String make() {
		String jwt = jwtBuilder
			.signWith(Keys.hmacShaKeyFor(secret.getBytes()))
			.compact();
		jwtBuilder = Jwts.builder();
		return jwt;
	}

	public Claims decode(String jwtString) {
		JwtParserBuilder parser = Jwts.parser();
		try {
			Jws<Claims> claimsJws = parser.verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
				.build()
				.parseSignedClaims(jwtString);
			return claimsJws.getPayload();

		} catch (ExpiredJwtException e) {
			throw new IllegalArgumentException("JWT 토큰이 만료되었습니다.");
		} catch (JwtException e) {
			throw new IllegalArgumentException("JWT 토큰이 잘못되었습니다.");
		}
	}
}
