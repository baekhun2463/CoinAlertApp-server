package org.coinalert.coinalertappserver.Repository;

import org.coinalert.coinalertappserver.Model.OAuth2Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuth2MemberRepository extends JpaRepository<OAuth2Member, Long> {
    Optional<OAuth2Member> findByEmail(String email);
}