package com.vulkano.desarolloApp.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vulkano.desarolloApp.models.user.RefreshTokenEntity;
import com.vulkano.desarolloApp.models.user.UserEntity;
import com.vulkano.desarolloApp.security.jwt.JwtUtils;
import com.vulkano.desarolloApp.service.user.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils,
                                   AuthenticationManager authenticationManager,
                                   RefreshTokenService refreshTokenService) {
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        super.setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/api/auth/login"); // Establece la URL de login
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {
        try {
            UserEntity userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class);
            String username = userEntity.getUsername();
            String password = userEntity.getPassword();

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            return getAuthenticationManager().authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer credenciales de la solicitud", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        User user = (User) authResult.getPrincipal();

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String token = jwtUtils.generateAccessToken(user.getUsername());
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("token", token);
        httpResponse.put("refreshToken", refreshToken.getToken());
        httpResponse.put("message", "Autenticación correcta");
        httpResponse.put("username", user.getUsername());
        httpResponse.put("roles", roles);

        response.addHeader("Authorization", "Bearer " + token);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), httpResponse);

        log.info("Inicio de sesión exitoso - Usuario: {} - Roles: {}", user.getUsername(), roles);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              org.springframework.security.core.AuthenticationException failed)
            throws IOException {
        log.error("Error de autenticación: {}", failed.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", failed instanceof BadCredentialsException ?
                "Credenciales inválidas" : failed.getMessage());
        errorResponse.put("path", request.getRequestURI());

        new ObjectMapper().writeValue(response.getWriter(), errorResponse);
    }
}