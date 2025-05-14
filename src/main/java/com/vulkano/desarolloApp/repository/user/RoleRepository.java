package com.vulkano.desarolloApp.repository.user;

import com.vulkano.desarolloApp.models.user.ERole;
import com.vulkano.desarolloApp.models.user.RoleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<RoleEntity, String> {
    Optional<RoleEntity> findByName(ERole name);
}