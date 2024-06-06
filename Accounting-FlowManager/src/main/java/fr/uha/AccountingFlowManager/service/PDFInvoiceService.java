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
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PDFInvoiceService {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Facture N°: " + cleanText(String.valueOf(invoiceDTO.getInvoiceId())));
                contentStream.endText();

                // Issue Date
                contentStream.beginText();
                contentStream.setFont(regularFont, 12);
                contentStream.newLineAtOffset(400, 750);
                contentStream.showText("Date d'émission: " + cleanText(invoiceDTO.getIssueDate().format(DATE_FORMATTER)));
                contentStream.endText();

                // Header
                drawHeader(contentStream, invoiceDTO);

                // Invoice Items Table
                drawInvoiceTable(contentStream, invoiceDTO);

                // Summary
                drawSummary(contentStream, invoiceDTO, 120);
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
        contentStream.setFont(boldFont, 12);
        contentStream.newLineAtOffset(50, top);
        contentStream.showText("Facturé à :");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(regularFont, 12);
        contentStream.newLineAtOffset(50, top - 20);
        contentStream.showText(cleanText(invoiceDTO.getCustomerName()));
        contentStream.newLine();
        contentStream.showText(cleanText(invoiceDTO.getCustomerAddress()));
        contentStream.newLine();
        contentStream.showText(cleanText(invoiceDTO.getCustomerCountry()));
        contentStream.newLine();
        contentStream.showText(cleanText(invoiceDTO.getCustomerEmail()));
        contentStream.endText();

        // Provider information
        contentStream.beginText();
        contentStream.setFont(boldFont, 12);
        contentStream.newLineAtOffset(300, top);
        contentStream.showText("Facturé par :");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(regularFont, 12);
        contentStream.newLineAtOffset(300, top - 20);
        contentStream.showText(cleanText(invoiceDTO.getProviderName()));
        contentStream.newLine();
        contentStream.showText(cleanText(invoiceDTO.getProviderAddress()));
        contentStream.newLine();
        contentStream.showText(cleanText(invoiceDTO.getProviderCountry()));
        contentStream.newLine();
        contentStream.showText(cleanText(invoiceDTO.getProviderEmail()));
        contentStream.endText();
    }

    private void drawInvoiceTable(PDPageContentStream contentStream, InvoiceDisplayDTO invoiceDTO) throws IOException {
        int startY = 550;
        contentStream.setFont(boldFont, 12);
        String[] headers = {"Produit", "Quantité", "Prix Unitaire", "Total"};
        int[] columnWidths = {200, 100, 100, 100};
        int currentX = 50;

        // Draw table headers
        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(currentX, startY);
            contentStream.showText(headers[i]);
            contentStream.endText();
            currentX += columnWidths[i];
        }

        // Draw table header lines
        contentStream.moveTo(50, startY - 5);
        contentStream.lineTo(50 + columnWidths[0] + columnWidths[1] + columnWidths[2] + columnWidths[3], startY - 5);
        contentStream.stroke();

        startY -= 20;
        contentStream.setFont(regularFont, 12);

        // Draw product details
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
        int leftAlign = 50;  // Adjusted for better alignment

        // Labels for the summary section
        String[] labels = {"Sous-total:", "Remise:", "Acompte:", "Frais de port:", "TVA:", "Total:"};
        String[] values = {
                CURRENCY_FORMAT.format(invoiceDTO.getSubtotal()),
                DECIMAL_FORMAT.format(invoiceDTO.getDiscount() * 100) + " %",  // Multiply by 100 to convert to percentage
                CURRENCY_FORMAT.format(invoiceDTO.getAdvancePayment()),
                CURRENCY_FORMAT.format(invoiceDTO.getShippingCost()),
                "15% (" + CURRENCY_FORMAT.format(invoiceDTO.getVat()) + ")",  // 15% VAT rate with the calculated VAT amount
                CURRENCY_FORMAT.format(invoiceDTO.getTotal())
        };

        contentStream.setFont(regularFont, 12);

        // Iterate through each label and value to display them
        for (int i = 0; i < labels.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(leftAlign, startY);
            contentStream.showText(labels[i]);
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(leftAlign + 200, startY);  // Adjusted for right alignment of values
            contentStream.showText(values[i]);
            contentStream.endText();

            startY -= 20;  // Move down to the next line
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