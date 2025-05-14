package com.vulkano.desarolloApp.service.userImpl;

import com.mongodb.client.MongoCollection;
import com.vulkano.desarolloApp.models.user.ERole;
import com.vulkano.desarolloApp.models.user.RoleEntity;
import com.vulkano.desarolloApp.repository.user.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Buscando usuario: {}", username);

        MongoCollection<Document> usersCollection = mongoTemplate.getCollection("users");
        Document userDoc = usersCollection.find(new Document("username", username)).first();

        if (userDoc == null) {
            log.error("Usuario no encontrado: {}", username);
            throw new UsernameNotFoundException("El usuario " + username + " no existe.");
        }

        Boolean enabled = userDoc.getBoolean("enabled", true);
        if (!enabled) {
            log.error("Usuario deshabilitado: {}", username);
            throw new DisabledException("El usuario " + username + " está deshabilitado.");
        }

        String password = userDoc.getString("password");

        Set<GrantedAuthority> authorities = new HashSet<>();
        List<Object> rolesArray = userDoc.getList("roles", Object.class);

        if (rolesArray != null && !rolesArray.isEmpty()) {
            log.info("Roles encontrados para el usuario {}: {}", username, rolesArray);

            for (Object roleObj : rolesArray) {
                if (roleObj instanceof Document) {
                    Document roleDoc = (Document) roleObj;
                    if (roleDoc.containsKey("name")) {
                        String roleName = roleDoc.getString("name");
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                        log.info("Añadido rol desde documento: {}", roleName);
                    }
                } else if (roleObj instanceof String) {
                    String roleName = (String) roleObj;
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
                    log.info("Añadido rol desde string: {}", roleName);
                }
            }
        }

        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            log.info("No se encontraron roles, asignando ROLE_USER por defecto");
        }

        log.info("Usuario {} cargado exitosamente con roles: {}", username, authorities);
        return new User(username, password, authorities);
    }
}
