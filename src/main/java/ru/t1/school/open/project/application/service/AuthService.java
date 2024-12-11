package ru.t1.school.open.project.application.service;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.t1.school.open.project.application.security.JwtUtil;


@Service
public class AuthService {
    private final JwtUtil jwtUtil;

    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String generateAccessToken(String username) {
        return jwtUtil.generateAccessToken(username);
    }

    public Pair<String, String> generateTokenPair(String username) {
        return Pair.of(generateRefreshToken(username), generateAccessToken(username));
    }

    private String generateRefreshToken(String username) {
        return jwtUtil.generateRefreshToken(username);
    }

    public boolean isTokenValid(String token) {
        return jwtUtil.validateJwt(token);
    }
}
