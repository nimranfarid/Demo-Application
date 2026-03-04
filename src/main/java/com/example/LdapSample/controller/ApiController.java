package com.example.LdapSample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
	@GetMapping("/secure-data")
    public String getSecureData() {
        return "This is secured data, accessible with a valid token.";
    }
}
