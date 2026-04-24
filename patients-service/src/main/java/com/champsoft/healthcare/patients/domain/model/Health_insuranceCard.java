//package com.champsoft.healthcare.patients.domain.model;
//
//import com.champsoft.healthcare.patients.domain.exception.ExpiredHealthInsuranceCardException;
//import com.champsoft.healthcare.patients.domain.exception.InvalidInsuranceCardNumber;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//
//@NoArgsConstructor
//public class Health_insuranceCard {
//    private  String healthCardNum;
//    @Getter
//    private  LocalDate expiryDate;
//
//
//    public Health_insuranceCard(String card, LocalDate expiryDate){
//        if(card == null || !card.matches("[A-Za-z]{4}\\d{8}"))
//            throw new InvalidInsuranceCardNumber("Health insurance card invalid, wrong format\n correct format: BASM 4567 8907");
//        String healthCard = card.trim().toUpperCase();
//
//
//        LocalDate today= LocalDate.now();
//        if(expiryDate.isBefore(today))
//            throw new ExpiredHealthInsuranceCardException("Health insurance card invalid(reason: it is expired)");
//
//        this.healthCardNum=healthCard;
//        this.expiryDate=expiryDate;
//    }
//
//
//    public String insuranceCardNumber(){
//        return healthCardNum;
//    }
//
//
//}
