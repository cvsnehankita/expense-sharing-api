package com.expense.api.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtTestGenerator {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationMs = 24 * 60 * 60 * 1000; // 24 hours
    public static void main(String[] args) {
        JwtService jwtService = new JwtService();


        // Replace with any username/email you want to test
        String testUsername = "admin@example.com";

        // Generate token directly
       // String token = jwtService.generateTokenResponse(testUsername).getToken();

       // System.out.println("JWT Token for testing:");
        //System.out.println(token);
    }

}
