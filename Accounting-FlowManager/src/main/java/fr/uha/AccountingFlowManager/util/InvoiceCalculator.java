package fr.uha.AccountingFlowManager.utils;

import fr.uha.AccountingFlowManager.dto.invoice.InvoiceFormDataDTO;
import fr.uha.AccountingFlowManager.enums.ReductionType;
import fr.uha.AccountingFlowManager.enums.ShippingCostType;
import fr.uha.AccountingFlowManager.exception.InvoiceExceptions.*;

public class InvoiceCalculator {

    private static final double COMMERCIAL_REDUCTION_PERCENTAGE = 0.01;
    private static final double FINANCIAL_REDUCTION_PERCENTAGE = 0.01;
    private static final int FINANCIAL_REDUCTION_THRESHOLD = 5;
    private static final double FLAT_RATE_SHIPPING_COST = 5.0;
    private static final double PER_ITEM_SHIPPING_COST = 0.10;
    private static final double VAT_RATE = 0.15;
    private static final int[] ALLOWED_REDUCTIONS = {0, 10, 15, 20};

    public static double calculateTotalReduction(InvoiceFormDataDTO invoiceData) {
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

        // Validate and calculate financial reduction based on quantity
        if (invoiceData.getAdditionalReduction().equals(ReductionType.FINANCIERE.name())) {
            int totalQuantity = invoiceData.getProducts().stream().mapToInt(InvoiceFormDataDTO.ProductInvoiceForm::getQuantity).sum();
            if (totalQuantity > FINANCIAL_REDUCTION_THRESHOLD) {
                reduction += FINANCIAL_REDUCTION_PERCENTAGE;
            }
        } else if (invoiceData.getAdditionalReduction().equals(ReductionType.COMMERCIALE.name())) {
            // Calculate commercial reduction based on payment type
            reduction += COMMERCIAL_REDUCTION_PERCENTAGE;
        } else if (!invoiceData.getAdditionalReduction().equals("NA")) {
            throw new InvalidAdditionalReductionException("Invalid additional reduction type: " + invoiceData.getAdditionalReduction());
        }

        return reduction;
    }

    public static double calculateShippingCost(InvoiceFormDataDTO invoiceData) {
        double shippingCost = 0.0;

        try {
            switch (ShippingCostType.valueOf(invoiceData.getShippingCostType())) {
                case PROVIDER_PAYS:
                    shippingCost = 0.0;
                    break;
                case FLAT_RATE:
                    shippingCost = FLAT_RATE_SHIPPING_COST;
                    break;
                case FULL_BILLING:
                    int totalQuantity = invoiceData.getProducts().stream().mapToInt(InvoiceFormDataDTO.ProductInvoiceForm::getQuantity).sum();
                    shippingCost = totalQuantity * PER_ITEM_SHIPPING_COST;
                    break;
                default:
                    throw new InvalidShippingCostTypeException("Invalid shipping cost type: " + invoiceData.getShippingCostType());
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidShippingCostTypeException("Invalid shipping cost type: " + invoiceData.getShippingCostType());
        }

        return shippingCost;
    }

    public static double calculateTotalPrice(InvoiceFormDataDTO invoiceData) {
        double totalPrice = invoiceData.getProducts().stream()
                .mapToDouble(product -> product.getQuantity() * product.getPrice())
                .sum();

        double reduction = calculateTotalReduction(invoiceData);
        double shippingCost = calculateShippingCost(invoiceData);

        totalPrice = totalPrice * (1 - reduction);
        totalPrice += shippingCost;
        totalPrice = totalPrice * (1 + VAT_RATE);

        return totalPrice;
    }
}
