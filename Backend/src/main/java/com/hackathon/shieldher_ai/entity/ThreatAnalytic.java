package com.hackathon.shieldher_ai.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "threat_analytics")
public class ThreatAnalytic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String riskLevel;
    private String languageUsed;
    private Instant createdAt = Instant.now();

    public ThreatAnalytic() {}
    public ThreatAnalytic(String riskLevel, String languageUsed) {
        this.riskLevel = riskLevel;
        this.languageUsed = languageUsed;
    }

    public Long getId() { return id; }
    public String getRiskLevel() { return riskLevel; }
    public String getLanguageUsed() { return languageUsed; }
    public Instant getCreatedAt() { return createdAt; }
}
