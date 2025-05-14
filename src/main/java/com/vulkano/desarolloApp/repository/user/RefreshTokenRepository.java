package com.vulkano.desarolloApp.repository.user;

import com.vulkano.desarolloApp.models.user.RefreshTokenEntity;
import com.vulkano.desarolloApp.models.user.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshTokenEntity, String> {

    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByUser(UserEntity user);

    List<RefreshTokenEntity> findAllByUserAndRevokedFalseAndUsedFalse(UserEntity user);

    @Query("{'expiryDate': {$lt: ?0}}")
    void deleteAllExpiredTokens(Instant now);

    Optional<RefreshTokenEntity> findByPreviousToken(String previousToken);
}