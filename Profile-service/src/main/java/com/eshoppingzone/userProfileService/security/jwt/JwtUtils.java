package com.eshoppingzone.userProfileService.security.jwt;

import com.eshoppingzone.userProfileService.entity.Users;
import com.eshoppingzone.userProfileService.repository.UserRepository;
import com.eshoppingzone.userProfileService.security.oauth2.OAuth2UserPrincipal;
import com.eshoppingzone.userProfileService.security.services.UserDetailsImpl;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtUtils {
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Get the user from the repository
            Users user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            return generateToken(user);
        } else if (authentication.getPrincipal() instanceof OAuth2UserPrincipal) {
            OAuth2UserPrincipal userPrincipal = (OAuth2UserPrincipal) authentication.getPrincipal();
            return generateToken(userPrincipal.getUsers());
        } else {
            throw new IllegalArgumentException("Unsupported principal type");
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Get username from JWT token
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            logger.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Generate a JWT token for a User
     */
    public String generateToken(Users user) {
        //Get current time for token issuance
        long now = System.currentTimeMillis();
        
        //Create claims for the token
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        
        //Add roles to the token
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            claims.put("roles", user.getRoles());
        }
        
        // Build and sign the JWT token
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtExpirationMs)) //Use configured expiration time
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}