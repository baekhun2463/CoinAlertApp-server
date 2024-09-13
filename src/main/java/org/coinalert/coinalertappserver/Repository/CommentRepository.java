package org.coinalert.coinalertappserver.Repository;

import org.coinalert.coinalertappserver.Model.Comment;
import org.coinalert.coinalertappserver.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    List<Comment> findByAuthor(String nickname);
}
