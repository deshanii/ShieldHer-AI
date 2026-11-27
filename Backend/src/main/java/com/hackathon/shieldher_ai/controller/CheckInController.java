package com.hackathon.shieldher_ai.controller;

import com.hackathon.shieldher_ai.service.CheckInService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/api/v1/check-ins")
public class CheckInController {
    private final CheckInService checkIns;

    public CheckInController(CheckInService checkIns) {
        this.checkIns = checkIns;
    }

    @PostMapping
    public CompletionStage<ResponseEntity<Void>> send(
            @RequestHeader(value = "X-Check-In-Code", defaultValue = "") String code,
            @Valid @RequestBody CheckInRequest request
    ) {
        return checkIns.send(code, request.recipient()).thenApply(status -> ResponseEntity.status(status).build());
    }

    public record CheckInRequest(@NotBlank @Email @Size(max = 254) String recipient) {}
}
