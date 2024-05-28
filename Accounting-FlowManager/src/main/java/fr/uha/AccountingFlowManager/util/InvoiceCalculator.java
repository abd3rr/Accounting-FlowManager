package fr.uha.AccountingFlowManager.util;

import fr.uha.AccountingFlowManager.dto.invoice.InvoiceFormDataDTO;
import fr.uha.AccountingFlowManager.enums.ReductionType;
import fr.uha.AccountingFlowManager.enums.ShippingCostType;
import fr.uha.AccountingFlowManager.exception.InvoiceExceptions.*;
import org.springframework.stereotype.Component;

@Component
public class InvoiceCalculator {

    private static final double COMMERCIAL_REDUCTION_PERCENTAGE = 0.01;
    private static final double FINANCIAL_REDUCTION_PERCENTAGE = 0.01;
    private static final int FINANCIAL_REDUCTION_THRESHOLD = 5;
    private static final double FLAT_RATE_SHIPPING_COST = 5.0;
    private static final double PER_ITEM_SHIPPING_COST = 0.10;
    private static final double VAT_RATE = 0.15;
    private static final int[] ALLOWED_REDUCTIONS = {0, 10, 15, 20};

    public static double calculateReduction(InvoiceFormDataDTO invoiceData) {
        double reduction = 0.0;

        // Validate specified reduction
        int specifiedReduction;
        try {
            specifiedReduction = Integer.parseInt(invoiceData.getReduction());
        } catch (NumberFormatException e) {
            throw new InvalidReductionException("Invalid reduction percentage format: " + invoiceData.getReduction());
        }

        boolean isValidReduction = false;
        for (int allowedReduction : ALLOWED_REDUCTIONS) {
            if (specifiedReduction == allowedReduction) {
                isValidReduction = true;
                break;
            }
        }
        if (!isValidReduction) {
            throw new InvalidReductionException("Invalid reduction percentage: " + specifiedReduction);
        }

        reduction += specifiedReduction / 100.0;
        return reduction;
    }

    public static double calculateAdditionalReduction(InvoiceFormDataDTO invoiceData) {
        double additionalReduction = 0.0;

        // Calculate additional reduction based on the type
        if (invoiceData.getAdditionalReduction().equals(ReductionType.FINANCIERE)) {
            int totalQuantity = invoiceData.getProducts().stream().mapToInt(InvoiceFormDataDTO.ProductInvoiceForm::getQuantity).sum();
            if (totalQuantity > FINANCIAL_REDUCTION_THRESHOLD) {
                additionalReduction += FINANCIAL_REDUCTION_PERCENTAGE;
            }
        } else if (invoiceData.getAdditionalReduction().equals(ReductionType.COMMERCIALE)) {
            additionalReduction += COMMERCIAL_REDUCTION_PERCENTAGE;
        } else if (invoiceData.getAdditionalReduction().equals(ReductionType.NONE)) {
            // No additional reduction
        } else {
            throw new InvalidAdditionalReductionException("Invalid additional reduction type: " + invoiceData.getAdditionalReduction());
        }

        return additionalReduction;
    }

    public static double calculateTotalReduction(InvoiceFormDataDTO invoiceData) {
        double reduction = calculateReduction(invoiceData);
        double additionalReduction = calculateAdditionalReduction(invoiceData);
        return reduction + additionalReduction;
    }

    public static double calculateShippingCost(InvoiceFormDataDTO invoiceData) {
        double shippingCost = 0.0;

        try {
            shippingCost = switch (invoiceData.getShippingCostType()) {
                case PROVIDER_PAYS -> 0.0;
                case FLAT_RATE -> FLAT_RATE_SHIPPING_COST;
                case FULL_BILLING -> {
                    int totalQuantity = invoiceData.getProducts().stream().mapToInt(InvoiceFormDataDTO.ProductInvoiceForm::getQuantity).sum();
                    yield totalQuantity * PER_ITEM_SHIPPING_COST;
                }
            };
        } catch (IllegalArgumentException e) {
            throw new InvalidShippingCostTypeException("Invalid shipping cost type: " + invoiceData.getShippingCostType());
        }

        return shippingCost;
    }

    public static double calculateTotalPrice(InvoiceFormDataDTO invoiceData) {
        double totalPrice = invoiceData.getProducts().stream()
                .mapToDouble(product -> product.getQuantity() * product.getPrice())
                .sum();

        double totalReduction = calculateTotalReduction(invoiceData);
        double shippingCost = calculateShippingCost(invoiceData);

        totalPrice = totalPrice * (1 - totalReduction);
        totalPrice += shippingCost;
        totalPrice = totalPrice * (1 + VAT_RATE);
        totalPrice -= invoiceData.getAdvancePaymentAsDouble(); // Subtract the advance payment from the total price

        return totalPrice;
    }
}
