package fr.uha.AccountingFlowManager.service;

import fr.uha.AccountingFlowManager.model.Invoice;
import fr.uha.AccountingFlowManager.model.InvoiceLine;
import fr.uha.AccountingFlowManager.model.ProductCatalog;
import fr.uha.AccountingFlowManager.repository.InvoiceLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceLineService {


    private final InvoiceLineRepository invoiceLineRepository;

    @Autowired
    public InvoiceLineService(InvoiceLineRepository invoiceLineRepository) {
        this.invoiceLineRepository = invoiceLineRepository;
    }

    @Transactional
    public InvoiceLine createInvoiceLine(Invoice invoice, ProductCatalog product, int quantity, double unitPrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        if (unitPrice <= 0) {
            throw new IllegalArgumentException("Unit price must be positive.");
        }

        InvoiceLine line = new InvoiceLine();
        line.setInvoice(invoice);
        line.setProduct(product);
        line.setQuantity(quantity);
        line.setPrice(unitPrice);
        line.calculateTotal();  // Ensure total is calculated based on quantity and price
        return invoiceLineRepository.save(line);
    }
}
