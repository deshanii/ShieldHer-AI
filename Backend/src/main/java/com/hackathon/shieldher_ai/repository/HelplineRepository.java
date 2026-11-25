package com.hackathon.shieldher_ai.repository;

import com.hackathon.shieldher_ai.entity.Helpline;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HelplineRepository extends JpaRepository<Helpline, Long> {
    List<Helpline> findByCountryIgnoreCase(String country);
}