package com.hackathon.shieldher_ai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sandbox_scenarios")
public class SandboxScenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String promptText;

    private String optionA;
    private String optionB;
    private Character correctOption;

    @Column(columnDefinition = "TEXT")
    private String feedbackText;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getPromptText() { return promptText; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public Character getCorrectOption() { return correctOption; }
    public String getFeedbackText() { return feedbackText; }
}