package com.example.Tech.config;




import com.example.Tech.services.AuthService;
import com.google.firebase.auth.FirebaseToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.OutputStream;
import java.io.IOException;

public class TokenHandler implements HttpHandler {
    private final AuthService authService = new AuthService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        String response;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response = "Missing or invalid Authorization header";
            exchange.sendResponseHeaders(401, response.length());
        } else {
            String token = authHeader.substring(7);
            try {
                FirebaseToken decodedToken = authService.verifyToken(token);
                response = "Token is valid. UID: " + decodedToken.getUid();
                exchange.sendResponseHeaders(200, response.length());
            } catch (Exception e) {
                response = "Invalid token: " + e.getMessage();
                exchange.sendResponseHeaders(401, response.length());
            }
        }

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

