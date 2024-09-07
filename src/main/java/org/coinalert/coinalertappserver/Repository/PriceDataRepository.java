package org.coinalert.coinalertappserver.Repository;

import org.coinalert.coinalertappserver.Model.Member;
import org.coinalert.coinalertappserver.Model.PriceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceDataRepository extends JpaRepository<PriceData, Long> {
    List<PriceData> findByMember_Id(Long memberId);
}
