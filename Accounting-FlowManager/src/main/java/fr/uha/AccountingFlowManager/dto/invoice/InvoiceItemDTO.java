package fr.uha.AccountingFlowManager.dto.invoice;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InvoiceItemDTO {
    private long invoiceId;
    private long customerId;
    private String customerName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Europe/Paris")
    private LocalDateTime issueDate;


}
