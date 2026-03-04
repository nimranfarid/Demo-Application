package com.example.LdapSample.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.LdapSample.security.SecurityConfig;

@RestController
@RequestMapping("/auth")
public class LdapController {

    public final AuthenticationManager authenticationManager;
    private final SecurityConfig securityConfig;

    public LdapController(AuthenticationManager authenticationManager, SecurityConfig securityConfig) {
        this.authenticationManager = authenticationManager;
        this.securityConfig = securityConfig;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        return securityConfig.generateToken(auth); // Return JWT token
    }
}
