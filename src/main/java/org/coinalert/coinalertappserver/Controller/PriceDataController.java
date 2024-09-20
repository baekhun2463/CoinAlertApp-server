package org.coinalert.coinalertappserver.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.Model.PriceData;
import org.coinalert.coinalertappserver.Service.PriceDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PriceDataController {

    private final PriceDataService priceDataService;

    @PostMapping("/savePriceData")
    public ResponseEntity<?> savePriceData(@RequestBody PriceData priceData,
                                           @AuthenticationPrincipal UserDetails userDetails) {

        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        try {
            PriceData savedPriceData = priceDataService.savePriceData(priceData, userDetails.getUsername());
            return ResponseEntity.ok(savedPriceData);
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("알람 저장 중에 오류가 발생했습니다.");
        }
    }

    @GetMapping("/priceData")
    public ResponseEntity<?> getPriceData(@AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        String username = userDetails.getUsername();
        List<PriceData> priceDataList = priceDataService.getPriceData(username);


        return ResponseEntity.ok(priceDataList);
    }

    @DeleteMapping("/deletePriceData/{id}")
    public ResponseEntity<Void> deletePriceData(@PathVariable Long id) {
        try {
            priceDataService.deletePriceData(id);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
