package fr.uha.AccountingFlowManager.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    OUTSTANDING_RECEIVABLES("Outstanding Receivables"),
    ADVANCE_PAYMENTS("Advance Payments");

    private final String displayName;


    AccountType(String displayName) {
        this.displayName = displayName;
    }

}