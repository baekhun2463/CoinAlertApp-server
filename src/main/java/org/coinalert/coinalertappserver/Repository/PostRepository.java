package org.coinalert.coinalertappserver.Repository;

import org.coinalert.coinalertappserver.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
