package com.tacs2022.wordlehelper.security.jwt;

import com.tacs2022.wordlehelper.domain.user.User;
import io.jsonwebtoken.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.tacs2022.wordlehelper.utils.Constants.*;

public class TokenProvider {
    public static String generateToken(User usuario) {
        // Generates token with roles, issuer, date, expiration (8h)
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        return Jwts.builder()
                .setSubject(usuario.getUsername())
                .setId(String.valueOf(usuario.getId()))
                .claim(AUTHORITIES_KEY, grantedAuthorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .signWith(SignatureAlgorithm.HS256, SIGNING_KEY)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setIssuer(ISSUER_TOKEN)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS*1000))
                .compact();

    }

    public static String getId(final String token) throws io.jsonwebtoken.ExpiredJwtException, io.jsonwebtoken.UnsupportedJwtException, io.jsonwebtoken.MalformedJwtException, io.jsonwebtoken.SignatureException, IllegalArgumentException {
        final JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);
        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token.replace(TOKEN_BEARER_PREFIX + " ", ""));
        return claimsJws.getBody().getId();
    }
}