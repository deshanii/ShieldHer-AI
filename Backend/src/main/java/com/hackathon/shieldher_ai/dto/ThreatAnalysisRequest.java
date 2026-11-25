package com.hackathon.shieldher_ai.dto;

import jakarta.validation.constraints.NotBlank;

public class ThreatAnalysisRequest {

    @NotBlank(message = "Message or URL content is required")
    private String content;

    private String language; // "English", "Sinhala", "Tamil", or "Urdu"

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
