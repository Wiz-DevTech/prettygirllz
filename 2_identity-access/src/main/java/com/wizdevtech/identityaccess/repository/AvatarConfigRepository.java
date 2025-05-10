package com.wizdevtech.identityaccess.repository;

import com.wizdevtech.identityaccess.model.AvatarConfig;
import com.wizdevtech.identityaccess.model.enums.AvatarType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvatarConfigRepository extends JpaRepository<AvatarConfig, Long> {
    // Basic finder methods - these are already working
    Optional<AvatarConfig> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    // Methods using the status field we added
    Page<AvatarConfig> findByStatus(String status, Pageable pageable);

    // Filter by status and avatar type
    Page<AvatarConfig> findByStatusAndAvatarType(String status, AvatarType avatarType, Pageable pageable);

    // User-related queries with explicit JPQL
    @Query("SELECT a FROM AvatarConfig a WHERE a.user.enabled = :enabled")
    Page<AvatarConfig> findByUserEnabled(@Param("enabled") boolean enabled, Pageable pageable);

    // Replacing the problematic findByIsActive method with a query on status field
    // This assumes "ACTIVE" status equals isActive=true
    @Query("SELECT a FROM AvatarConfig a WHERE a.status = :status")
    Page<AvatarConfig> findByActiveStatus(@Param("status") String status, Pageable pageable);

    // Complex search with email, user status, and avatar status
    @Query("SELECT a FROM AvatarConfig a WHERE LOWER(a.user.email) LIKE LOWER(CONCAT('%', :email, '%')) " +
            "AND a.user.enabled = :userEnabled AND a.status = :status")
    Page<AvatarConfig> findByUserEmailUserEnabledAndStatus(
            @Param("email") String email,
            @Param("userEnabled") boolean userEnabled,
            @Param("status") String status,
            Pageable pageable);
}