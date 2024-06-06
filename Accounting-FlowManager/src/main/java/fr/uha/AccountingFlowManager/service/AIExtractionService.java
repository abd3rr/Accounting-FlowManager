package fr.uha.AccountingFlowManager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        return "Based on the following French text, determine if it is an invoice. An invoice is characterized by the presence of at least one item with its corresponding price and a total amount due. " +
                "Review the text for the following essential elements to confirm if it is an invoice:\n\n" +
                "- At least one line item description with a price (look for descriptions followed by numerical values indicating cost)\n" +
                "- A total amount due that summarizes the cost of the items\n" +
                "Additional common features of an invoice such as a unique invoice number, date of issue, and the names and addresses of both the supplier and the customer can help confirm the identification but are not strictly necessary for the definition. " +
                "Respond only with 'yes' or 'no' based on the presence of these key elements.\n\n" +
                "Here is the text:\n\n" +
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
        return "Extract the following client information and other details from the French invoice provided, ensuring that the data is complete and correctly formatted before encoding it into JSON. For services, generate a descriptive, contextually appropriate name that reflects the service being invoiced, with a maximum length of 50 characters and a default quantity of '1'. Treat any unspecified monetary value as '0'. " +
                "Format the extracted data as key-value pairs without currency symbols and separate address components:\n" +
                "Invoice Text: " + text + "\n" +
                "Ensure the JSON response is well-formed and valid. Here's the required format, noting that all textual fields should be concise and not exceed specified lengths:\n" +
                "{\n" +
                "  \"Customer Name\": \"value\" (max 100 characters),\n" +
                "  \"Customer Address\": \"value\" (max 150 characters),\n" +
                "  \"Customer Country\": \"value\" (max 50 characters),\n" +
                "  \"Customer Email\": \"value\" (max 100 characters),\n" +
                "  \"Issue Date\": \"value\",  // Format: dd/MM/yyyy HH:mm:ss\n" +
                "  \"Currency\": \"value\" (max 3 characters),\n" +
                "  \"Subtotal\": \"value\",  // Use '0' if not specified\n" +
                "  \"Discount\": \"value\",  // As decimal, e.g., 20% should be formatted as 0.2; use '0' if no discount\n" +
                "  \"Advance Payment\": \"value\",  // Use '0' if not specified\n" +
                "  \"Total\": \"value\",\n" +
                "  \"Shipping Cost\": \"value\",  // Use '0' if not specified\n" +
                "  \"Shipping Cost Type\": \"value\" (max 50 characters),\n" +
                "  \"VAT\": \"value\",  // Use '0' if VAT is not applicable or not specified\n" +
                "  \"Lines\": [{\n" +
                "    \"Product Name\": \"value\" (max 50 characters),  // Descriptive and context-appropriate for services\n" +
                "    \"Quantity\": \"value\",  // Use '1' for services\n" +
                "    \"Unit Price\": \"value\",\n" +
                "    \"Total Price\": \"value\"\n" +
                "  }]\n" +
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
