package com.hackathon.shieldher_ai.controller;

import com.hackathon.shieldher_ai.dto.ThreatAnalysisRequest;
import com.hackathon.shieldher_ai.dto.ThreatAnalysisResponse;
import com.hackathon.shieldher_ai.entity.Helpline;
import com.hackathon.shieldher_ai.entity.SandboxScenario;
import com.hackathon.shieldher_ai.repository.HelplineRepository;
import com.hackathon.shieldher_ai.repository.SandboxScenarioRepository;
import com.hackathon.shieldher_ai.repository.ThreatAnalyticRepository;
import com.hackathon.shieldher_ai.service.GeminiThreatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ShieldHerController {

    private final GeminiThreatService geminiThreatService;
    private final HelplineRepository helplineRepository;
    private final SandboxScenarioRepository sandboxScenarioRepository;
    private final ThreatAnalyticRepository threatAnalyticRepository;

    public ShieldHerController(
            GeminiThreatService geminiThreatService,
            HelplineRepository helplineRepository,
            SandboxScenarioRepository sandboxScenarioRepository,
            ThreatAnalyticRepository threatAnalyticRepository
    ) {
        this.geminiThreatService = geminiThreatService;
        this.helplineRepository = helplineRepository;
        this.sandboxScenarioRepository = sandboxScenarioRepository;
        this.threatAnalyticRepository = threatAnalyticRepository;
    }

    @PostMapping("/threat/analyze")
    public ResponseEntity<ThreatAnalysisResponse> analyzeThreat(
            @Valid @RequestBody ThreatAnalysisRequest request
    ) {
        ThreatAnalysisResponse response = geminiThreatService.analyzeThreat(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/helplines")
    public ResponseEntity<List<Helpline>> getHelplines(
            @RequestParam(defaultValue = "ALL") String country
    ) {
        if ("ALL".equalsIgnoreCase(country.trim())) {
            return ResponseEntity.ok(helplineRepository.findAll());
        }
        return ResponseEntity.ok(helplineRepository.findByCountryIgnoreCase(country.trim()));
    }

    @GetMapping("/scenarios")
    public ResponseEntity<List<SandboxScenario>> getScenarios() {
        return ResponseEntity.ok(sandboxScenarioRepository.findAll());
    }

    @GetMapping("/analytics/summary")
    public ResponseEntity getAnalyticsSummary() {
        long totalScans = threatAnalyticRepository.count();
        return ResponseEntity.ok(Map.of("totalScans", totalScans));
    }
}
