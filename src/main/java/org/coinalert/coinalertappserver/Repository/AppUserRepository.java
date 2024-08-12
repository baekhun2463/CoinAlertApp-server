package org.coinalert.coinalertappserver.Repository;

import org.coinalert.coinalertappserver.Model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
//    Optional<User> findByEmail(String email);
//    Optional<AppUser> findByNickName(String nickName);

    List<AppUser> findByEmail(String email);
}
