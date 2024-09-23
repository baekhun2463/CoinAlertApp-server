//package org.coinalert.coinalertappserver.FeignClientInterface;
//
//import org.coinalert.coinalertappserver.Model.Post;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.*;
//
//@FeignClient(name = "post-service")
//public interface PostServiceClient {
//
//    @GetMapping("/posts/{id}")
//    Post getPostById(@PathVariable("id")Long id);
//
//    @PostMapping("/posts")
//    Post createPost(@RequestBody Post post);
//
//    @PutMapping("/posts/{id}/like")
//    void toggleLike(@PathVariable("id") Long postId, @RequestParam("isLiked") boolean isLiked);
//}
