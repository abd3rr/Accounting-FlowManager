package fr.uha.AccountingFlowManager.enums;

public enum Currency {
    USD("United States Dollar"),
    RWF("Rwandan Francs"),
    EUR("Euro"),
    CAD("Canadian Dollar"),
    MAD("Moroccan Dirham");

    private final String displayName;

    Currency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

