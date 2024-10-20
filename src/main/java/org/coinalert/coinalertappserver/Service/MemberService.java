package org.coinalert.coinalertappserver.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.DTO.MemberDataResponseDTO;
import org.coinalert.coinalertappserver.DTO.MemberResponseDTO;
import org.coinalert.coinalertappserver.Exception.DuplicateNicknameException;
import org.coinalert.coinalertappserver.Exception.UnauthorizedException;
import org.coinalert.coinalertappserver.Exception.UserNotFoundException;
import org.coinalert.coinalertappserver.Model.Comment;
import org.coinalert.coinalertappserver.Model.Member;
import org.coinalert.coinalertappserver.Model.Post;
import org.coinalert.coinalertappserver.Repository.CommentRepository;
import org.coinalert.coinalertappserver.Repository.MemberRepository;
import org.coinalert.coinalertappserver.Repository.PostRepository;
import org.coinalert.coinalertappserver.Util.JwtUtil;
import org.coinalert.coinalertappserver.Util.Role;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public boolean checkIfUserExistsAndRegister(Member member) {
        if(memberRepository.findByEmail(member.getEmail()).isPresent()) {
            return false;
        }
        registerUser(member);
        return true;
    }

    //Optional 체이닝
    //Optional을 사용하는 코드에서 isPresent()를 사용하지 않아도 문제가 생기지 않은 이유는
    //orElseGet()과 같은 메서드가 Optional이 비어있을 때만 실행되기 때문.
    //반대로 값이 있는 경우에는 값을 바로 반환.
    //이 방식을 사용하면 isPresent()를 호출해서 값이 있는지 확인하고 처리를 따로 안해도됨
    //요약 하면 orElseThrow()는 값이 없으면 에외를 던지고 있으면 반환
    public Member findMember(String identifier) throws UserNotFoundException{
        log.info("Looking for user with identifier: {}", identifier);

        return memberRepository.findByEmail(identifier)
                .orElseGet(() -> memberRepository.findByOauth2Id(Long.valueOf(identifier))
                        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.")));
    }

    public Member registerUser(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setCreatedAt(LocalDateTime.now());
        member.setRole(Role.USER);
        return memberRepository.save(member);
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByEmail(username)
                .orElseGet(() -> memberRepository.findByOauth2Id(Long.valueOf(username))
                        .orElse(null));
    }

    public String authenticateAndGenerateToken(Member member, Authentication authentication) throws BadCredentialsException {

        Optional<Member> optionalMember = memberRepository.findByEmail(member.getEmail());

        Member foundMember = optionalMember.orElseThrow(() ->
                new UsernameNotFoundException("존재하지 않은 회원입니다."));

        foundMember.setLastLogin(LocalDateTime.now());

        memberRepository.save(foundMember);

        return jwtUtil.generateToken(authentication);
    }

    public void deleteMember(UserDetails userDetails) {
        if(userDetails == null) {
            throw new UnauthorizedException("인증되지 않은 사용자");
        }

        String identifier = userDetails.getUsername();

        Member member = findMember(identifier);

        memberRepository.delete(member);
    }

    public Optional<String> findEmailByNickname(String nickName) {
        return memberRepository.findByNickname(nickName)
                .map(Member::getEmail);
    }


    public void resetPassword(String email, String newPassword) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("이메일을 찾을 수 없습니다."));
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    public void updateNickname(String newNickname, String currentUsername) {
        if(memberRepository.existsByNickname(newNickname)) {
            throw new DuplicateNicknameException("중복된 닉네임이 있습니다.");
        }

        Member member = findMember(currentUsername);

        member.setNickname(newNickname);
        memberRepository.save(member);
    }

    public MemberResponseDTO getNickname(String username) {
        Member member = findMember(username);
        return new MemberResponseDTO(member.getNickname(), member.getId());
    }

    public MemberDataResponseDTO getMemberData(String username) {
        Member member = findMember(username);

        log.info("Fetched Member Data: {}", member.getAvatar_url());

        List<Post> posts = postRepository.findByMember(member);
        List<Comment> comments = commentRepository.findByAuthor(member.getNickname());

        return new MemberDataResponseDTO(
                member.getNickname(), member.getAvatar_url(), posts, comments
        );
    }

    public void updateAvatarUrl(String username, String newAvatarUrl) throws UserNotFoundException {
        Member member = findMember(username);
        member.setAvatar_url(newAvatarUrl);
        memberRepository.save(member);
    }

}
