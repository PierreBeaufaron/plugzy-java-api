package com.humanbooster.cda.plugzy.repository;

import com.humanbooster.cda.plugzy.entity.RefreshToken;
import com.humanbooster.cda.plugzy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findAllByUser(User user);

    @Modifying
    @Query("delete from RefreshToken r where r.user = :user and r.deviceId = :deviceId")
    void deleteByUserAndDeviceId(@Param("user") User user, @Param("deviceId") String deviceId);

    @Modifying
    void deleteByToken(String token);

    @Modifying
    @Query("delete from RefreshToken r where r.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpired();

    @Modifying
    void deleteByUser(User user);
}
