package com.hackathon.shieldher_ai.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.shieldher_ai.dto.ThreatAnalysisRequest;
import com.hackathon.shieldher_ai.dto.ThreatAnalysisResponse;
import com.hackathon.shieldher_ai.entity.ThreatAnalytic;
import com.hackathon.shieldher_ai.repository.ThreatAnalyticRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiThreatService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ThreatAnalyticRepository threatAnalyticRepository;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public GeminiThreatService(ThreatAnalyticRepository threatAnalyticRepository) {
        this.restClient = RestClient.create();
        this.threatAnalyticRepository = threatAnalyticRepository;
    }

    public ThreatAnalysisResponse analyzeThreat(ThreatAnalysisRequest request) {
        String targetLanguage = resolveLanguage(request.getLanguage());
        String promptText = buildPrompt(request.getContent(), targetLanguage);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", promptText)))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "temperature", 0.2
                )
        );

        try {
            String rawResponse = restClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode rootNode = objectMapper.readTree(rawResponse);
            String modelJson = rootNode
                    .path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            ThreatAnalysisResponse parsed = objectMapper.readValue(modelJson, ThreatAnalysisResponse.class);
            String normalizedRisk = normalizeRiskLevel(parsed.getRiskLevel());
            parsed.setRiskLevel(normalizedRisk);

            saveAnonymizedLog(normalizedRisk, targetLanguage);
            return parsed;
        } catch (Exception ex) {
            ThreatAnalysisResponse fallback = buildFallbackResponse();
            saveAnonymizedLog(fallback.getRiskLevel(), targetLanguage);
            return fallback;
        }
    }

    private String resolveLanguage(String requestedLanguage) {
        if (requestedLanguage == null || requestedLanguage.trim().isEmpty()) {
            return "English";
        }
        return requestedLanguage.trim();
    }

    private String buildPrompt(String userContent, String language) {
        return "You are ShieldHer AI, a digital safety assistant protecting women in South Asia (Sri Lanka and Pakistan).\n"
                + "Analyze the following message, link, or online interaction for scams, phishing, fake job/loan offers, impersonation, blackmail, or harassment.\n"
                + "Return ONLY valid JSON with this exact structure:\n"
                + "{\n"
                + "  \"riskLevel\": \"LOW\" | \"MEDIUM\" | \"HIGH\",\n"
                + "  \"riskScore\": 0,\n"
                + "  \"summary\": \"Clear explanation in " + language + "\",\n"
                + "  \"redFlags\": [\"Warning sign 1 in " + language + "\", \"Warning sign 2 in " + language + "\"],\n"
                + "  \"actionSteps\": [\"Safe next step 1 in " + language + "\", \"Safe next step 2 in " + language + "\"]\n"
                + "}\n\n"
                + "User content to analyze:\n"
                + userContent;
    }

    private String normalizeRiskLevel(String riskLevel) {
        if (riskLevel == null) {
            return "MEDIUM";
        }
        String upper = riskLevel.trim().toUpperCase();
        if ("LOW".equals(upper) || "MEDIUM".equals(upper) || "HIGH".equals(upper)) {
            return upper;
        }
        return "MEDIUM";
    }

    private void saveAnonymizedLog(String riskLevel, String languageUsed) {
        try {
            ThreatAnalytic log = new ThreatAnalytic(riskLevel, languageUsed);
            threatAnalyticRepository.save(log);
        } catch (Exception ignored) {
            // Do not block the user response if analytics logging fails
        }
    }

    private ThreatAnalysisResponse buildFallbackResponse() {
        return new ThreatAnalysisResponse(
                "MEDIUM",
                50,
                "Automated AI analysis could not complete right now. Please treat unfamiliar links or urgent requests carefully.",
                List.of("Unable to verify sender or link automatically"),
                List.of(
                        "Do not click unfamiliar links or share passwords, OTPs, or ID photos",
                        "Verify the sender through an official website or trusted helpline"
                )
        );
    }
}