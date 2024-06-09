package fr.uha.AccountingFlowManager.dto;

import fr.uha.AccountingFlowManager.enums.TransactionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransactionsDTO {
    private Double balance;
    private List<TransactionDTO> transactions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionDTO {
        private LocalDateTime date;
        private Double amount;
        private TransactionType transactionType;
        private String description;
    }
}