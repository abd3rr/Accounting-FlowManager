package fr.uha.AccountingFlowManager.enums;

import lombok.Getter;

@Getter
public enum ShippingCostType {
    PROVIDER_PAYS("Fournisseur paie"),
    FLAT_RATE("Tarif forfaitaire"),
    FULL_BILLING("Facturation intégrale");

    private final String displayName;

    ShippingCostType(String displayName) {
        this.displayName = displayName;
    }


}
