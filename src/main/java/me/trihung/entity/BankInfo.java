package me.trihung.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
//Viết riêng được clean dễ nhìn khi được nhúng vào entity khác các field sẽ được gộp chung 
//với entity đó luôn
public class BankInfo {
    private String accountHolder;
    private String accountNumber;
    private String bankName;
    private String bankBranch;
}