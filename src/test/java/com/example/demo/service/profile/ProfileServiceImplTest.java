package com.example.demo.service.profile;

import static com.example.demo.exception.ExceptionType.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.domain.cafe.Cafe;
import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.ThumbnailImage;
import com.example.demo.domain.study.StudyOnce;
import com.example.demo.dto.profile.ProfileGetResponse;
import com.example.demo.dto.profile.ProfileUpdateRequest;
import com.example.demo.dto.profile.ProfileUpdateResponse;
import com.example.demo.exception.CafegoryException;
import com.example.demo.helper.CafeSaveHelper;
import com.example.demo.helper.MemberSaveHelper;
import com.example.demo.helper.StudyOnceSaveHelper;
import com.example.demo.helper.ThumbnailImageSaveHelper;
import com.example.demo.service.ServiceTest;
import com.example.demo.service.study.StudyOnceService;

class ProfileServiceImplTest extends ServiceTest {

	private static final LocalDateTime NOW = LocalDateTime.now();

	@Autowired
	private ProfileService sut;
	@Autowired
	private StudyOnceService studyOnceService;
	@Autowired
	private CafeSaveHelper cafeSaveHelper;
	@Autowired
	private MemberSaveHelper memberSaveHelper;
	@Autowired
	private StudyOnceSaveHelper studyOnceSaveHelper;
	@Autowired
	private ThumbnailImageSaveHelper thumbnailImageSaveHelper;

	@Test
	@DisplayName("카공장이 카공원의 프로필을 조회한다.")
	void leader_can_view_member_profile() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMember(thumbnailImage);
		Member member = memberSaveHelper.saveMemberWithName(thumbnailImage, "회원");
		Cafe cafe = cafeSaveHelper.saveCafe();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnce(cafe, leader);
		studyOnceService.tryJoin(member.getId(), studyOnce.getId());
		//when
		ProfileGetResponse response = sut.get(leader.getId(), member.getId());
		//then
		assertThat(response.getName()).isEqualTo("회원");
	}

	@Test
	@DisplayName("카공이 시작되면 카공원은 참여자들의 프로필을 조회할 수 있다.")
	void member_can_view_profiles_of_participants_when_study_begins() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMemberWithName(thumbnailImage, "카공장");
		Member member = memberSaveHelper.saveMemberWithName(thumbnailImage, "카공원");
		Cafe cafe = cafeSaveHelper.saveCafe();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe, leader, NOW.plusHours(4),
			NOW.plusHours(5));
		studyOnceService.tryJoin(member.getId(), studyOnce.getId());
		//when
		ProfileGetResponse response = sut.get(member.getId(), leader.getId(), NOW.plusHours(4));
		//then
		assertThat(response.getName()).isEqualTo("카공장");
	}

	@Test
	@DisplayName("카공이 시작되기전에는 카공원은 참여자들의 프로필을 조회할 수 없다.")
	void member_cannot_view_profiles_of_participants_before_study_begins() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member leader = memberSaveHelper.saveMemberWithName(thumbnailImage, "카공장");
		Member member = memberSaveHelper.saveMemberWithName(thumbnailImage, "카공원");
		Cafe cafe = cafeSaveHelper.saveCafe();
		StudyOnce studyOnce = studyOnceSaveHelper.saveStudyOnceWithTime(cafe, leader, NOW.plusHours(4),
			NOW.plusHours(5));
		studyOnceService.tryJoin(member.getId(), studyOnce.getId());
		//then
		assertThatThrownBy(() -> sut.get(member.getId(), leader.getId(), NOW))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(PROFILE_GET_PERMISSION_DENIED.getErrorMessage());
	}

	@Test
	@DisplayName("자신의 프로필을 조회한다.")
	void view_own_profile() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member member = memberSaveHelper.saveMemberWithName(thumbnailImage, "멤버");
		//when
		ProfileGetResponse response = sut.get(member.getId(), member.getId());
		//then
		assertThat(response.getName()).isEqualTo("멤버");
	}

	@Test
	@DisplayName("자신의 프로필을 수정한다.")
	void update_own_profile() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member member = memberSaveHelper.saveMember(thumbnailImage);
		ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest("name", "introduction");
		//when
		ProfileUpdateResponse response = sut.update(member.getId(), member.getId(), profileUpdateRequest);
		//then
		assertThat(response.getName()).isEqualTo("name");
	}

	@Test
	@DisplayName("타인의 프로필을 수정할 수 없다.")
	void member_can_not_update_another_members_profile() {
		//given
		ThumbnailImage thumbnailImage = thumbnailImageSaveHelper.saveThumbnailImage();
		Member member1 = memberSaveHelper.saveMember(thumbnailImage);
		Member member2 = memberSaveHelper.saveMember(thumbnailImage);
		ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest("name", "introduction");
		//then
		assertThatThrownBy(() -> sut.update(member1.getId(), member2.getId(), profileUpdateRequest))
			.isInstanceOf(CafegoryException.class)
			.hasMessage(PROFILE_UPDATE_PERMISSION_DENIED.getErrorMessage());
	}
}
