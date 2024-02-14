package com.Licenta.SocialMediaApp.Config.Security;

import com.Licenta.SocialMediaApp.Model.Authentication.JwtConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtProvider {
    private static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    public static String generateToken(Authentication auth)
    {
        String jwt = Jwts.builder()
                .setIssuer("SocialMediaApp").setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+86400000))
                .claim("username", auth.getName())
                .signWith(key)
                .compact();

        return jwt;
    }

    public static String generateToken(String username)
    {
        String jwt = Jwts.builder()
                .setIssuer("SocialMediaApp").setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+86400000))
                .claim("username", username)
                .signWith(key)
                .compact();

        return jwt;
    }
    public static String getUsernameFromJwtToken(String jwt){
        jwt = jwt.substring(7);

        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

        String username = String.valueOf(claims.get("username"));

        return username;
    }
}
