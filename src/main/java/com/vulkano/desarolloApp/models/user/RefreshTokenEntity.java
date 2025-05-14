package com.vulkano.desarolloApp.models.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Document(collection = "refresh_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenEntity {

    @Id
    private String id;

    private String token;
    private Instant expiryDate;
    private UserEntity user;
    private boolean revoked;
    private boolean used;
    private String previousToken;

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }

    public boolean isValid() {
        return !isExpired() && !revoked && !used;
    }
}