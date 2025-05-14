package com.vulkano.desarolloApp.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseStatusResponse {

    private LocalDateTime timestamp;
    private boolean allConnectionsOk;
    private Map<String, DatabaseConnectionStatus> connections;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatabaseConnectionStatus {
        private boolean connected;
        private String message;
        private String details;
    }

    /**
     * Crea una respuesta exitosa con todas las conexiones activas
     * @return DatabaseStatusResponse configurada para estado exitoso
     */
    public static DatabaseStatusResponse createSuccessResponse() {
        Map<String, DatabaseConnectionStatus> connections = new HashMap<>();

        connections.put("main", buildStatus(
                true,
                "Conexión exitosa a la base de datos principal",
                null));

        connections.put("users", buildStatus(
                true,
                "Conexión exitosa a la base de datos de usuarios",
                null));

        return DatabaseStatusResponse.builder()
                .timestamp(LocalDateTime.now())
                .allConnectionsOk(true)
                .connections(connections)
                .build();
    }

    public static DatabaseStatusResponse createErrorResponse(String dbName,
                                                             String errorMessage,
                                                             String details) {
        Map<String, DatabaseConnectionStatus> connections = new HashMap<>();

        // Conexión fallida
        connections.put(
                dbName, buildStatus(
                false,
                        errorMessage,
                        details)
        );

        // Marcar la otra base de datos como estado desconocido
        String otherDb = "main".equals(dbName) ? "users" : "main";
        connections.put(otherDb, buildStatus(
                false,
                "Estado desconocido - No se pudo verificar",
                "La verificación fue omitida debido al fallo en " + dbName));

        return DatabaseStatusResponse.builder()
                .timestamp(LocalDateTime.now())
                .allConnectionsOk(false)
                .connections(connections)
                .build();
    }

    private static DatabaseConnectionStatus buildStatus(boolean connected,
                                                        String message,
                                                        String details) {
        return DatabaseConnectionStatus.builder()
                .connected(connected)
                .message(message)
                .details(details)
                .build();
    }
}