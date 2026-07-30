package com.samuel.docuchat;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final OrgRepository orgRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, OrgRepository orgRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.orgRepository = orgRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        if (userRepository.findByEmail(req.email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }

        Org org = orgRepository.save(new Org(req.orgName));
        String hashed = passwordEncoder.encode(req.password);
        User user = userRepository.save(new User(req.email, hashed, org));

        String token = jwtService.generateToken(user.getId(), user.getEmail(), org.getId());
        return ResponseEntity.ok(Map.of("token", token, "email", user.getEmail(), "orgName", org.getName()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        var userOpt = userRepository.findByEmail(req.email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(req.password, userOpt.get().getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        User user = userOpt.get();
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getOrg().getId());
        return ResponseEntity.ok(Map.of("token", token, "email", user.getEmail()));
    }
}