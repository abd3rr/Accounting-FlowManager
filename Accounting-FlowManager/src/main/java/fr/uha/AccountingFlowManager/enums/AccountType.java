package fr.uha.AccountingFlowManager.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    BALANCE("Balance");

    private final String displayName;


    AccountType(String displayName) {
        this.displayName = displayName;
    }

}