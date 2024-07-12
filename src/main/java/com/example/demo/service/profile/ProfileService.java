package com.example.demo.service.profile;

import static com.example.demo.util.TruncatedTimeUtil.*;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.profile.ProfileGetResponse;
import com.example.demo.dto.profile.ProfileUpdateRequest;
import com.example.demo.dto.profile.ProfileUpdateResponse;

public interface ProfileService {

	@Transactional
	default ProfileGetResponse get(Long requestMemberId, Long targetMemberId) {
		return get(requestMemberId, targetMemberId, LOCAL_DATE_TIME_NOW);
	}

	ProfileGetResponse get(Long requestMemberId, Long targetMemberId, LocalDateTime baseDateTime);

	ProfileUpdateResponse update(Long requestMemberId, Long targetMemberId, ProfileUpdateRequest profileUpdateRequest);
}
