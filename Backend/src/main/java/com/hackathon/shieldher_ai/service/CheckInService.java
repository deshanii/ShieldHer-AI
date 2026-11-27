package com.hackathon.shieldher_ai.service;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class CheckInService {
    private static final String SUBJECT = "Quick check-in";
    private static final String BODY = "Hi, could you check in with me when you have a moment? Thank you.";
    private final ThreadPoolExecutor workers = new ThreadPoolExecutor(
            1, 2, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(4)
    );
    private final Deque<Instant> attempts = new ArrayDeque<>();
    private final ObjectProvider<JavaMailSender> mailProvider;
    private final String from;
    private final String accessCode;

    public CheckInService(ObjectProvider<JavaMailSender> mailProvider,
                          @Value("${shieldher.check-in.from}") String from,
                          @Value("${shieldher.check-in.access-code}") String accessCode) {
        this.mailProvider = mailProvider;
        this.from = from;
        this.accessCode = accessCode;
    }

    public CompletableFuture<HttpStatus> send(String code, String recipient) {
        JavaMailSender sender = mailProvider.getIfAvailable();
        if (sender == null || from.isBlank() || accessCode.length() < 16) {
            return CompletableFuture.completedFuture(HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (code.length() > 128 || !MessageDigest.isEqual(
                accessCode.getBytes(StandardCharsets.UTF_8), code.getBytes(StandardCharsets.UTF_8))) {
            return CompletableFuture.completedFuture(HttpStatus.FORBIDDEN);
        }
        if (recipient.indexOf('@') < 1 || recipient.matches(".*[\\s,;<>].*")) {
            return CompletableFuture.completedFuture(HttpStatus.BAD_REQUEST);
        }
        if (!reserveAttempt()) {
            return CompletableFuture.completedFuture(HttpStatus.TOO_MANY_REQUESTS);
        }
        try {
            return CompletableFuture.supplyAsync(() -> submit(sender, recipient), workers);
        } catch (RejectedExecutionException busy) {
            return CompletableFuture.completedFuture(HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    private synchronized boolean reserveAttempt() {
        Instant now = Instant.now();
        Instant cutoff = now.minus(Duration.ofMinutes(10));
        while (!attempts.isEmpty() && attempts.getFirst().isBefore(cutoff)) attempts.removeFirst();
        if (attempts.size() >= 5) return false;
        attempts.addLast(now);
        return true;
    }

    private HttpStatus submit(JavaMailSender sender, String recipient) {
        var message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject(SUBJECT);
        message.setText(BODY);
        try {
            sender.send(message);
            return HttpStatus.NO_CONTENT;
        } catch (MailException failure) {
            return HttpStatus.BAD_GATEWAY;
        }
    }

    @PreDestroy
    void shutdown() {
        workers.shutdownNow();
    }
}
