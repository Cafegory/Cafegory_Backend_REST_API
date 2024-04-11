package com.example.demo.service.oauth2;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.domain.auth.CafegoryTokenManager;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.oauth2.OAuth2ProfileRequester;
import com.example.demo.domain.oauth2.OAuth2TokenRequester;
import com.example.demo.dto.auth.CafegoryToken;
import com.example.demo.dto.oauth2.OAuth2Profile;
import com.example.demo.dto.oauth2.OAuth2Token;
import com.example.demo.dto.oauth2.OAuth2TokenRequest;
import com.example.demo.repository.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service {
	private final OAuth2ProfileRequester oAuth2ProfileRequester;
	private final OAuth2TokenRequester oAuth2TokenRequester;
	@Autowired
	private CafegoryTokenManager cafegoryTokenManager;
	@Autowired
	private MemberRepository memberRepository;

	@Override
	public CafegoryToken joinOrLogin(OAuth2TokenRequest oAuth2TokenRequest) {
		OAuth2Profile oAuth2Profile = callOAuth2Api(oAuth2TokenRequest);
		Optional<Member> byEmail = memberRepository.findByEmail(oAuth2Profile.getEmailAddress());
		if (byEmail.isEmpty()) {
			Member save = memberRepository.save(makeNewMember(oAuth2Profile));
			return makeCafegoryToken(save.getId());
		}
		return makeCafegoryToken(byEmail.get().getId());
	}

	private CafegoryToken makeCafegoryToken(long memberId) {
		Map<String, Object> claims = Map.of("memberId", memberId);
		return cafegoryTokenManager.createToken(claims);
	}

	private static Member makeNewMember(OAuth2Profile oAuth2Profile) {
		ThumbnailImage thumbnailImage = new ThumbnailImage(null, oAuth2Profile.getProfileImgUrl());
		String nickName = oAuth2Profile.getNickName();
		String emailAddress = oAuth2Profile.getEmailAddress();
		return Member.builder()
			.name(nickName)
			.thumbnailImage(thumbnailImage)
			.email(emailAddress)
			.build();
	}

	private OAuth2Profile callOAuth2Api(OAuth2TokenRequest oAuth2TokenRequest) {
		OAuth2Token token = oAuth2TokenRequester.getToken(oAuth2TokenRequest);
		return oAuth2ProfileRequester.getOAuth2Profile(token);
	}
}
