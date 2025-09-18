package me.trihung.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import me.trihung.entity.RefreshToken;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    void deleteByToken(String token);

    @Query(value = "{'expireTime': {'$lt': ?0}}", delete = true)
    long deleteByExpireTimeBefore(LocalDateTime now);

    Optional<RefreshToken> findByToken(String refreshToken);

    Optional<RefreshToken> findByTokenAndUsername(String refreshToken, String username);
}