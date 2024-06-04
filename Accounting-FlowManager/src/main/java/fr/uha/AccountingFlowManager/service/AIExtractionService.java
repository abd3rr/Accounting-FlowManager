package fr.uha.AccountingFlowManager.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.uha.AccountingFlowManager.dto.invoice.PreviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class AIExtractionService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openai.apikey}")
    private String openAiApiKey;

    @Autowired
    public AIExtractionService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    private String callOpenAiApi(String prompt, int maxTokens) {
        String apiURL = UriComponentsBuilder.fromHttpUrl("https://api.openai.com/v1/chat/completions")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", Collections.singletonList(Map.of("role", "user", "content", prompt)));
        body.put("max_tokens", maxTokens);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiURL, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                // Log error and handle non-2xx responses appropriately
                throw new IllegalStateException("Failed to call OpenAI API: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            // Log and handle the RestClientException appropriately
            throw new RuntimeException("Error communicating with OpenAI API", e);
        }
    }


    private String createIsInvoicePrompt(String text) {
        return "Based on the following French text, determine if it is an invoice. An invoice typically includes a unique invoice number, " +
                "date of issue, descriptions of goods or services, quantities, prices, total amount due, " +
                "and the names and addresses of both the supplier and the customer. Respond only with 'yes' or 'no'. Here is the text:\n\n" +
                text + "\n\nIs this text an invoice? Respond strictly with 'yes' or 'no'.";
    }

    public boolean isInvoice(String text) {
        String prompt = createIsInvoicePrompt(text);
        String jsonResponse = callOpenAiApi(prompt, 10);
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode choiceNode = rootNode.path("choices").get(0).path("message").path("content");
            return "yes".equalsIgnoreCase(choiceNode.asText());
        } catch (Exception e) {
            e.printStackTrace();
            return false; // or handle more appropriately
        }
    }


    private String createExtractionPrompt(String text) {
        return "Extract the following client information and other details from the French invoice provided. " +
                "Format the extracted data as key-value pairs without currency symbols and separate address components:\n" +
                "Invoice Text: " + text + "\n" +
                "Format the response as: {\n" +
                "  \"Customer Name\": \"value\",\n" +
                "  \"Customer Address\": \"value\",\n" +
                "  \"Customer Country\": \"value\",\n" +
                "  \"Customer Email\": \"value\",\n" +
                "  \"Issue Date\": \"value\",  // Format: dd/MM/yyyy HH:mm:ss\n" +
                "  \"Currency\": \"value\",\n" +
                "  \"Subtotal\": \"value\",\n" +
                "  \"Discount\": \"value\",\n" +
                "  \"Advance Payment\": \"value\",\n" +
                "  \"Total\": \"value\",\n" +
                "  \"Shipping Cost\": \"value\",\n" +
                "  \"Shipping Cost Type\": \"value\",\n" +
                "  \"VAT\": \"value\",\n" +
                "  \"Lines\": [{\"Product Name\": \"value\", \"Quantity\": \"value\", \"Unit Price\": \"value\", \"Total Price\": \"value\"}]\n" +
                "}";
    }



    public String getInvoiceDataResponse(String invoiceText) {
        if (!isInvoice(invoiceText)) {
            throw new IllegalArgumentException("The provided text is not an invoice.");
        }

        String prompt = createExtractionPrompt(invoiceText);
        return callOpenAiApi(prompt, 250); // Returning the raw response as a string
    }

}
