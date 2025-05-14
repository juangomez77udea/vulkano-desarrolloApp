package com.vulkano.desarolloApp.security.exceptions;

import com.vulkano.desarolloApp.response.DatabaseStatusResponse;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // Mantener los manejadores de excepciones existentes si los hay

    /**
     * Maneja excepciones relacionadas con la autenticación
     */
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(Exception ex) {
        log.error("Error de autenticación: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("error", "Unauthorized");
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja excepciones relacionadas con la renovación de tokens
     */
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<Map<String, Object>> handleTokenRefreshException(TokenRefreshException ex) {
        log.error("Error en refresh token: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("error", "Forbidden");
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Maneja excepciones relacionadas con la conexión a la base de datos
     */
    @ExceptionHandler({
            SQLException.class,
            DataAccessResourceFailureException.class,
            JDBCConnectionException.class,
            PersistenceException.class
    })
    public ResponseEntity<DatabaseStatusResponse> handleDatabaseConnectionException(Exception ex, WebRequest request) {
        log.error("Error de conexión a la base de datos: {}", ex.getMessage());

        // Determinar qué base de datos causó el error basado en el mensaje de error
        String dbName = "main"; // Por defecto asumimos la base de datos principal
        String errorMessage = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        if (errorMessage.contains("fiscontrol_users_db") ||
                errorMessage.contains("user") ||
                errorMessage.contains("second-datasource")) {
            dbName = "users";
        }

        DatabaseStatusResponse response = DatabaseStatusResponse.createErrorResponse(
                dbName,
                "Error de conexión a la base de datos",
                ex.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

}
