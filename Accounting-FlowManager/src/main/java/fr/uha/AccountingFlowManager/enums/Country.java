package fr.uha.AccountingFlowManager.enums;

import lombok.Getter;

@Getter
public enum Country {
    USA("United States"),
    RWANDA("Rwanda"),
    CANADA("Canada"),
    UK("United Kingdom"),
    GERMANY("Germany"),
    FRANCE("France"),
    SPAIN("Spain"),
    ITALY("Italy"),
    NETHERLANDS("Netherlands"),
    BELGIUM("Belgium"),
    SWITZERLAND("Switzerland"),
    SWEDEN("Sweden"),
    NORWAY("Norway"),
    DENMARK("Denmark"),
    FINLAND("Finland"),
    PORTUGAL("Portugal"),
    AUSTRIA("Austria"),
    GREECE("Greece"),
    POLAND("Poland"),
    CZECH_REPUBLIC("Czech Republic"),
    HUNGARY("Hungary"),
    MOROCCO("Morocco"),
    DEFAULT("France");
    private final String displayName;

    Country(String displayName) {
        this.displayName = displayName;
    }

    public static Country fromString(String name) {
        for (Country country : Country.values()) {
            if (country.name().equalsIgnoreCase(name)) {
                return country;
            }
        }
        return DEFAULT;
    }
}
