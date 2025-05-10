package com.wizdevtech.repository;

import com.wizdevtech.model.ApiResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;

public interface ApiResponseRepository extends JpaRepository<ApiResponse, String> {

    // Delete expired cache entries
    @Modifying
    @Query("DELETE FROM ApiResponse a WHERE a.expiry < :now")
    int deleteExpired(@Param("now") Instant now);
}
