package org.coinalert.coinalertappserver.Service;

import lombok.RequiredArgsConstructor;
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

    public PriceData savePriceData(PriceData priceData, String username) {
        Optional<Member> memberOptional = memberRepository.findByEmail(username)
                .or(() -> memberRepository.findByOauth2Id(Long.valueOf(username)));

            Member member = memberOptional.get();
            priceData.setMember(member);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            priceData.setDate(LocalDateTime.now().format(formatter));

            return priceDataRepository.save(priceData);

    }

    public List<PriceData> getPriceData(String username) {
        Optional<Member> memberOptional = memberRepository.findByEmail(username)
                .or(() -> memberRepository.findByOauth2Id(Long.valueOf(username)));

        if(memberOptional.isPresent()) {
            Member member =  memberOptional.get();
            return priceDataRepository.findByMember_Id(member.getId());
        }else {
            return Collections.emptyList();
        }
    }

    public void deletePriceData(Long id) {
        priceDataRepository.deleteById(id);
    }
}
