package fr.uha.AccountingFlowManager.enums;


import lombok.Getter;

@Getter
public enum TransactionType {
    DEBIT("Debit");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }
}
