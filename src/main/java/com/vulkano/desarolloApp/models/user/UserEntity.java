package com.vulkano.desarolloApp.models.user;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "users")
public class UserEntity {

    @Id
    private String id;

    private String username;
    private String password;
    private String email;
    private Boolean enabled;

    private Set<String> roleNames;

    private Set<RoleEntity> roles;
}
