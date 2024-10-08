package com.example.demo.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    //getting secret from 'application.properties' file
    @Value("${app.secret}")
    private String jwtSecret;

    //getting token expiration time from 'application.properties' file
    @Value("${app.jwtExpiration}")
    private int jwtExpiration;

    //Generating a key
    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    //Generate jwt token
    public String generateJwtToken(Authentication authentication){

        UserDetails userDetails=(UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+jwtExpiration))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Validating the token
    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        }catch (MalformedJwtException e){
            System.err.println("Invalid Token");
        }catch (ExpiredJwtException e){
            System.err.println("Expired Token");
        }catch (UnsupportedJwtException e){
            System.err.println("Unsupported Token");
        }catch (IllegalArgumentException e){
            System.err.println("Token Blank");
        }

        return false;
    }

    //Get user name from generated Token
    public String getUsernameFromJwtToken(String authToken){
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken).getBody().getSubject();
    }

}
