package me.trihung.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankInfo {
    private String accountHolder;
    private String accountNumber;
    private String bankName;
    private String bankBranch;
}