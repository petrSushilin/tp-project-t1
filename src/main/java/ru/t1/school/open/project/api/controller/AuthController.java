package ru.t1.school.open.project.api.controller;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.school.open.project.api.dto.JwtResponse;
import ru.t1.school.open.project.api.dto.LoginRequest;
import ru.t1.school.open.project.api.dto.SignupRequest;
import ru.t1.school.open.project.api.dto.UserDto;
import ru.t1.school.open.project.application.security.JwtUtil;
import ru.t1.school.open.project.application.service.AuthService;
import ru.t1.school.open.project.application.service.UserService;
import ru.t1.school.open.project.domain.enums.UserRoles;
import ru.t1.school.open.project.domain.security.UserDetailsImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, AuthService authService, UserService userService, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.userService = userService;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest) {
        if (userService.existingByUsername(signupRequest.username())) {
            return ResponseEntity
                    .badRequest()
                    .body("Username already exists");
        }

        UserDto savedUserDto = userService.create(
                new UserDto(0L, signupRequest.username(), signupRequest.password(), Set.of(UserRoles.USER))
        );

        Pair<String, String> tokenPair = authService.generateTokenPair(signupRequest.username());

        Set<String> roles = savedUserDto.roles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new JwtResponse(
                tokenPair.getFirst(), // refresh token
                tokenPair.getSecond(), // access token
                savedUserDto.id(),
                savedUserDto.username(),
                roles
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Pair<String, String> tokenPair = authService.generateTokenPair(loginRequest.username());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new JwtResponse(
                tokenPair.getFirst(), // refresh token
                tokenPair.getSecond(), // access token
                userDetails.getId(),
                userDetails.getUsername(),
                roles
        ));
    }

}
