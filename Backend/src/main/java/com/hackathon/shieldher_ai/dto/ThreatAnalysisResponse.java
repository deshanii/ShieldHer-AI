package com.hackathon.shieldher_ai.dto;

import java.util.List;

public class ThreatAnalysisResponse {

    private String riskLevel; // "LOW", "MEDIUM", or "HIGH"
    private int riskScore; // 0 to 100
    private String summary;
    private List<String> redFlags;
    private List<String> actionSteps;

    public ThreatAnalysisResponse() {
    }

    public ThreatAnalysisResponse(
            String riskLevel,
            int riskScore,
            String summary,
            List<String> redFlags,
            List<String> actionSteps
    ) {
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.summary = summary;
        this.redFlags = redFlags;
        this.actionSteps = actionSteps;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getRedFlags() {
        return redFlags;
    }

    public void setRedFlags(List<String> redFlags) {
        this.redFlags = redFlags;
    }

    public List<String> getActionSteps() {
        return actionSteps;
    }

    public void setActionSteps(List<String> actionSteps) {
        this.actionSteps = actionSteps;
    }
}
