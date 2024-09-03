package org.coinalert.coinalertappserver.Repository;

import org.coinalert.coinalertappserver.Model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByOauth2Id(Long oAuth2Id);
}
