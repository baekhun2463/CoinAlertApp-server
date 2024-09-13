package org.coinalert.coinalertappserver.Repository;

import org.coinalert.coinalertappserver.Model.Member;
import org.coinalert.coinalertappserver.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByMember(Member member);
}
