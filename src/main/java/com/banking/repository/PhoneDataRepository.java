package com.banking.repository;

import com.banking.entity.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

    boolean existsByPhone(String phone);

    Optional<PhoneData> findByPhone(String phone);

    List<PhoneData> findByUserId(Long userId);

    long countByUserId(Long userId);
}
