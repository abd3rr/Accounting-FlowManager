package fr.uha.AccountingFlowManager.enums;

import lombok.Getter;

@Getter
public enum ReductionType {
    NONE("Aucune Réduction Additionnelle"),
    FINANCIERE("Réduction financière"),
    COMMERCIALE("Réduction commerciale");

    private final String displayName;

    ReductionType(String displayName) {
        this.displayName = displayName;
    }


}
