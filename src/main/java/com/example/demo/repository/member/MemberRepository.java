package com.example.demo.repository.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.member.MemberImpl;

public interface MemberRepository extends JpaRepository<MemberImpl, Long> {
	Optional<MemberImpl> findByEmail(String email);
}
