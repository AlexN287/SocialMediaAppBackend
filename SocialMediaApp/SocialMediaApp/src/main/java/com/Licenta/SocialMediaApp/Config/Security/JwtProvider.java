package com.Licenta.SocialMediaApp.Config.Security;

import com.Licenta.SocialMediaApp.Exceptions.CustomException;
import com.Licenta.SocialMediaApp.Model.Authentication.JwtConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JwtProvider {
    private static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    private final UserDetailsService customUserDetailsService;

    public JwtProvider(UserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

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

    public boolean validateToken(String token) {

        try {
            String username = getUsernameFromJwtToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException("JWT token is expired", HttpStatus.UNAUTHORIZED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException("Invalid JWT token", HttpStatus.BAD_REQUEST);
        }
    }

    public Authentication getAuthentication(String token) {
        /*String username = getUsernameFromJwtToken(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());*/

        String username = JwtProvider.getUsernameFromJwtToken(token);

        List<GrantedAuthority> authorities = new ArrayList<>();

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,authorities);

        return authentication;
    }

}
