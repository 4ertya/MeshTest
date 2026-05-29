package com.banking.repository;

import com.banking.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    boolean existsByEmail(String email);

    Optional<EmailData> findByEmail(String email);

    List<EmailData> findByUserId(Long userId);

    long countByUserId(Long userId);
}
