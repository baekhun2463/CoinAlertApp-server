package org.coinalert.coinalertappserver.Service;

import lombok.RequiredArgsConstructor;
import org.coinalert.coinalertappserver.Exception.UserNotFoundException;
import org.coinalert.coinalertappserver.Model.Member;
import org.coinalert.coinalertappserver.Model.PriceData;
import org.coinalert.coinalertappserver.Repository.MemberRepository;
import org.coinalert.coinalertappserver.Repository.PriceDataRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PriceDataService {

    private final PriceDataRepository priceDataRepository;
    private final MemberRepository memberRepository;

    //Optional 체이닝
    //Optional을 사용하는 코드에서 isPresent()를 사용하지 않아도 문제가 생기지 않은 이유는
    //orElseGet()과 같은 메서드가 Optional이 비어있을 때만 실행되기 때문.
    //반대로 값이 있는 경우에는 값을 바로 반환.
    //이 방식을 사용하면 isPresent()를 호출해서 값이 있는지 확인하고 처리를 따로 안해도됨
    //요약 하면 orElseThrow()는 값이 없으면 에외를 던지고 있으면 반환
    public Member findMember(String identifier) throws UserNotFoundException {
        return memberRepository.findByEmail(identifier)
                .orElseGet(() -> memberRepository.findByOauth2Id(Long.valueOf(identifier))
                        .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.")));
    }

    public PriceData savePriceData(PriceData priceData, String username) {
            Member member = findMember(username);
            priceData.setMember(member);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            priceData.setDate(LocalDateTime.now().format(formatter));

            return priceDataRepository.save(priceData);

    }

    public List<PriceData> getPriceData(String username) {
        Member member = findMember(username);
        return priceDataRepository.findByMember_Id(member.getId());
    }

    public void deletePriceData(Long id) {
        priceDataRepository.deleteById(id);
    }
}
