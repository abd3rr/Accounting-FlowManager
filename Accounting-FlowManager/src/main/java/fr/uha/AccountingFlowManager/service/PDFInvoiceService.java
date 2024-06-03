package fr.uha.AccountingFlowManager.service;

import fr.uha.AccountingFlowManager.dto.invoice.InvoiceDisplayDTO;
import fr.uha.AccountingFlowManager.dto.invoice.InvoiceLineDisplayDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class PDFInvoiceService {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    private PDType1Font regularFont;
    private PDType1Font boldFont;

    public PDFInvoiceService() {
        // Use built-in fonts
        regularFont = PDType1Font.HELVETICA;
        boldFont = PDType1Font.HELVETICA_BOLD;
    }

    public byte[] createInvoicePDF(InvoiceDisplayDTO invoiceDTO) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setLeading(14.5f);

                // Title
                contentStream.beginText();
                contentStream.setFont(boldFont, 18);
                contentStream.newLineAtOffset(100, 750);
                contentStream.showText("Facture N°: " + cleanText(String.valueOf(invoiceDTO.getInvoiceId())));
                contentStream.endText();

                // Header
                drawHeader(contentStream, invoiceDTO);

                // Invoice Items Table
                drawInvoiceTable(contentStream, invoiceDTO);

                // Summary
                drawSummary(contentStream, invoiceDTO, 150);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    private void drawHeader(PDPageContentStream contentStream, InvoiceDisplayDTO invoiceDTO) throws IOException {
        int top = 700;

        // Client information
        contentStream.beginText();
        contentStream.setFont(regularFont, 12);
        contentStream.newLineAtOffset(50, top);
        contentStream.showText("Client: " + cleanText(invoiceDTO.getCustomerName()));
        contentStream.newLine();
        contentStream.showText("Adresse: " + cleanText(invoiceDTO.getCustomerAddress()));
        contentStream.newLine();
        contentStream.showText("Pays: " + cleanText(invoiceDTO.getCustomerCountry()));
        contentStream.newLine();
        contentStream.showText("Email: " + cleanText(invoiceDTO.getCustomerEmail()));
        contentStream.endText();

        // Provider information
        contentStream.beginText();
        contentStream.newLineAtOffset(300, top);
        contentStream.showText("Fournisseur: " + cleanText(invoiceDTO.getProviderName()));
        contentStream.newLine();
        contentStream.showText("Adresse: " + cleanText(invoiceDTO.getProviderAddress()));
        contentStream.newLine();
        contentStream.showText("Pays: " + cleanText(invoiceDTO.getProviderCountry()));
        contentStream.newLine();
        contentStream.showText("Email: " + cleanText(invoiceDTO.getProviderEmail()));
        contentStream.endText();
    }

    private void drawInvoiceTable(PDPageContentStream contentStream, InvoiceDisplayDTO invoiceDTO) throws IOException {
        int startY = 550;
        contentStream.setFont(boldFont, 12);
        String[] headers = {"Produit", "Quantité", "Prix Unitaire", "Total"};
        int[] columnWidths = {200, 100, 100, 100};
        int currentX = 50;

        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(currentX, startY);
            contentStream.showText(headers[i]);
            contentStream.endText();
            contentStream.moveTo(currentX, startY - 15);
            contentStream.lineTo(currentX + columnWidths[i], startY - 15);
            contentStream.stroke();
            currentX += columnWidths[i];
        }

        startY -= 20;
        contentStream.setFont(regularFont, 12);
        for (InvoiceLineDisplayDTO line : invoiceDTO.getLines()) {
            currentX = 50;
            String[] lineDetails = {
                    cleanText(line.getProductName()),
                    cleanText(String.valueOf(line.getQuantity())),
                    DECIMAL_FORMAT.format(line.getUnitPrice()) + " €",
                    DECIMAL_FORMAT.format(line.getTotal()) + " €"
            };
            for (int i = 0; i < lineDetails.length; i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(currentX, startY);
                contentStream.showText(lineDetails[i]);
                contentStream.endText();
                currentX += columnWidths[i];
            }
            startY -= 20;
        }
    }

    private void drawSummary(PDPageContentStream contentStream, InvoiceDisplayDTO invoiceDTO, int startY) throws IOException {
        contentStream.beginText();
        contentStream.setFont(boldFont, 12);
        contentStream.newLineAtOffset(50, startY);
        contentStream.showText(cleanText("Résumé de la Facture:"));
        contentStream.endText();

        startY -= 20;
        String[] labels = {"Sous-total:", "Remise:", "TVA:", "Total:"};
        String[] values = {
                cleanText(CURRENCY_FORMAT.format(invoiceDTO.getSubtotal())),
                cleanText(CURRENCY_FORMAT.format(invoiceDTO.getDiscount())),
                cleanText(invoiceDTO.getVat() + "% (" + CURRENCY_FORMAT.format(invoiceDTO.getSubtotal() * invoiceDTO.getVat() / 100) + ")"),
                cleanText(CURRENCY_FORMAT.format(invoiceDTO.getTotal()))
        };

        for (int i = 0; i < labels.length; i++) {
            contentStream.beginText();
            contentStream.setFont(regularFont, 12);
            contentStream.newLineAtOffset(50, startY);
            contentStream.showText(cleanText(labels[i]) + " " + cleanText(values[i]));
            contentStream.endText();
            startY -= 20;
        }
    }

    private String cleanText(String input) {
        if (input == null) {
            return "";
        }
        // Replace narrow no-break spaces and non-breaking spaces with regular spaces
        input = input.replaceAll("[\\u202F\\u00A0]", " ");
        // Remove characters not supported by WinAnsiEncoding
        StringBuilder sanitized = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (WinAnsiEncoding.INSTANCE.contains(c)) {
                sanitized.append(c);
            } else {
                sanitized.append(' '); // Replace unsupported characters with a space
            }
        }
        return sanitized.toString();
    }

}
