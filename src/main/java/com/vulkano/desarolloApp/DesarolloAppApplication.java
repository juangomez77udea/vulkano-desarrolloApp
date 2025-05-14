package com.vulkano.desarolloApp;

import com.vulkano.desarolloApp.models.user.ERole;
import com.vulkano.desarolloApp.models.user.RoleEntity;
import com.vulkano.desarolloApp.models.user.UserEntity;
import com.vulkano.desarolloApp.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableScheduling
public class DesarolloAppApplication {

	private static final Logger log = LoggerFactory.getLogger(DesarolloAppApplication.class);
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	public DesarolloAppApplication(PasswordEncoder passwordEncoder, UserRepository userRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(DesarolloAppApplication.class, args);
	}

	private void createUser(String email, String username, ERole role) {
		Set<RoleEntity> roles = new HashSet<>();
		roles.add(RoleEntity.builder().name(role).build());

		UserEntity userEntity = UserEntity.builder()
				.email(email)
				.username(username)
				.password(passwordEncoder.encode("usuarioPrueba123*")) // Contraseña en texto plano
				.enabled(true)
				.roles(roles)
				.build();

		userRepository.save(userEntity);
		log.info("Usuario creado: {}", userEntity.getUsername());
	}

	@Bean
	CommandLineRunner init() {
		return args -> {
			if (userRepository.count() == 0) {
				log.info("No se encontraron usuarios. Creando usuario de prueba...");
				createUser("usuario_prueba@mail.com", "usuario_prueba", ERole.ADMIN);
			} else {
				log.info("Usuarios ya existentes. No se creará el usuario de prueba.");
			}
		};
	}
}
