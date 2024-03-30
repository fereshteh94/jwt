package com.fhy.jwt.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRET_KEY="4oqG03x3T`7fUHw'%:MbOmM.5u4BK$0K_'&!![_\"V60#TQ09jm.HfT=OACX.Zo;";
    public String extractUserName(String token) {

        return extractClaim(token,Claims::getSubject);
    }

public <T> T extractClaim(String token, Function<Claims,T> claimsResolver)
{
    final Claims claims= extractAllClaims(token);
    return  claimsResolver.apply(claims);
}

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJwt(token)
                .getBody();
    }

    //generate jwt token


    //if we want to generate token just from userDetail we use this method
    public String generateToken(UserDetails userDetails)
    {
   return  generateToken(new HashMap<>(),userDetails);
    }

    //if we want to generate token from claims and userDetail we use this method
    public String generateToken(Map<String,Object> extraClaims,UserDetails userDetails)
    {
        return  Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();

    }


    //validate token

    public boolean isValidateToken(String token,UserDetails userDetails)
    {
      final String username=extractUserName(token);
      return  (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    private Key getSignInKey() {
      byte[] keyBytes= Decoders.BASE64.decode(SECRET_KEY);
      return Keys.hmacShaKeyFor(keyBytes);
    }
}
