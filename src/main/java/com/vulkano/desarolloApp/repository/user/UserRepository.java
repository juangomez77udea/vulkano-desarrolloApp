package com.vulkano.desarolloApp.repository.user;

import com.vulkano.desarolloApp.models.user.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByUsername(String username);

    @Query("{'username': ?0}")
    Optional<UserEntity> getName(String username);
}