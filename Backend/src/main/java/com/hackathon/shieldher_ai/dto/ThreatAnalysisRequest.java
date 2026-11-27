package com.hackathon.shieldher_ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ThreatAnalysisRequest {

    @NotBlank(message = "Message or URL content is required")
    @Size(max = 4000)
    private String content;

    @Size(max = 20)
    private String language;

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
