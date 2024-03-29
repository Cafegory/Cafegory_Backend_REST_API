package com.example.demo.service.profile;

import java.time.LocalDateTime;

import com.example.demo.dto.profile.ProfileResponse;

public interface ProfileService {
	default ProfileResponse get(Long requestMemberID, Long targetMemberID) {
		return get(requestMemberID, targetMemberID, LocalDateTime.now());
	}

	ProfileResponse get(Long requestMemberID, Long targetMemberID, LocalDateTime baseDateTime);
}
