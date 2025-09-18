package me.trihung.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import me.trihung.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken t WHERE t.expireTime < :now")
    void deleteExpiredTokens(LocalDateTime now);

    Optional<RefreshToken> findByToken(String refreshToken);

    Optional<RefreshToken> findByTokenAndUsername(String refreshToken, String username);
}