package com.sip.psm.controller;

import com.sip.psm.dto.AuthResponse;
import com.sip.psm.dto.LoginRequest;
import com.sip.psm.dto.RegisterRequest;
import com.sip.psm.entity.User;
import com.sip.psm.repository.UserRepository;
import com.sip.psm.security.JwtUtil;
import com.sip.psm.service.EmailService;
import com.sip.psm.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          UserService userService,
                          JwtUtil jwtUtil,
                          EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));

        String token = jwtUtil.generateToken(user.getEmail());
        var roles = user.getRoles().stream().map(r -> r.getLibelle()).toList();

        return new AuthResponse(token, user.getEmail(), user.getNom(), user.getPrenom(), roles);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@RequestBody RegisterRequest request) {
        User user = new User(request.email(), request.password(), request.tel(), request.nom(), request.prenom());
        User saved = userService.register(user);
        emailService.notifyAdminNewRegistration(saved);
        return Map.of("message", "Inscription réussie");
    }
}