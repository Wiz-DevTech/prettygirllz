package com.wizdevtech.identityaccess.repository;

import com.wizdevtech.identityaccess.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<User> findByEnabled(boolean enabled, Pageable pageable);
    Page<User> findByEmailContainingIgnoreCaseAndEnabled(String email, boolean enabled, Pageable pageable);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}