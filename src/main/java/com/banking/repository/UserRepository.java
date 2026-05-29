package com.banking.repository;

import com.banking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.emails e LEFT JOIN u.phones p " +
            "WHERE (:dateOfBirth IS NULL OR u.dateOfBirth > :dateOfBirth) AND (:name IS NULL OR u.name LIKE :name%) " +
            "AND (:email IS NULL OR e.email = :email) AND (:phone IS NULL OR p.phone = :phone)")
    Page<User> searchUsers(
            @Param("dateOfBirth") LocalDate dateOfBirth,
            @Param("name") String name,
            @Param("email") String email,
            @Param("phone") String phone,
            Pageable pageable
    );

    @Query("SELECT u FROM User u JOIN u.emails e WHERE e.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN u.phones p WHERE p.phone = :phone")
    Optional<User> findByPhone(@Param("phone") String phone);
}
