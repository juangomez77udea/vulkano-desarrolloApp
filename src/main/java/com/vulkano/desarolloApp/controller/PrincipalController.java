package com.vulkano.desarolloApp.controller;

import com.vulkano.desarolloApp.models.user.RefreshTokenEntity;
import com.vulkano.desarolloApp.models.user.UserEntity;
import com.vulkano.desarolloApp.repository.user.UserRepository;
import com.vulkano.desarolloApp.request.LoginRequest;
import com.vulkano.desarolloApp.security.jwt.JwtUtils;
import com.vulkano.desarolloApp.service.user.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class PrincipalController {

    private static final Logger log = LoggerFactory.getLogger(PrincipalController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String username = loginRequest.getUsername();
        log.info("Intento de login para el usuario: {}", username);

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar token JWT
            String jwt = jwtUtils.generateAccessToken(username);

            // Generar refresh token
            RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(username);

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("refreshToken", refreshToken.getToken());
            response.put("username", authentication.getName());

            log.info("Login exitoso - Username: {}", username);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.error("Intento de login fallido: Credenciales inválidas - Username: {}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        } catch (UsernameNotFoundException e) {
            log.error("Intento de login fallido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error en login - Username: {} - Error: {}", username, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de autenticación");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("API funcionando correctamente");
    }
}
