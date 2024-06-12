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
import java.util.UUID;


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

    private String callOpenAiApi(String prompt, int maxTokens, String sessionToken) {
        String apiURL = UriComponentsBuilder.fromHttpUrl("https://api.openai.com/v1/chat/completions")
                .queryParam("session_token", sessionToken)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo-0125");
        body.put("messages", Collections.singletonList(Map.of("role", "user", "content", prompt)));
        body.put("max_tokens", maxTokens);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiURL, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new IllegalStateException("Failed to call OpenAI API: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Error communicating with OpenAI API", e);
        }
    }



    private String createIsInvoicePrompt(String text) {
        return "Please determine if the following French text is an invoice. An invoice typically includes at least one line item description with a corresponding price and a total amount due. It may also include an invoice number, date of issue, and the names and addresses of the supplier and customer. Respond with 'yes' if these elements are present and 'no' if they are not.\n\n" +
                "Text:\n" +
                text + "\n\n" +
                "Is this text an invoice? Respond with 'yes' or 'no'.";
    }



    public boolean isInvoice(String text) {
        String prompt = createIsInvoicePrompt(text);
        String jsonResponse = callOpenAiApi(prompt, 10, UUID.randomUUID().toString());
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode choiceNode = rootNode.path("choices").get(0).path("message").path("content");
            return "yes".equalsIgnoreCase(choiceNode.asText());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private String createExtractionPrompt(String text) {
        return "Please extract the following information from the provided French invoice text and format it as a well-formed, complete, and valid JSON object. Ensure that all required fields are present with appropriate values, and handle any missing or unspecified data according to the guidelines provided.\n\n" +
                "Required Fields:\n" +
                "- Customer Name (string, max 100 characters)\n" +
                "- Customer Address (string, max 150 characters)\n" +
                "- Customer Country (string, max 50 characters)\n" +
                "- Issue Date (string, format: dd/MM/yyyy HH:mm:ss)\n" +
                "- Currency (string, max 3 characters)\n" +
                "- Subtotal (decimal, use '0' if not specified)\n" +
                "- Discount (decimal, e.g., 20% should be formatted as 0.2; use '0' if no discount)\n" +
                "- Advance Payment (decimal, use '0' if not specified)\n" +
                "- Total (decimal)\n" +
                "- Shipping Cost (decimal, use '0' if not specified)\n" +
                "- VAT (decimal, use '0' if VAT is not applicable or not specified)\n" +
                "- Lines (array of objects, each object representing an invoice line item)\n" +
                "  - Product Name (string, max 50 characters)\n" +
                "    - If the line item is a product, extract the product name as is, truncating if it exceeds 50 characters\n" +
                "    - If the line item is a service, generate a descriptive and contextually appropriate name that reflects the service being invoiced, with a maximum length of 50 characters\n" +
                "  - Quantity (decimal, use '1' for services)\n" +
                "  - Unit Price (decimal)\n" +
                "  - Total Price (decimal)\n\n" +
                "Optional Fields:\n" +
                "- Customer Email (string, max 100 characters)\n" +
                "- Shipping Cost Type (string, max 50 characters)\n\n" +
                "Guidelines:\n" +
                "- For services, generate a descriptive and contextually appropriate name that reflects the service being invoiced, with a maximum length of 50 characters and a default quantity of '1'.\n" +
                "- For products, extract the product name as is, truncating if it exceeds 50 characters.\n" +
                "- Treat any unspecified monetary value as '0'.\n" +
                "- Format the extracted data as key-value pairs without currency symbols and separate address components.\n" +
                "- Ensure the JSON response is well-formed, complete, and valid. All required fields must be present with appropriate values.\n\n" +
                "Invoice Text:\n" +
                text + "\n\n" +
                "Please provide the extracted information as a JSON object adhering to the specified format and guidelines. Prioritize accuracy and completeness while handling product names and service descriptions appropriately.";
    }

    private String createVerificationPrompt(String text, String extractedJson) {
        return "Please verify the accuracy and completeness of the extracted JSON data by comparing it against the original French invoice text provided below. Check for any missing, incorrect, or inconsistent values.\n\n" +
                "If the extracted JSON is valid and accurate, respond with the JSON object as is.\n\n" +
                "If you find any issues, please provide only the corrected version of the JSON object, using the original invoice text to fill in any missing or incorrect information. Ensure that the corrected JSON adheres to the specified format and guidelines.\n\n" +
                "Original Invoice Text:\n" +
                text + "\n\n" +
                "Extracted JSON:\n" +
                extractedJson + "\n\n" +
                "Please provide the verified or corrected JSON object.";
    }
    public String getInvoiceDataResponse(String invoiceText) {
        if (!isInvoice(invoiceText)) {
            throw new IllegalArgumentException("The provided text is not an invoice.");
        }

        String extractionPrompt = createExtractionPrompt(invoiceText);
        String extractedJson = callOpenAiApi(extractionPrompt, 3000,UUID.randomUUID().toString());

        //String verificationPrompt = createVerificationPrompt(invoiceText, extractedJson);
        //String verifiedJson = callOpenAiApi(verificationPrompt, 800);
        System.out.println(extractedJson);
        return extractedJson;
    }

}
