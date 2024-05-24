package fr.uha.AccountingFlowManager.enums;

import lombok.Getter;

@Getter
public enum ReductionType {
    FINANCIERE("Réduction financière"),
    COMMERCIALE("Réduction commerciale");

    private final String displayName;

    ReductionType(String displayName) {
        this.displayName = displayName;
    }


}
