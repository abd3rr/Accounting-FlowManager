package fr.uha.AccountingFlowManager.enums;

import lombok.Getter;

@Getter
public enum Currency {
    USD("United States Dollar", "$", "USD"),
    EUR("Euro", "€", "EUR"),
    JPY("Japanese Yen", "¥", "JPY"),
    GBP("British Pound", "£", "GBP"),
    AUD("Australian Dollar", "A$", "AUD"),
    CAD("Canadian Dollar", "C$", "CAD"),
    CHF("Swiss Franc", "Fr", "CHF"),
    CNY("Chinese Yuan", "¥", "CNY"),
    SEK("Swedish Krona", "kr", "SEK"),
    NZD("New Zealand Dollar", "$", "NZD"),
    MXN("Mexican Peso", "$", "MXN"),
    SGD("Singapore Dollar", "$", "SGD"),
    HKD("Hong Kong Dollar", "$", "HKD"),
    NOK("Norwegian Krone", "kr", "NOK"),
    KRW("South Korean Won", "₩", "KRW"),
    RWF("Rwandan Francs", "Fr", "RWF"),
    MAD("Moroccan Dirham", "د.م.", "MAD");

    private final String name;
    private final String symbol;
    private final String code;

    Currency(String name, String symbol, String code) {
        this.name = name;
        this.symbol = symbol;
        this.code = code;
    }

    public String getDisplayName() {
        return code + " - " +name + " ("+symbol+")";
    }
}

