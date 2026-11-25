package com.hackathon.shieldher_ai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "helplines")
public class Helpline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String country;
    private String category;
    private String organization;
    private String contactInfo;

    public Long getId() { return id; }
    public String getCountry() { return country; }
    public String getCategory() { return category; }
    public String getOrganization() { return organization; }
    public String getContactInfo() { return contactInfo; }
}